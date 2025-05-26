// src/main/java/com/example/dostemlakprojesi/IlanRepository.java
package com.example.dostemlakprojesi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IlanRepository extends JpaRepository<Ilan, Long> {
    
    // İsim veya açıklamada arama
    List<Ilan> findByIsmiContainingOrAciklamaContaining(String isim, String aciklama);
    
    // Fiyat aralığında arama
    List<Ilan> findByFiyatBetween(Integer minFiyat, Integer maxFiyat);
    
    // Şehirde arama
    List<Ilan> findByCityContaining(String city);
    
    // Kullanıcıya göre ilanlar
    List<Ilan> findByOwnerId(Integer ownerId);
    
    // Custom query - fiyat ve m2'ye göre
    @Query("SELECT i FROM Ilan i WHERE i.fiyat <= :maxFiyat AND i.m2 >= :minM2")
    List<Ilan> findByFiyatAndM2(@Param("maxFiyat") Integer maxFiyat, @Param("minM2") Integer minM2);
    
    // Oda sayısına göre
    List<Ilan> findByOdaSayisi(Integer odaSayisi);
}