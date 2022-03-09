package com.example.restservice;

import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
import org.json.JSONArray;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class NFTContractPriceEvalController {

    ArrayList<String> etherscanAPIs = new ArrayList<>(Arrays.asList(
            "EIVSSQEKN35MWD5KAQ12BP6S298HF582GQ",
            "VEE8T7JAVIKUEB3158PBZ77Z6BRMXEWCWZ",
            "UR5ZPPD6W23D7RSS9SA37QRIQ9QGN2Z8AV",
            "QIKA37R844CPB6NN11J63JP4TQ3Q2VRVBS",
            "E68X9DTD13FUWTG9FIXEGZ1N4INZ4HRKRD",
            "N2DR72B2BGW3XAQPR13S4PMMKQAJZ8PN1C"));
    int apiCalls=0;

    ArrayList<String> polygonMumbaiScanAPIs = new ArrayList<>(Arrays.asList(
            "A8PSCMARIK7ZGWJVUH8F1AW9DE4RA2E2WW"));


    @GetMapping("/polygon")
    public NFTContractPricePolygon polygonMumbai(@RequestParam(value="contractAddress", defaultValue = "0xa4c186cc72dd4eabc8390bbadc787ee20c6c18ce")String contractAddress){


        String tokenName="";
        RestTemplate restTemplate = new RestTemplate();
        String mumbaiScanAPI = polygonMumbaiScanAPIs.get((apiCalls++)%(polygonMumbaiScanAPIs.size()));
        String result = restTemplate.getForObject("https://mumbai.polygonscan.com/api?module=token&action=tokeninfo&contractaddress="+ contractAddress +"&apikey="  + mumbaiScanAPI,
                String.class);

        try{
            JSONObject jsonRoot = new JSONObject(result);
            JSONArray array = new JSONArray(jsonRoot.getString("result"));
            JSONObject object = array.getJSONObject(0);
            tokenName = object.getString("tokenName");
        }catch (Exception e)
        {
            System.out.println(e);
        }

        tokenName = tokenName.toLowerCase();
        tokenName = tokenName.replaceAll(" ","-");

        String openSeaResult = restTemplate.getForObject("https://testnets-api.opensea.io/api/v1/collection/" + tokenName + "/stats",
                String.class);

        String numberOfNFT="";
        String averagePrice="";

        try{
            JSONObject jsonRoot = new JSONObject(openSeaResult);
            //System.out.println(jsonRoot.getString("stats"));
            JSONObject res = new JSONObject(jsonRoot.getString("stats"));
            numberOfNFT = res.getString("total_sales");
            averagePrice = res.getString("average_price");
        }catch (Exception e)
        {
            System.out.println(e);
        }

        System.out.println(openSeaResult);
        
        return new NFTContractPricePolygon(numberOfNFT,averagePrice);
    }


    @GetMapping("/nft")
    public NFTContractPriceEval greeting(@RequestParam(value="contractAddress", defaultValue = "0xf42cdDB08BF80E8701f4b58C49789ddf031926e6")String contractAddress)  {

        int maxEvaluation = 30;
        int numberOfAPICallsPerSecond = 1;
        int numberOfTransferTransactions = 0;

        ArrayList<String> transactionHashes = getTransactionHashes(contractAddress);
        //System.out.println(transactionHashes);

        BigInteger total = new BigInteger("0");

        for(String txHash : transactionHashes)
        {
            BigInteger bi = getValueFromTransaction(txHash);
            if(!bi.toString().equals("0"))
            {
                System.out.println(bi.toString());
                total = total.add(bi);
                numberOfTransferTransactions++;
            }

            numberOfAPICallsPerSecond++;
            maxEvaluation--;

            if(maxEvaluation <= 0)
            {
                continue;
            }

            try
            {
                if(numberOfAPICallsPerSecond == 5)
                {
                    Thread.sleep(1000);
                    numberOfAPICallsPerSecond = 0;
                }
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
        }


        BigInteger finalPrice = total.divide(new BigInteger(String.valueOf(numberOfTransferTransactions)));
        return new NFTContractPriceEval(numberOfTransferTransactions,finalPrice);
    }

    @GetMapping("/tx")
    public NFTContractPriceEval tx(@RequestParam(value="txHash", defaultValue = "0x000d732e80bf64641f862b89e98c00c28b496e20c320d5c1bfba620c53652ad2")String transactionHash)
    {
        ArrayList<String> values = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        String ethScanAPI = etherscanAPIs.get((apiCalls++)%(etherscanAPIs.size()));
        String txString = "https://api-rinkeby.etherscan.io/api?module=proxy&action=eth_getTransactionByHash&txhash="+ transactionHash + "&apikey=" + ethScanAPI;
        BigInteger bigInt = null;
        String result = restTemplate.getForObject(txString
                ,
                String.class);

        try{
            JSONObject jsonRoot = new JSONObject(result);
            JSONObject res = new JSONObject(jsonRoot.getString("result"));
            values.add(res.getString("value"));
            bigInt = new BigInteger(res.getString("value").substring(2) , 16);
        }catch (Exception e)
        {
            System.out.println(e);
        }

        System.out.println(bigInt);
        return new NFTContractPriceEval(5,new BigInteger("0"));
    }

    public ArrayList<String> getTransactionHashes(String contractAddress)
    {
        ArrayList<String> transactionHashes = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String ethScanAPI = etherscanAPIs.get((apiCalls++)%(etherscanAPIs.size()));
        String result = restTemplate.getForObject("https://api-rinkeby.etherscan.io/api?module=account&action=txlistinternal&address="+ contractAddress +"&startblock=0&endblock=99999999&sort=desc&apikey="  + ethScanAPI,
                String.class);

        try{
            JSONObject jsonRoot = new JSONObject(result);
            JSONArray array = new JSONArray(jsonRoot.getString("result"));
            for(int i=0; i < array.length(); i++)
            {
                JSONObject object = array.getJSONObject(i);
                transactionHashes.add(object.getString("hash"));
                System.out.println(object.getString("hash"));
            }
        }catch (Exception e)
        {
            System.out.println(e);
        }
        return transactionHashes;
    }

    public BigInteger getValueFromTransaction(String transactionHash)
    {
        RestTemplate restTemplate = new RestTemplate();
        String ethScanAPI = etherscanAPIs.get((apiCalls++)%(etherscanAPIs.size()));
        String txString = "https://api-rinkeby.etherscan.io/api?module=proxy&action=eth_getTransactionByHash&txhash="+ transactionHash + "&apikey=" + ethScanAPI;
        BigInteger bigInt = null;
        String result = restTemplate.getForObject(txString
                ,
                String.class);

        try{
            JSONObject jsonRoot = new JSONObject(result);
            JSONObject res = new JSONObject(jsonRoot.getString("result"));
            bigInt = new BigInteger(res.getString("value").substring(2) , 16);
        }catch (Exception e)
        {
            System.out.println(e);
        }
        return bigInt;
    }
}



