package com.example.imagefinder;

public class ImageSearch {

    private String url;
    private Integer depth;

    public ImageSearch() {
    }

    public ImageSearch(String url, Integer depth) {
        this.url = url;
        this.depth = depth;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }
    
    @Override
    public String toString() {
        return "ImageSearchResult{" +
                ", url='" + url + '\'' +
                ", depth=" + depth +
                '}';
    }
}
