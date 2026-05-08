package com.devsuperior.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.demo.entities.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}