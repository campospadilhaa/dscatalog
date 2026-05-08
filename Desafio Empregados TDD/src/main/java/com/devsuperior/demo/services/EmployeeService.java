package com.devsuperior.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.demo.dto.EmployeeDTO;
import com.devsuperior.demo.entities.Department;
import com.devsuperior.demo.entities.Employee;
import com.devsuperior.demo.repositories.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	@Transactional(readOnly = true)
	public Page<EmployeeDTO> findAll(Pageable peageble){

		Page<Employee> listaEmployee = employeeRepository.findAll(peageble);

		return listaEmployee.map( employee -> new EmployeeDTO(employee) );
	}

	@Transactional
	public EmployeeDTO insert(EmployeeDTO employeeDTO) {

		Employee employee = new Employee();
		employee.setName(employeeDTO.getName());
		employee.setEmail(employeeDTO.getEmail());
		employee.setDepartment( new Department(employeeDTO.getDepartmentId(), null));

		employee = employeeRepository.save(employee);

		return new EmployeeDTO(employee);
	}
}