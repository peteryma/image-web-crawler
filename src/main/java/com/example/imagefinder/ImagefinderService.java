package com.example.imagefinder;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.imagefinder.webcrawler.WebCrawler;
import com.example.imagefinder.webcrawler.ImageRecognizer;

@Service
public class ImagefinderService {
    
    private final ImagefinderRepository imagefinderRepository;

    @Autowired
    public ImagefinderService(ImagefinderRepository imagefinderRepository) {
        this.imagefinderRepository = imagefinderRepository;
    }

    public List<SearchResult> getResults() {
        List<SearchResult> results = imagefinderRepository.findAllNames();
        Collections.reverse(results); 
        return results;
    }

    public ImageSearchEntity getImage(Long id) {
        return imagefinderRepository.findById(id).orElseThrow(() -> 
                new IllegalStateException("Search result with id " + id + " does not exist"));
    }

    public String[] searchUrl(String url, Integer depth, Boolean imgRec) {
        // crawl the url and get the images
		WebCrawler crawler = new WebCrawler();
        System.out.println("CRAWLING " + url + " with depth " + depth + " image recognition " + (imgRec ? "enabled" : "disabled"));
        crawler.startCrawl(url, depth);
        String[] imageUrls = crawler.getImageUrls();

        // classify faces and vectors in the images
		ImageRecognizer recognizer = new ImageRecognizer();
		ArrayList<String> faceUrls = new ArrayList<String>();
        ArrayList<String> svgUrls = new ArrayList<String>();
        ArrayList<String> restUrls = new ArrayList<String>();

        if (imgRec) {
            System.out.println("Recognizing images for " + url);
		    recognizer.recognizeFaces(imageUrls, faceUrls, svgUrls, restUrls);
        } else {
            for (String imageUrl : imageUrls) {
                restUrls.add(imageUrl);
            }
        }

        ImageSearchEntity imageSearchEntity = new ImageSearchEntity(url, depth, imgRec, imageUrls.length, faceUrls, svgUrls, restUrls);
        imagefinderRepository.save(imageSearchEntity);

        return imageUrls;
    }
}
