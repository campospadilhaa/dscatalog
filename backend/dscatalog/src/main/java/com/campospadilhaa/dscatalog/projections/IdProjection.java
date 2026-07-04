package com.campospadilhaa.dscatalog.projections;

// tipo 'E' para considerar qualquer tipo de Id
public interface IdProjection<E> {

	E getId();
}