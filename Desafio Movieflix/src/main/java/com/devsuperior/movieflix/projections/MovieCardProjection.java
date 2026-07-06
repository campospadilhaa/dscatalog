package com.devsuperior.movieflix.projections;

public interface MovieCardProjection {

	Long getId();
	String getTitle();
	String getSubTitle();
	Integer getYear();
	String getImgUrl();
}