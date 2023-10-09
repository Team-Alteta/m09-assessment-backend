package dev.alteta.backendigdbproxy.controllers;

import jdk.jfr.ContentType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/games")
@CrossOrigin
public class IGDBController {

    @PostMapping
    public String getGames(@RequestBody String body) throws IOException {
        String url = "https://api.igdb.com/v4/games";

        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            HttpPost httpPost = new HttpPost(url);

            httpPost.addHeader("Client-ID", System.getenv("Client-ID")); // Should be an environment variable
            httpPost.addHeader("Authorization", System.getenv("Authorization")); // Should be an environment variable

            Gson gson = new Gson();

            // Assuming you have a payload string, replace 'payloadString' with the actual payload
            String payloadString = body;
            byte[] payloadBytes = payloadString.getBytes();

            HttpEntity entity = new ByteArrayEntity(payloadBytes);

            httpPost.setEntity(entity);

            System.out.println("Successfully sent request");

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                // Get the response status code
                int statusCode = response.getStatusLine().getStatusCode();

                // Get the response body
                HttpEntity responseEntity = response.getEntity();
                String responseBody = EntityUtils.toString(responseEntity);

                System.out.println(statusCode);
                // Process the response as needed
                // You can return the response body or perform other operations based on the status code

                return responseBody;
            }
        } finally {
            httpClient.close();
        }
    }
}
