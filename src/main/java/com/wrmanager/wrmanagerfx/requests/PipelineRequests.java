package com.wrmanager.wrmanagerfx.requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrmanager.wrmanagerfx.models.StockDTO;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class PipelineRequests {


      public static final HttpClient httpClient = HttpClientBuilder.create().build();

      private static final String URL= "http://127.0.0.1:8000/";



    public static List<String> getProductFromImage(String path) {
        String url = "http://127.0.0.1:8000/product";
        String requestBody = "{ \"path\": \"" + path + "\" }";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String responseBody = sendPostRequest(httpClient, url, requestBody);
            //System.out.println("response body " + responseBody);
            var responseList = parseProductResponseJson(responseBody);
            //System.out.println(responseList);
            return responseList;

        } catch (IOException e) {
            e.printStackTrace();
        }

      return new ArrayList<>();
    }

    public static StockDTO getStockFromImage(String path) {
        String url = "http://127.0.0.1:8000/stock";
        String requestBody = "{ \"path\": \"" + path + "\" }";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String responseBody = sendPostRequest(httpClient, url, requestBody);
            //System.out.println("response body " + responseBody);
            var responseList = parseStockResponseJson(responseBody);
            //System.out.println(responseList);
            return responseList;

        } catch (IOException e) {
            e.printStackTrace();
        }

      return null;
    }

    private static StockDTO parseStockResponseJson(String responseBody) {


        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(responseBody);

            Integer productId = responseJson.get("product_id").asInt();

            Float ppa = (float) responseJson.get("ppa").asDouble();

            String lot = responseJson.get("lot").asText();


            java.sql.Date date = Date.valueOf(responseJson.get("date").asText());


            var stock = StockDTO.builder().product_id(Long.valueOf(productId)).ppa(ppa).lot(lot).date(date).build();

            System.out.println(stock);

            return stock;

 } catch (IOException e) {
            e.printStackTrace();
        }

        return null;



    }


    private static List<String> parseProductResponseJson(String responseBody) {
        List<String> values = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(responseBody);

            String name = responseJson.get("name").asText();
            String dos = responseJson.get("dos").asText();
            String forme = responseJson.get("forme").asText();


            values.add(name);
            values.add(dos);
            values.add(forme);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return values;
    }










private static String sendPostRequest(CloseableHttpClient httpClient, String url, String requestBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Accept", "application/json");

        StringEntity requestEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        }
    }


}
