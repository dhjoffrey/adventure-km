package com.adventurekm.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class AdventureKmBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdventureKmBackendApplication.class, args);
	}
	
	@RestController
	@RequestMapping("/api")
	public class UserController {

	    @GetMapping("/bienvenue")
	    public String hello() {
	        return "Bienvenue sur Kilomètres d'Aventures ! Votre journal pour documenter vos sorties les plus mémorables.";
	    }
	}

}
