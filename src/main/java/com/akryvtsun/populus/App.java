package com.akryvtsun.populus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class App {
    private static final String CONSUMER_KEY = "89358-616af8fc8693e51acd35f0fb";
    private static final String REDIRECT_URL = "http://127.0.0.1:7777"; // "https://natribu.org/";

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        System.out.println(">>> Step 0...");
        new Thread(() -> {
            try {
                ServerSocket ss = new ServerSocket(7777);
                Socket s = ss.accept();
                System.out.println("Server socket has received smth on " + s);
                PrintStream ps = new PrintStream(s.getOutputStream());

                File file = new File(
                    App.class.getClassLoader().getResource("index.html").getFile()
                );
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String line;
                while ((line = br.readLine()) != null) {
                    ps.println(line);
                }
                fr.close();
                ps.close();
                //                 s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(2 * 1000);

        // Step 2: Obtain a request token
        System.out.println(">>> Step 2...");
        String code = getRequestToken(CONSUMER_KEY, REDIRECT_URL);
        System.out.println("\tcode=" + code);

        // Step 3: Redirect user to Pocket to continue authorization
        System.out.println(">>> Step 3...");
        Desktop.getDesktop().browse(
            new URI(
                String.format("https://getpocket.com/auth/authorize?request_token=%s&redirect_uri=%s",
                    code, REDIRECT_URL
                )
            )
        );
        Thread.sleep(2 * 1000);

        // Step 5: Convert a request token into a Pocket access token
        System.out.println(">>> Step 4...");
        String accessToken = convertToAccessToken(CONSUMER_KEY, code);
        System.out.println("\taccessToken=" + accessToken);

        // Step 6: Make authenticated requests to Pocket
        System.out.println(">>> Step 5...");
        //Object result = get2FavoriteLinks(CONSUMER_KEY, accessToken);
        while (true) {
            LinkHolder result = getAllLinks(CONSUMER_KEY, accessToken);
            System.out.println("\tObtain " + result.list.size());
            if (result.list.size() == 0)
                break;
            String res = deleteLinks(CONSUMER_KEY, accessToken, result.list.values());
            System.out.println("\t\tDeleted = " + res);
        }
        ;
        System.out.println("THE END!");
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

    private static LinkHolder getAllLinks(String consumerKey, String accessToken) {
        final String uri = "https://getpocket.com/v3/get";

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("X-Accept", "application/x-www-form-urlencoded");
        HttpEntity entity = new HttpEntity(null, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
            // Add query parameter
            .queryParam("consumer_key", consumerKey)
            .queryParam("access_token", accessToken)
            .queryParam("favorite", "0")
            .queryParam("count", "10")
            .queryParam("offset", "0");

        String result = new RestTemplate()
            .postForObject(builder.toUriString(), entity, String.class);

        ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        LinkHolder object = null;
        try {
            object = mapper.readValue(result, LinkHolder.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return object;
    }

    private static String deleteLinks(String consumerKey, String accessToken, Collection<Link> links) {
        List<DeleteAction> actions = new ArrayList<>(links.size());
        for (Link link : links) {
            actions.add(new DeleteAction(link.item_id));
        }
        DeleteAction[] deleteActions = actions.toArray(new DeleteAction[actions.size()]);
        ActionsList list = new ActionsList(deleteActions);

        ObjectMapper mapper = new ObjectMapper();

        String jsonActions = null;
        try {
            jsonActions = mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //////////////

        final String uri = "https://getpocket.com/v3/send";

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("X-Accept", "application/x-www-form-urlencoded");
        HttpEntity entity = new HttpEntity(jsonActions, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
            // Add query parameter
            .queryParam("consumer_key", consumerKey)
            .queryParam("access_token", accessToken);

        String result = new RestTemplate()
            .postForObject(builder.toUriString(), entity, String.class);

        return result;
    }
}
