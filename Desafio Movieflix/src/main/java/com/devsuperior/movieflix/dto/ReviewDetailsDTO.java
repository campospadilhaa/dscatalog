package com.devsuperior.movieflix.dto;

public class ReviewDetailsDTO {

    private Long id;
    private String text;
    private String name;

	public ReviewDetailsDTO() {
		
	}

	public ReviewDetailsDTO(Long id, String text, String name) {

		this.id = id;
		this.text = text;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}