package com.example.imagefinder;

import java.util.List;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@Table
public class ImageSearchEntity {
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
    private Boolean imgRec;
    private Integer numImages;

    @ElementCollection
    @Column(length=1023)
    private List<String> faceUrls = new ArrayList<String>();

    @ElementCollection
    @Column(length=1023)
    private List<String> svgUrls = new ArrayList<String>();

    @ElementCollection
    @Column(length=1023)
    private List<String> restUrls = new ArrayList<String>();

    public ImageSearchEntity() {
    }

    public ImageSearchEntity(Long id, String url, Integer depth, Boolean imgRec, Integer numImages, ArrayList<String> faceUrls, ArrayList<String> svgUrls, ArrayList<String> restUrls) {
        this.id = id;
        this.url = url;
        this.depth = depth;
        this.imgRec = imgRec;
        this.numImages = numImages;
        this.faceUrls = faceUrls;
        this.svgUrls = svgUrls;
        this.restUrls = restUrls;
    }

    public ImageSearchEntity(String url, Integer depth, Boolean imgRec, Integer numImages, ArrayList<String> faceUrls, ArrayList<String> svgUrls, ArrayList<String> restUrls) {
        this.url = url;
        this.depth = depth;
        this.imgRec = imgRec;
        this.numImages = numImages;
        this.faceUrls = faceUrls;
        this.svgUrls = svgUrls;
        this.restUrls = restUrls;
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

    public List<String> getFaceUrls() {
        return faceUrls;
    }

    public void setFaceUrls(ArrayList<String> faceUrls) {
        this.faceUrls = faceUrls;
    }

    public List<String> getSvgUrls() {
        return svgUrls;
    }

    public void setSvgUrls(ArrayList<String> svgUrls) {
        this.svgUrls = svgUrls;
    }

    public List<String> getRestUrls() {
        return restUrls;
    }

    public void setRestUrls(ArrayList<String> restUrls) {
        this.restUrls = restUrls;
    }
    
    @Override
    public String toString() {
        return "ImageSearchEntity{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", depth=" + depth +
                ", imgRec=" + imgRec +
                ", numImages=" + numImages +
                ", faceUrls=" + faceUrls +
                ", svgUrls=" + svgUrls +
                ", restUrls=" + restUrls +
                '}';
    }
}
