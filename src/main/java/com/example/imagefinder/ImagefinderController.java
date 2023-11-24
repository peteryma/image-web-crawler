package com.example.imagefinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/imagefinder")
public class ImagefinderController {
    private final ImagefinderService imagefinderService;

    @Autowired
    public ImagefinderController(ImagefinderService imagefinderService) {
        this.imagefinderService = imagefinderService;
    }

    @GetMapping("/searches")
    public List<ImageSearch> getSearches() {
        return imagefinderService.getSearches();
    }

    @GetMapping("/results/{id}")
    public ImageSearchResult getSearchResult(@PathVariable Long id) {
        return imagefinderService.getSearchResult(id);
    }


    @PostMapping
    public String[] searchUrl(@RequestParam("url") String url,
                              @RequestParam("depth") Integer depth) {
        return imagefinderService.searchUrl(url, depth);
    }
}
