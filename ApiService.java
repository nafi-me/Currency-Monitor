package com.livefx;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiService {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static double getRate(String from, String to) {
        try {
            String url = "https://open.er-api.com/v6/latest/" + from;
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());

            JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
            return json.getAsJsonObject("rates").get(to).getAsDouble();

        } catch (Exception e) {
            return -1;
        }
    }
}
