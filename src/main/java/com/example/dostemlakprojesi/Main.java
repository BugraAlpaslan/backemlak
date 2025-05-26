// src/main/java/com/example/dostemlakprojesi/Main.java
package com.example.dostemlakprojesi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // Eski JavaFX kodunu kaldırdık, Spring Boot başlatıyoruz
        SpringApplication.run(Main.class, args);
        
        System.out.println("🚀 DOSTemlak Backend Server başlatıldı!");
        System.out.println("📍 API: http://localhost:8080/api");
        System.out.println("📊 H2 Console: http://localhost:8080/h2-console");
        System.out.println("🎯 Test endpoint: http://localhost:8080/api/listings");
    }
}