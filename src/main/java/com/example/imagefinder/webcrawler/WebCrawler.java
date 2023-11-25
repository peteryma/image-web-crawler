package com.example.imagefinder.webcrawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
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

    private ConcurrentHashMap<String, Boolean> visitedUrls = new ConcurrentHashMap<>();
    private ConcurrentLinkedQueue<String> imageUrls = new ConcurrentLinkedQueue<>();

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private AtomicInteger activeTaskCount = new AtomicInteger(0);

    private String domainName;
    private BaseRobotRules robotTxtRules;

    private AtomicLong lastAccessTime = new AtomicLong(0);
    private long crawlDelay = 0;

    private List<String> userAgents = Arrays.asList(
        "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36",
        "Mozilla/5.0 (Linux; Android 13; SM-S908B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36",
        "Mozilla/5.0 (iPhone14,3; U; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Mobile/19A346 Safari/602.1",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36 Edge/12.246",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/601.3.9 (KHTML, like Gecko) Version/9.0.2 Safari/601.3.9",
        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:15.0) Gecko/20100101 Firefox/15.0.1",
        "Mozilla/5.0 (PlayStation; PlayStation 5/2.26) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0 Safari/605.1.15"
    );
    private Random random = new Random();

    /**
     * Starts the web crawling process from the specified URL with the given depth.
     *
     * @param url   the URL to start crawling from
     * @param depth the depth of the crawling process
     */
    public void startCrawl(String url, int depth) {
        try {
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
        if (depth < 0 || visitedUrls.containsKey(url) || !robotTxtRules.isAllowed(url)) {
            return;
        }

        visitedUrls.put(url, true);
        activeTaskCount.incrementAndGet();

        threadPool.submit(() -> {
            try {
                enforceCrawlDelay();
                Document doc = Jsoup.connect(url).userAgent(getRandomUserAgent()).get();

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
                    if (subPageUrl != null && !subPageUrl.isEmpty() && !visitedUrls.containsKey(subPageUrl) && subPageUrl.contains(domainName)) {
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
                System.err.println("ERROR: Crawl delay interrupted: " + e.getMessage());
            }
        }
    }

    // format url with https://
    public String formatUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        return url;
    }


    // extract the domain name from the url
    public String getDomainName(String url) {
        try {
            URL netUrl = new URL(url);
            String host = netUrl.getHost();

            return host.startsWith("www.") ? host.substring(4) : host;
        } catch (Exception e) {
            System.err.println("ERROR Unable to extract domain name from URL: " + url);
        }
        return "";
    }

    public BaseRobotRules isAllowedToCrawl(String url) {
        RobotsTxtHandler robotsTxtHandler = new RobotsTxtHandler();
        return robotsTxtHandler.getRulesForDomain(formatUrl(domainName), getRandomUserAgent());
    }

    public String getRandomUserAgent() {
        return userAgents.get(random.nextInt(userAgents.size()));
    }

    public String[] getImageUrls() {
        return imageUrls.toArray(new String[0]);
    }
}