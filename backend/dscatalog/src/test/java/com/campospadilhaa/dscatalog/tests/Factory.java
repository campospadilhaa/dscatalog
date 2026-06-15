package com.campospadilhaa.dscatalog.tests;

import java.time.Instant;

import com.campospadilhaa.dscatalog.dto.ProductDTO;
import com.campospadilhaa.dscatalog.entities.Category;
import com.campospadilhaa.dscatalog.entities.Product;

public class Factory {

	public static Product createdProduct() {

		Product product = new Product(1L, "Phone", "Good Phone", 800.00, "https://img.com/img.png", Instant.parse("2026-04-28T14:53:00Z"));
		product.getCategories().add(createCategory());

		return product;
	}

	public static ProductDTO createdProductDTO() {

		Product product = createdProduct();

		return new ProductDTO(product, product.getCategories());
	}

	public static Category createCategory() {
		return new Category(1L, "Electronics");
	}
}