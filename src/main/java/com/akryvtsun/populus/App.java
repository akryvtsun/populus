package com.akryvtsun.populus;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpHeaders;
import java.util.Arrays;

public class App {
    private static final String CONSUMER_KEY = "89358-746e75c43372415aa37c59bf";
    private static final String REDIRECT_URL = "https://natribu.org/";

    public static void main(String[] args) throws URISyntaxException, IOException {
        // Step 2: Obtain a request token
        String code = getRequestToken();
        // Step 3: Redirect user to Pocket to continue authorization
        Desktop.getDesktop().browse(
            new URI(
                String.format("https://getpocket.com/auth/authorize?request_token=%s&redirect_uri=%s",
                    code, REDIRECT_URL
                )
            )
        );
        // Step 5: Convert a request token into a Pocket access token
        String accessToken = convertToAccessToken(CONSUMER_KEY, code);

        // Step 6: Make authenticated requests to Pocket
        Object result = get2FavoriteLinks(CONSUMER_KEY, accessToken);
        System.out.println(result);
    }

    private static String getRequestToken() {
        final String uri = "https://getpocket.com/v3/oauth/request";

        //       HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

//        RestTemplate restTemplate = new RestTemplate();
//        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
//        ResponseEntity<String> result = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
//        System.out.println(result);
        return "";
    }

    private static String convertToAccessToken(String consumerKey, String code) {
        return null;
    }

    private static Object get2FavoriteLinks(String consumerKey, String accessToken) {
        return null;
    }
}
