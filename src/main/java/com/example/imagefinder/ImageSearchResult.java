package com.example.imagefinder;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.ElementCollection;
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

    @ElementCollection
    private List<String> faceUrls = new ArrayList<String>();
    
    @ElementCollection
    private List<String> nonFaceUrls = new ArrayList<String>();

    @ElementCollection
    private List<String> svgUrls = new ArrayList<String>();

    public ImageSearchResult() {
    }

    public ImageSearchResult(Long id, String url, Integer depth, ArrayList<String> faceUrls, ArrayList<String> nonFaceUrls, ArrayList<String> svgUrls) {
        this.id = id;
        this.url = url;
        this.depth = depth;
        this.faceUrls = faceUrls;
        this.nonFaceUrls = nonFaceUrls;
        this.svgUrls = svgUrls;
    }

    public ImageSearchResult(String url, Integer depth, ArrayList<String> faceUrls, ArrayList<String> nonFaceUrls, ArrayList<String> svgUrls) {
        this.url = url;
        this.depth = depth;
        this.faceUrls = faceUrls;
        this.nonFaceUrls = nonFaceUrls;
        this.svgUrls = svgUrls;
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

    public List<String> getFaceUrls() {
        return faceUrls;
    }

    public void setFaceUrls(ArrayList<String> faceUrls) {
        this.faceUrls = faceUrls;
    }

    public List<String> getNonFaceUrls() {
        return nonFaceUrls;
    }

    public void setNonFaceUrls(ArrayList<String> nonFaceUrls) {
        this.nonFaceUrls = nonFaceUrls;
    }

    public List<String> getSvgUrls() {
        return svgUrls;
    }

    public void setSvgUrls(ArrayList<String> svgUrls) {
        this.svgUrls = svgUrls;
    }
    
    @Override
    public String toString() {
        return "ImageSearchResult{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", depth=" + depth +
                ", faceUrls=" + faceUrls +
                ", nonFaceUrls=" + nonFaceUrls +
                ", svgUrls=" + svgUrls +
                '}';
    }
}
