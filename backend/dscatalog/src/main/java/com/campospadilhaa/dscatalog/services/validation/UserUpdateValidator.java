package com.campospadilhaa.dscatalog.services.validation;

// classe que executa a validação da anotation UserUpdateValid
// validação da atualização de usuário

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.campospadilhaa.dscatalog.dto.UserUpdateDTO;
import com.campospadilhaa.dscatalog.entities.User;
import com.campospadilhaa.dscatalog.repositories.UserRepository;
import com.campospadilhaa.dscatalog.resources.exceptions.FieldMessage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UserRepository userRepository;

	@Override
	public void initialize(UserUpdateValid ann) {

	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {

		// obtendo os valores da requisição

		@SuppressWarnings("unchecked")
		var uriVars = (Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

		long userId = Long.parseLong( uriVars.get("id") );
		////

		List<FieldMessage> list = new ArrayList<>();
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista

		User user = userRepository.findByEmail(dto.getEmail());
		if(user!=null && userId != user.getId()) {

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