package com.akryvtsun.populus;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class App {
    private static final String CONSUMER_KEY = "89358-746e75c43372415aa37c59bf";
    private static final String REDIRECT_URL = "https://natribu.org/";

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        // Step 2: Obtain a request token
        String code = getRequestToken(CONSUMER_KEY, REDIRECT_URL);

        // Step 3: Redirect user to Pocket to continue authorization
        Desktop.getDesktop().browse(
            new URI(
                String.format("https://getpocket.com/auth/authorize?request_token=%s&redirect_uri=%s",
                    code, REDIRECT_URL
                )
            )
        );
        Thread.sleep(2*1000);

        // Step 5: Convert a request token into a Pocket access token
        String accessToken = convertToAccessToken(CONSUMER_KEY, code);

        // Step 6: Make authenticated requests to Pocket
        Object result = get2FavoriteLinks(CONSUMER_KEY, accessToken);
        System.out.println(result);
    }

    private static String getRequestToken(String consumerKey, String redirectUrl) {
        final String uri = "https://getpocket.com/v3/oauth/request";

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("X-Accept", "application/x-www-form-urlencoded");
        HttpEntity entity = new HttpEntity(null, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
            // Add query parameter
            .queryParam("consumer_key", consumerKey)
            .queryParam("redirect_uri", redirectUrl);

       String result = new RestTemplate()
            .postForObject(builder.toUriString(), entity, String.class);
        System.out.println(result);
        return result.split("=")[1];
    }

    private static String convertToAccessToken(String consumerKey, String code) {
        final String uri = "https://getpocket.com/v3/oauth/authorize";

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("X-Accept", "application/x-www-form-urlencoded");
        HttpEntity entity = new HttpEntity(null, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
            // Add query parameter
            .queryParam("consumer_key", consumerKey)
            .queryParam("code", code);

        String result = new RestTemplate()
            .postForObject(builder.toUriString(), entity, String.class);
        System.out.println(result);
        return (result.split("&")[0]).split("=")[1];
    }

    private static Object get2FavoriteLinks(String consumerKey, String accessToken) {
        final String uri = "https://getpocket.com/v3/get";

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("X-Accept", "application/x-www-form-urlencoded");
        HttpEntity entity = new HttpEntity(null, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
            // Add query parameter
            .queryParam("consumer_key", consumerKey)
            .queryParam("access_token", accessToken)
            .queryParam("favorite", "1")
            .queryParam("count", "2");

        String result = new RestTemplate()
            .postForObject(builder.toUriString(), entity, String.class);
        return result;
    }
}
