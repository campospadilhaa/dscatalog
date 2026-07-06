package com.devsuperior.movieflix.dto;

import com.devsuperior.movieflix.projections.MovieCardProjection;

public class MovieCardDTO {

    private Long id;
    private String title;
    private String subTitle;
    private Integer year;
    private String imgUrl;

    public MovieCardDTO(MovieCardProjection movieCardProjection) {

		this.id = movieCardProjection.getId();
		this.title = movieCardProjection.getTitle();
		this.subTitle = movieCardProjection.getSubTitle();
		this.year = movieCardProjection.getYear();
		this.imgUrl = movieCardProjection.getImgUrl();
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
}