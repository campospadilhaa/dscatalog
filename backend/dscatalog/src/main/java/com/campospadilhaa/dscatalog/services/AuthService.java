package com.campospadilhaa.dscatalog.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campospadilhaa.dscatalog.dto.EmailDTO;
import com.campospadilhaa.dscatalog.dto.NewPasswordDTO;
import com.campospadilhaa.dscatalog.entities.PasswordRecover;
import com.campospadilhaa.dscatalog.entities.User;
import com.campospadilhaa.dscatalog.repositories.PasswordRecoverRepository;
import com.campospadilhaa.dscatalog.repositories.UserRepository;
import com.campospadilhaa.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class AuthService {

	@Value("${email.password-recover.token.minutes}")
	private Long tokenMinutes;

	@Value("${email.password-recover.uri}")
	private String recoverUri;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordRecoverRepository passwordRecoverRepository;

	@Transactional
	public void createRecoverToken(EmailDTO emailDTO) {

		User user = userRepository.findByEmail(emailDTO.getEmail());

		if(user==null) {
			throw new ResourceNotFoundException("Email não encontrado");
		}

		String token = UUID.randomUUID().toString();

		PasswordRecover passwordRecover = new PasswordRecover();
		passwordRecover.setEmail(emailDTO.getEmail());
		passwordRecover.setToken(token);
		passwordRecover.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));

		passwordRecover = passwordRecoverRepository.save(passwordRecover);

		String body = "Acesso o link para definir uma nova senha\n\n" +
				      recoverUri + token + "\n" +
				      "Validade de " + tokenMinutes + " minutos";

		emailService.sendEmail(emailDTO.getEmail(), "Recuperação de senha", body);
	}

	@Transactional
	public void saveNewPassword(NewPasswordDTO newPasswordDTO) {

		List<PasswordRecover> listaPasswordRecover = passwordRecoverRepository.searchValidTokens(newPasswordDTO.getToken(), Instant.now());

		if(listaPasswordRecover==null || listaPasswordRecover.isEmpty()) {
			throw new ResourceNotFoundException("Token inválido");
		}

		User user = userRepository.findByEmail(listaPasswordRecover.get(0).getEmail());
		user.setPassword(passwordEncoder.encode(newPasswordDTO.getPassword()));

		user = userRepository.save(user);
	}

	protected User authenticated() {

		try {
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		    Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
		    String username = jwtPrincipal.getClaim("username");

		    return userRepository.findByEmail(username);
		} catch (Exception e) {
			throw new UsernameNotFoundException("Usuário inválido");
		}
	}
}