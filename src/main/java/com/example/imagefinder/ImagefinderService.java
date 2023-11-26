package com.example.imagefinder;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.imagefinder.webcrawler.WebCrawler;
import com.example.imagefinder.webcrawler.ImageRecognizer;

@Service
public class ImageFinderService {
    
    private final ImageFinderRepository ImageFinderRepository;

    @Autowired
    public ImageFinderService(ImageFinderRepository ImageFinderRepository) {
        this.ImageFinderRepository = ImageFinderRepository;
    }

    public List<SearchResult> getResults() {
        List<SearchResult> results = ImageFinderRepository.findAllSearchResults();
        Collections.reverse(results);
        
        return results;
    }

    public ImageSearchEntity getImages(Long id) {
        return ImageFinderRepository.findById(id).orElseThrow(() -> 
                new IllegalStateException("Search result with id " + id 
                                        + " does not exist"));
    }

    public String[] searchUrl(String url, Integer depth, Boolean imgRec) {
        // crawl the url and return the images
		WebCrawler crawler = new WebCrawler();
        System.out.println("CRAWLING " + url + " with depth " + depth 
                         + " image recognition " 
                          + (imgRec ? "enabled" : "disabled"));
        crawler.startCrawl(url, depth);
        String[] imageUrls = crawler.getImageUrls();

        // classify images into frontal faces, vectors, and the rest
		ImageRecognizer recognizer = new ImageRecognizer();
		ArrayList<String> faceUrls = new ArrayList<String>();
        ArrayList<String> svgUrls = new ArrayList<String>();
        ArrayList<String> uncategorizedUrls = new ArrayList<String>();

        if (imgRec) {
            System.out.println("Recognizing images for " + url);
		    recognizer.recognizeFaces(imageUrls, faceUrls, svgUrls, 
                                      uncategorizedUrls);
        } else {
            for (String imageUrl : imageUrls) {
                uncategorizedUrls.add(imageUrl);
            }
        }

        // save the processed search result to the database
        ImageSearchEntity imageSearchEntity = new ImageSearchEntity(url, 
                                                depth, imgRec, imageUrls.length, 
                                                faceUrls, svgUrls, 
                                                uncategorizedUrls);
        ImageFinderRepository.save(imageSearchEntity);

        return imageUrls;
    }
}
