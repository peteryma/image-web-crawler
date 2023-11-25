package com.example.imagefinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/imagefinder")
public class ImagefinderController {
    private final ImagefinderService imagefinderService;
    private final AtomicBoolean activeRequest = new AtomicBoolean(false);

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
    public ResponseEntity<String[]> searchUrl(@RequestParam("url") String url,
                              @RequestParam("depth") Integer depth) {
        if (!activeRequest.compareAndSet(false, true)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                 .body(new String[]{"Another request is currently being processed. Please try again later."});
        }

        try {
            return ResponseEntity.ok(imagefinderService.searchUrl(url, depth));
        } finally {
            activeRequest.set(false);
        }
    }
}
