package com.devsuperior.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.demo.entities.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

}