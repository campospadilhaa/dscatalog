package com.campospadilhaa.dscatalog.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.campospadilhaa.dscatalog.dto.UserDTO;
import com.campospadilhaa.dscatalog.dto.UserInsertDTO;
import com.campospadilhaa.dscatalog.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

	@Autowired
	private UserService userService;

	// alternativo ao método abaixo, parâmetros individuais substituído pelo objetivo Pageable
	/*
	@GetMapping
	public ResponseEntity<Page<UserDTO>> findAll(
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction,
			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy
		){

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);

		//List<UserDTO> list = userService.findAll();
		Page<UserDTO> list = userService.findAllPaged(pageRequest);

		return ResponseEntity.ok().body(list);
	}*/

	// alternativo ao método acima
	@GetMapping
	public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable){

		Page<UserDTO> list = userService.findAllPaged(pageable);

		return ResponseEntity.ok().body(list);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id){

		UserDTO userDTO = userService.findById(id);

		return ResponseEntity.ok().body(userDTO);
	}	

	@PostMapping
	//public ResponseEntity<UserDTO> insert(@RequestBody UserInsertDTO userInsertDTO){
	public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO userInsertDTO){

		UserDTO userDTO = userService.insert(userInsertDTO);

		// para obter o locale do objeto criado para ser retornado no respose
		URI uri = ServletUriComponentsBuilder
						.fromCurrentRequest().path("/{id}")
						.buildAndExpand(userInsertDTO.getId())
						.toUri();

		return ResponseEntity.created(uri).body(userDTO);
	}

	@PutMapping(value = "/{id}")
	//public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO userDTO){
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO){

		userDTO = userService.update(id, userDTO);

		return ResponseEntity.ok().body(userDTO);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){

		userService.delete(id);

		return ResponseEntity.noContent().build();
	}
}