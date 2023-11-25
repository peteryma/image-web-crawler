package com.example.imagefinder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagefinderRepository extends JpaRepository<ImageSearchResult, Long>{
    
    @Query("SELECT new com.example.imagefinder.ImageSearch(isr.id, isr.url, isr.depth, isr.imgRec, isr.numImages) FROM ImageSearchResult isr")
    List<ImageSearch> findAllNames();
}
