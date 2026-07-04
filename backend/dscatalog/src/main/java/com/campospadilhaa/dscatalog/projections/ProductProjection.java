package com.campospadilhaa.dscatalog.projections;

public interface ProductProjection extends IdProjection<Long> {

	/* removido para herdar de Idprojection
	Long getId();*/
	String getName();
}