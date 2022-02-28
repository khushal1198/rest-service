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
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class NFTContractPriceEvalController {
    @GetMapping("/nft")
    public NFTContractPriceEval greeting(@RequestParam(value="contractAddress", defaultValue = "0xf42cdDB08BF80E8701f4b58C49789ddf031926e6")String contractAddress)  {

        int maxEvaluation = 30;
        int numberOfAPICallsPerSecond = 1;
        int numberOfTransferTransactions = 0;

        ArrayList<String> transactionHashes = getTransactionHashes(contractAddress);
        System.out.println(transactionHashes);

        System.out.println("Hi 3");

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
        return new NFTContractPriceEval(numberOfTransferTransactions,finalPrice.toString());
    }

    @GetMapping("/tx")
    public NFTContractPriceEval tx(@RequestParam(value="txHash", defaultValue = "0x000d732e80bf64641f862b89e98c00c28b496e20c320d5c1bfba620c53652ad2")String transactionHash)
    {
        ArrayList<String> values = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();
        String txString = "https://api-rinkeby.etherscan.io/api?module=proxy&action=eth_getTransactionByHash&txhash="+ transactionHash + "&apikey=VEE8T7JAVIKUEB3158PBZ77Z6BRMXEWCWZ";
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
        return new NFTContractPriceEval(5,"Hi");
    }

    public ArrayList<String> getTransactionHashes(String contractAddress)
    {
        ArrayList<String> transactionHashes = new ArrayList<>();

        System.out.println("Hi Hi");

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("https://api-rinkeby.etherscan.io/api?module=account&action=txlistinternal&address="+ contractAddress +"&startblock=0&endblock=99999999&sort=desc&apikey=VEE8T7JAVIKUEB3158PBZ77Z6BRMXEWCWZ",
                String.class);

        System.out.println("Hi Hi1 ");

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
        String txString = "https://api-rinkeby.etherscan.io/api?module=proxy&action=eth_getTransactionByHash&txhash="+ transactionHash + "&apikey=UR5ZPPD6W23D7RSS9SA37QRIQ9QGN2Z8AV";
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



