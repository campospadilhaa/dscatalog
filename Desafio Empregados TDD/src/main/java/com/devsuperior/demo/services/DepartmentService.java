package com.devsuperior.demo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.demo.dto.DepartmentDTO;
import com.devsuperior.demo.entities.Department;
import com.devsuperior.demo.repositories.DepartmentRepository;

@Service
public class DepartmentService {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Transactional(readOnly = true)
	public List<DepartmentDTO> findAll(){

		List<Department> listaDepartment = departmentRepository.findAll(Sort.by("name"));

		return listaDepartment.stream()
					.map( department -> new DepartmentDTO(department) ).collect( Collectors.toList() );
	}
}