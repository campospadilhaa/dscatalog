package com.campospadilhaa.dscatalog.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.campospadilhaa.dscatalog.projections.IdProjection;

public class Utils {

	// método com objetivo de criar uma nova lista de Product considerando a lista ordenada de listaOrdenada (paginação), baseada na lista desordenada listaDesordenada
	/*
	public static List<Product> relaplace(List<ProductProjection> listaOrdenada, List<Product> listaDesordenada) {

		Map<Long, Product> map = new HashMap<>();

		for (Product product : listaDesordenada) {
			map.put(product.getId(), product);
		}

		List<Product> listaProduct = new ArrayList<>();

		// cria a lista considerando a lista ordenada
		for (ProductProjection productProjection : listaOrdenada) {
			listaProduct.add(map.get(productProjection.getId()));
		}
		
		return listaProduct;
	}*/

	// tornando o método genérico
	public static <ID> List<? extends IdProjection<ID>> relaplace(List<? extends IdProjection<ID>> listaOrdenada, List<? extends IdProjection<ID>> listaDesordenada) {

		Map<ID, IdProjection<ID>> map = new HashMap<>();

		for (IdProjection<ID> product : listaDesordenada) {
			map.put(product.getId(), product);
		}

		List<IdProjection<ID>> listaProduct = new ArrayList<>();

		// cria a lista considerando a lista ordenada
		for (IdProjection<ID> productProjection : listaOrdenada) {
			listaProduct.add(map.get(productProjection.getId()));
		}
		
		return listaProduct;
	}	 
}