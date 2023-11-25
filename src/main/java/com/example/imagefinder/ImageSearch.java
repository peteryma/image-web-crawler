package com.example.imagefinder;

public class ImageSearch {

    private Long id;
    private String url;
    private Integer depth;
    private Boolean imgRec;
    private Integer numImages;

    public ImageSearch() {
    }

    public ImageSearch(Long id, String url, Integer depth, Boolean imgRec, Integer numImages) {
        this.id = id;
        this.url = url;
        this.depth = depth;
        this.imgRec = imgRec;
        this.numImages = numImages;
    }

    public Long getId() {
        return this.id;
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

    public Boolean getImgRec() {
        return imgRec;
    }

    public void setImgRec(Boolean imgRec) {
        this.imgRec = imgRec;
    }

    public Integer getNumImages() {
        return numImages;
    }

    public void setNumImages(Integer numImages) {
        this.numImages = numImages;
    }
    
    @Override
    public String toString() {
        return "ImageSearchResult{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", depth=" + depth +
                ", imgRec=" + imgRec +
                ", numImages=" + numImages +
                '}';
    }
}
