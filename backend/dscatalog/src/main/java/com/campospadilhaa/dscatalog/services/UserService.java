package com.campospadilhaa.dscatalog.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.campospadilhaa.dscatalog.dto.RoleDTO;
import com.campospadilhaa.dscatalog.dto.UserDTO;
import com.campospadilhaa.dscatalog.dto.UserInsertDTO;
import com.campospadilhaa.dscatalog.entities.Role;
import com.campospadilhaa.dscatalog.entities.User;
import com.campospadilhaa.dscatalog.repositories.RoleRepository;
import com.campospadilhaa.dscatalog.repositories.UserRepository;
import com.campospadilhaa.dscatalog.services.exceptions.DatabaseException;
import com.campospadilhaa.dscatalog.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder cryptPasswordEncoder; // classe do tipo @Configuration criada para criptografar a senha

	// alternativo ao método baixo
	public Page<UserDTO> findAllPaged(PageRequest pageRequest){

		Page<User> listUser = userRepository.findAll(pageRequest);

		Page<UserDTO> listUserDTO =
				listUser
					.map( user -> new UserDTO(user));

		return listUserDTO;
	}

	// alternativo ao método acima
	public Page<UserDTO> findAllPaged(Pageable peageble){

		Page<User> listUser = userRepository.findAll(peageble);

		Page<UserDTO> listUserDTO =
				listUser
					.map( user -> new UserDTO(user));

		return listUserDTO;
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id){

		Optional<User> optUser = userRepository.findById(id);

		// User user = optUser.get();

		User user =
				optUser
					.orElseThrow( () -> new ResourceNotFoundException("Usuário não encontrado") );

		UserDTO userDTO = new UserDTO(user);

		return userDTO;
	}

	@Transactional
	/* utilizado o UserInsertDTO, herança de UserDTO para trabalhar a inserção da senha
	public UserDTO insert(UserDTO userDTO) { */
	public UserDTO insert(UserInsertDTO userInsertDTO) {

		User user = new User();
		copyDtoToUser(user, userInsertDTO);

		//user.setPassword(userInsertDTO.getPassowrd());
		user.setPassword( cryptPasswordEncoder.encode( userInsertDTO.getPassword() ));

		user = userRepository.save(user);

		return new UserDTO(user);
	}

	@Transactional
	public UserDTO update(Long id, UserDTO userDTO) {

		try {

			// instancia um objeto sem ir ao banco de dados
			User user = userRepository.getReferenceById(id);
			copyDtoToUser(user, userDTO);

			user = userRepository.save(user);

			return new UserDTO(user);

		} catch (EntityNotFoundException e) {

			throw new ResourceNotFoundException("Categoria não encontrada: " + id);
		}
	}

	private void copyDtoToUser(User user, UserDTO userDTO) {

		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());

		user.getRoles().clear();
		for (RoleDTO roleDTO : userDTO.getRoles()) {

			Role role = roleRepository.getReferenceById(roleDTO.getId());
			user.getRoles().add(role);
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	public void delete(Long id) {

		if (!userRepository.existsById(id)) {
			throw new ResourceNotFoundException("Produto não encontrado");
		}

		try {
			userRepository.deleteById(id);    		
		}
    	catch (DataIntegrityViolationException e) {
        	throw new DatabaseException("Falha ao excluir a categoria, existem registros relacionados");
	   	}
	}
}