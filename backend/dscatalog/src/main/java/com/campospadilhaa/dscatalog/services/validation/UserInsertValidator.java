package com.campospadilhaa.dscatalog.services.validation;

// classe que executa a validação da anotation UserInsertValid

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.campospadilhaa.dscatalog.dto.UserInsertDTO;
import com.campospadilhaa.dscatalog.entities.User;
import com.campospadilhaa.dscatalog.repositories.UserRepository;
import com.campospadilhaa.dscatalog.resources.exceptions.FieldMessage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

	@Autowired
	private UserRepository userRepository;

	@Override
	public void initialize(UserInsertValid ann) {

	}

	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

		List<FieldMessage> list = new ArrayList<>();
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

		User user = userRepository.findByEmail(dto.getEmail());
		if(user!=null) {

			list.add( new FieldMessage("email", "Email já existe") );
		}

		for (FieldMessage e : list) {

			context.disableDefaultConstraintViolation();

			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}

		return list.isEmpty();
	}
}