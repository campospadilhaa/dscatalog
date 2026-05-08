package com.devsuperior.bds02.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds02.dto.CityDTO;
import com.devsuperior.bds02.entities.City;
import com.devsuperior.bds02.repositories.CityRepository;
import com.devsuperior.bds02.services.exceptions.DatabaseException;
import com.devsuperior.bds02.services.exceptions.ResourceNotFoundException;

@Service
public class CityService {

	@Autowired
	private CityRepository cityRepository;

	@Transactional(readOnly = true)
	public List<CityDTO> findAll(){

		List<City> listaCity = cityRepository.findAll(Sort.by("name"));

		return listaCity.stream()
					.map( city -> new CityDTO(city) ).collect( Collectors.toList() );
	}

	@Transactional
	public CityDTO insert(CityDTO cityDTO) {

		City city = new City();
		city.setName(cityDTO.getName());

		city = cityRepository.save(city);

		return new CityDTO(city);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {

		if (!cityRepository.existsById(id)) {
			throw new ResourceNotFoundException("Cidade não encontrada");
		}

		try {
			cityRepository.deleteById(id);    		
		}
    	catch (DataIntegrityViolationException e) {
        	throw new DatabaseException("Falha ao excluir a cidade, existem registros relacionados em eventos");
	   	}
	}
}