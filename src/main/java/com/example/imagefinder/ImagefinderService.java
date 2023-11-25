package com.example.imagefinder;

import java.util.List;
import java.util.ArrayList;

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

    public List<ImageSearch> getSearches() {
        return imagefinderRepository.findAllNames();
    }

    public ImageSearchResult getSearchResult(Long id) {
        return imagefinderRepository.findById(id).orElseThrow(() -> 
                new IllegalStateException("Search result with id " + id + " does not exist"));
    }

    public String[] searchUrl(String url, Integer depth) {
        // crawl the url and get the images
		WebCrawler crawler = new WebCrawler();
        crawler.startCrawl(url, depth);
        String[] imageUrls = crawler.getImageUrls();

        // classify faces in the images
		ImageRecognizer recognizer = new ImageRecognizer();
		ArrayList<String> faceUrls = new ArrayList<String>();
		ArrayList<String> nonFaceUrls = new ArrayList<String>();
		recognizer.recognizeFaces(imageUrls, faceUrls, nonFaceUrls);

        ImageSearchResult imageSearchResult = new ImageSearchResult(url, depth, faceUrls, nonFaceUrls);
        imagefinderRepository.save(imageSearchResult);

        return imageUrls;
    }
}
