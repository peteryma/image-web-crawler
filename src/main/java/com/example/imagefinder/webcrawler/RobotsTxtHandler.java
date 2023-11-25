package com.example.imagefinder.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;

public class RobotsTxtHandler {
    private SimpleRobotRulesParser robotRulesParser = new SimpleRobotRulesParser();

    public BaseRobotRules getRulesForDomain(String url, String userAgent) {
        String robotsTxtUrl = url + "/robots.txt";
        String content = fetchContent(robotsTxtUrl);
        return robotRulesParser.parseContent(robotsTxtUrl, content.getBytes(), "text/plain", userAgent);
    }

    private String fetchContent(String robotsTxtUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(robotsTxtUrl);
            String host = url.getHost();
            System.out.println("Fetching robots.txt from: " + host);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the connection properties
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // Check for successful response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
            } else {
                System.err.println("ERROR Failed to fetch robots.txt, HTTP error code: " + responseCode);
            }
        } catch (IOException e) {
            System.err.println("ERROR Unable to fetch robots.txt: " + e.getMessage());
        }

        return content.toString();
    }

    // test getting robots.txt
    public static void main(String[] args) {
        RobotsTxtHandler handler = new RobotsTxtHandler();
        BaseRobotRules rules = handler.getRulesForDomain("https://en.wikipedia.org", "MyWebCrawler/1.0 (+http://www.mywebsite.com/crawler)");
        System.out.println(rules);
    }
}
