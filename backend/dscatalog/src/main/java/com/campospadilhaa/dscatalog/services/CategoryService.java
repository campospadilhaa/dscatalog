package com.campospadilhaa.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.campospadilhaa.dscatalog.entities.Category;
import com.campospadilhaa.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	public List<Category> findAll(){

		return repository.findAll();
	}
}