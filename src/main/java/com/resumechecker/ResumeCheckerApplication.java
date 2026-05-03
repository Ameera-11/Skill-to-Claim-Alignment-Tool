package com.resumechecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the AI Resume Integrity Checker.
 * Run this class to start the web server.
 * Then open: http://localhost:8080
 */
@SpringBootApplication
public class ResumeCheckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeCheckerApplication.class, args);
        System.out.println("\n✅ Resume Checker is running!");
        System.out.println("👉 Open your browser: http://localhost:8080\n");
    }
}
