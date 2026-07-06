package com.devsuperior.movieflix.dto;

import java.util.ArrayList;
import java.util.List;

import com.devsuperior.movieflix.entities.Movie;

public class MovieDetailsDTO {

    private Long id;
    private String title;
    private String subTitle;
    private Integer year;
    private String imgUrl;
    private String synopsis;
    private GenreDTO genre;
    private List<ReviewDetailsDTO> listaReviewDetailsDTO = new ArrayList<>();

	public MovieDetailsDTO(Movie movie) {

		this.id = movie.getId();
		this.title = movie.getTitle();
		this.subTitle = movie.getSubTitle();
		this.year = movie.getYear();
		this.imgUrl = movie.getImgUrl();
		this.synopsis = movie.getSynopsis();

		this.genre = new GenreDTO(movie.getGenre());

		if(movie.getReviews()!=null && !movie.getReviews().isEmpty()) {
			movie.getReviews().forEach( review -> this.listaReviewDetailsDTO.add( new ReviewDetailsDTO( review.getId(), review.getText(), review.getUser().getName() ) ) );
		}
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

	public GenreDTO getGenre() {
		return genre;
	}

	public void setGenre(GenreDTO genre) {
		this.genre = genre;
	}

	public List<ReviewDetailsDTO> getListaReviewDetailsDTO() {
		return listaReviewDetailsDTO;
	}

	public void setListaReviewDetailsDTO(List<ReviewDetailsDTO> listaReviewDetailsDTO) {
		this.listaReviewDetailsDTO = listaReviewDetailsDTO;
	}
}