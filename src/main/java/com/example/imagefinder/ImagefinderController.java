package com.example.imagefinder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/imagefinder")
public class ImageFinderController {
    private final ImageFinderService imageFinderService;
    private final AtomicBoolean activeRequest = new AtomicBoolean(false);

    @Autowired
    public ImageFinderController(ImageFinderService imageFinderService) {
        this.imageFinderService = imageFinderService;
    }

    @GetMapping("/results")
    public List<SearchResult> getResults() {
        return imageFinderService.getResults();
    }

    @GetMapping("/images/{id}")
    public ImageSearchEntity getImages(@PathVariable Long id) {
        return imageFinderService.getImages(id);
    }

    @PostMapping
    public ResponseEntity<String[]> searchUrl(@RequestParam("url") String url,
                              @RequestParam("depth") Integer depth, 
                              @RequestParam("imgRec") Boolean imgRec) {
        if (!activeRequest.compareAndSet(false, true)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                 .body(new String[]{"Another request is "
                                                  + "currently being processed." 
                                                  + "Please try again later."});
        }

        try {
            return ResponseEntity.ok(imageFinderService.searchUrl(url, depth, 
                                                                  imgRec));
        } finally {
            activeRequest.set(false);
        }
    }
}
