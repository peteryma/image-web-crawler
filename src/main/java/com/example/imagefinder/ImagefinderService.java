package com.example.imagefinder;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        String[] result = new String[2];
        result[0] = "https://www.google.com/searchbyimage?&image_url=" + url;
        result[1] = "https://yandex.com/images/search?source=collections&url=" + url;

        ArrayList<String> faceUrls = new ArrayList<String>();
		ArrayList<String> nonFaceUrls = new ArrayList<String>();
        faceUrls.add("https://www.google.com/searchbyimage?&image_url=" + url);
        nonFaceUrls.add("https://yandex.com/images/search?source=collections&url=" + url);

        ImageSearchResult imageSearchResult = new ImageSearchResult(url, depth, faceUrls, nonFaceUrls);
        imagefinderRepository.save(imageSearchResult);

        return result;
    }
}
