package com.devsuperior.bds02.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.bds02.dto.EventDTO;
import com.devsuperior.bds02.entities.City;
import com.devsuperior.bds02.entities.Event;
import com.devsuperior.bds02.repositories.EventRepository;
import com.devsuperior.bds02.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepository;

	@Transactional
	public EventDTO update(Long id, EventDTO eventDTO) {

		try {

			// instancia um objeto sem ir ao banco de dados
			Event event = eventRepository.getReferenceById(id);
			event.setName(eventDTO.getName());
			event.setDate(eventDTO.getDate());
			event.setUrl(eventDTO.getUrl());
			event.setCity(new City(eventDTO.getCityId(), null));

			event = eventRepository.save(event);

			return new EventDTO(event);

		} catch (EntityNotFoundException e) {

			throw new ResourceNotFoundException("Evento não encontrado: " + id);
		}
	}
}