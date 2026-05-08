package com.devsuperior.demo.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.devsuperior.demo.dto.EmployeeDTO;
import com.devsuperior.demo.services.EmployeeService;

@RestController
@RequestMapping(value = "/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@GetMapping
	public ResponseEntity<Page<EmployeeDTO>> findAll(Pageable pageable){

		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("name"));

		Page<EmployeeDTO> list = employeeService.findAll(pageRequest);

		return ResponseEntity.ok().body(list);
	}

	@PostMapping
	public ResponseEntity<EmployeeDTO> insert(@RequestBody EmployeeDTO employeeDTO){

		employeeDTO = employeeService.insert(employeeDTO);

		// para obter o locale do objeto criado para ser retornado no respose
		URI uri = ServletUriComponentsBuilder
						.fromCurrentRequest().path("/{id}")
						.buildAndExpand(employeeDTO.getId())
						.toUri();

		return ResponseEntity.created(uri).body(employeeDTO);
	}
}