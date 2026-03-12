package com.adventurekm.backend.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Activity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String titre;
	private LocalDate date;
	private String description;
	
	// Getters et Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getTitre() { return titre; }
	public void setTitre(String titre) { this.titre = titre; }
	
	public LocalDate getDate() { return date; }
	public void setDate(LocalDate date) { this.date = date; }
	
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
}
