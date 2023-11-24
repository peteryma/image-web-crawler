package com.example.imagefinder;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@Table
public class ImageSearchResult {
    @Id
    @SequenceGenerator(
        name = "imagefinder_sequence",
        sequenceName = "imagefinder_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "imagefinder_sequence"
    )

    private Long id;
    private String url;
    private Integer depth;
    private ArrayList<String> faceUrls;
    private ArrayList<String> nonFaceUrls;

    public ImageSearchResult() {
    }

    public ImageSearchResult(Long id, String url, Integer depth, ArrayList<String> faceUrls, ArrayList<String> nonFaceUrls) {
        this.id = id;
        this.url = url;
        this.depth = depth;
        this.faceUrls = faceUrls;
        this.nonFaceUrls = nonFaceUrls;
    }

    public ImageSearchResult(String url, Integer depth, ArrayList<String> faceUrls, ArrayList<String> nonFaceUrls) {
        this.url = url;
        this.depth = depth;
        this.faceUrls = faceUrls;
        this.nonFaceUrls = nonFaceUrls;
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

    public ArrayList<String> getFaceUrls() {
        return faceUrls;
    }

    public void setFaceUrls(ArrayList<String> faceUrls) {
        this.faceUrls = faceUrls;
    }

    public ArrayList<String> getNonFaceUrls() {
        return nonFaceUrls;
    }

    public void setNonFaceUrls(ArrayList<String> nonFaceUrls) {
        this.nonFaceUrls = nonFaceUrls;
    }
    
    @Override
    public String toString() {
        return "ImageSearchResult{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", depth=" + depth +
                ", faceUrls=" + faceUrls +
                ", nonFaceUrls=" + nonFaceUrls +
                '}';
    }
}
