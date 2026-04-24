package com.campospadilhaa.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.campospadilhaa.dscatalog.dto.CategoryDTO;
import com.campospadilhaa.dscatalog.dto.ProductDTO;
import com.campospadilhaa.dscatalog.entities.Category;
import com.campospadilhaa.dscatalog.entities.Product;
import com.campospadilhaa.dscatalog.repositories.CategoryRepository;
import com.campospadilhaa.dscatalog.repositories.ProductRepository;
import com.campospadilhaa.dscatalog.services.exceptions.DatabaseException;
import com.campospadilhaa.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	public Page<ProductDTO> findAllPaged(PageRequest pageRequest){

		Page<Product> listProduct = productRepository.findAll(pageRequest);

		Page<ProductDTO> listProductDTO =
				listProduct
					.map( product -> new ProductDTO(product));

		return listProductDTO;
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id){

		Optional<Product> optProduct = productRepository.findById(id);

		// Product product = optProduct.get();

		Product product =
				optProduct
					.orElseThrow( () -> new ResourceNotFoundException("Produto não encontrado") );

		ProductDTO productDTO = new ProductDTO(product, product.getCategories());

		return productDTO;
	}

	@Transactional
	public ProductDTO insert(ProductDTO productDTO) {

		Product product = new Product();
		copyDtoToProduct(product, productDTO);

		product = productRepository.save(product);

		return new ProductDTO(product);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO productDTO) {

		try {

			// instancia um objeto sem ir ao banco de dados
			Product product = productRepository.getReferenceById(id);
			copyDtoToProduct(product, productDTO);

			product = productRepository.save(product);

			return new ProductDTO(product);

		} catch (EntityNotFoundException e) {

			throw new ResourceNotFoundException("Categoria não encontrada: " + id);
		}
	}

	private void copyDtoToProduct(Product product, ProductDTO productDTO) {

		product.setName(productDTO.getName());
		product.setDescription(productDTO.getDescription());
		product.setPrice(productDTO.getPrice());
		product.setImgUrl(productDTO.getImgUrl());
		product.setDate(productDTO.getDate());

		product.getCategories().clear();
		for (CategoryDTO categoryDTO : productDTO.getCategories()) {

			Category categegory = categoryRepository.getReferenceById(categoryDTO.getId());
			product.getCategories().add(categegory);
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {

		if (!productRepository.existsById(id)) {
			throw new ResourceNotFoundException("Categoria não encontrada");
		}

		try {
			productRepository.deleteById(id);    		
		}
    	catch (DataIntegrityViolationException e) {
        	throw new DatabaseException("Falha ao excluir a categoria, existem registros relacionados");
	   	}
	}
}