package com.example.imagefinder.webcrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawlercommons.robots.BaseRobotRules;

public class WebCrawler {

    private String resourcesPath = System.getProperty("user.dir") 
                        + "/src/main/java/com/example/imagefinder/webcrawler";

    private ConcurrentHashMap<String, Boolean> visitedUrls = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<String> imageUrls = new ConcurrentLinkedQueue<>();

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private AtomicInteger activeTaskCount = new AtomicInteger(0);

    private String domainName;
    private List<String> userAgents;
    private BaseRobotRules robotTxtRules;
    private AtomicLong lastAccessTime = new AtomicLong(0);
    private long crawlDelay = 0;

    // read in file and store each line in a list of strings
    private Random random = new Random();

    public void startCrawl(String url, int depth) {
        try {
            initializeUserAgents();
            url = formatUrl(url);
            domainName = getDomainName(url);
            robotTxtRules = isAllowedToCrawl(url);
            crawlDelay = Math.max(robotTxtRules.getCrawlDelay(), 0);

            crawlPage(url, depth);

            // Wait for all tasks to complete
            while (activeTaskCount.get() > 0) {
                Thread.sleep(100);
            }

            threadPool.shutdown();
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }

        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        } catch (Exception e) {
            System.err.println("ERROR Unable to crawl URL: " + url);
        }
    }

    private void crawlPage(String url, int depth) {
        if (depth < 0 || visitedUrls.containsKey(url) || 
            !robotTxtRules.isAllowed(url)) {
            return;
        }

        visitedUrls.put(url, true);
        activeTaskCount.incrementAndGet();

        // start a new thread to crawl the page
        threadPool.submit(() -> {
            try {
                enforceCrawlDelay();

                Document doc = Jsoup.connect(url)
                                    .userAgent(getRandomUserAgent()).get();

                // Get all the images in the page
                Elements images = doc.select("img");
                for (Element img : images) {
                    String src = img.absUrl("src");

                    if (src != null && !src.isEmpty()) {
                        imageUrls.add(src);
                    }
                }

                lastAccessTime.set(System.currentTimeMillis());

                // Recursively crawl the links in the page
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String subPageUrl = link.absUrl("href");

                    if (subPageUrl != null && !subPageUrl.isEmpty() && 
                        !visitedUrls.containsKey(subPageUrl) && 
                        subPageUrl.contains(domainName)) {
                        crawlPage(subPageUrl, depth - 1);
                    }
                }

            } catch (MalformedURLException e) {
                System.err.println("ERROR Invalid URL format: " + url);
            } catch (IOException e) {
                System.err.println("ERROR Unable to access URL: " + url);
            } catch (Exception e) {
                System.err.println("ERROR Unable to process URL: " + url);
            } finally {
                activeTaskCount.decrementAndGet();
            }
        });
    }

    private synchronized void enforceCrawlDelay() {
        long nextAccessTime = lastAccessTime.get() + crawlDelay * 1000L;
        long currentTime = System.currentTimeMillis();

        if (nextAccessTime > currentTime) {
            try {
                Thread.sleep(nextAccessTime - currentTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("ERROR: Crawl delay interrupted: " 
                                    + e.getMessage());
            }
        }
    }

    // format url by prepending with https://
    private String formatUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        return url;
    }

    // extract the domain name from the url
    private String getDomainName(String url) {
        try {
            URL netUrl = new URL(url);
            String host = netUrl.getHost();

            return host.startsWith("www.") ? host.substring(4) : host;

        } catch (Exception e) {
            System.err.println("ERROR Unable to extract domain name from URL: " 
                                + url);
        }
        return "";
    }

    private BaseRobotRules isAllowedToCrawl(String url) {
        RobotsTxtHandler robotsTxtHandler = new RobotsTxtHandler();

        return robotsTxtHandler.getRulesForDomain(formatUrl(domainName), 
                                                  getRandomUserAgent());
    }

    private void initializeUserAgents() {
        this.userAgents = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(
                                    new FileReader(resourcesPath 
                                    + "/user-agents.txt"));

            String userAgent;
            while ((userAgent = reader.readLine()) != null) {
                this.userAgents.add(userAgent);
            }

            reader.close();
        } catch (IOException e) {
            System.err.println("ERROR Unable to read user-agents.txt");
        }
    }

    private String getRandomUserAgent() {
        return userAgents.get(random.nextInt(userAgents.size()));
    }

    public String[] getImageUrls() {
        return imageUrls.toArray(new String[0]);
    }
}