package com.resumechecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MAIN ENTRY POINT
 * Run this class to start the backend server.
 * Then open: http://localhost:8080
 */
@SpringBootApplication
public class ResumeCheckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeCheckerApplication.class, args);
        System.out.println("\n✅ Backend is running!");
        System.out.println("👉 API ready at: http://localhost:8080/api/analyze\n");
    }
}
