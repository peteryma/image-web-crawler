package com.example.imagefinder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImagefinderRepository extends JpaRepository<ImageSearchEntity, Long>{
    
    @Query("SELECT new com.example.imagefinder.SearchResult(isr.id, isr.url, isr.depth, isr.imgRec, isr.numImages) FROM ImageSearchEntity isr")
    List<SearchResult> findAllNames();
}
 