// src/main/java/com/example/dostemlakprojesi/KullaniciRepository.java
package com.example.dostemlakprojesi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KullaniciRepository extends JpaRepository<KullaniciNode, Long> {
    
    // Kullanıcı adına göre bul
    Optional<KullaniciNode> findByKullaniciAdi(String kullaniciAdi);
    
    // Kullanıcı adı var mı kontrol et
    boolean existsByKullaniciAdi(String kullaniciAdi);
    
    // Kullanıcı adı ve şifre ile giriş
    Optional<KullaniciNode> findByKullaniciAdiAndSifre(String kullaniciAdi, Integer sifre);
}