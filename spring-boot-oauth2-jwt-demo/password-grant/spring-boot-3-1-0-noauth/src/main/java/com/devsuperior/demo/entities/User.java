package com.devsuperior.demo.entities;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_user")
//public class User {
public class User implements UserDetails { // utiliando a interface do spring security

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String email;
    private String password;

	//@ManyToMany(fetch = FetchType.EAGER) // EAGER para o JPA retornar a lista de roles na busca do User
    @ManyToMany // desta forma a lista de roles não é tornada, a busca é LAZY
	@JoinTable(name = "tb_user_role",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

    public User() {
    }

    public User(Long id, String name, String email, String phone, LocalDate birthDate, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addRole(Role role) {

    	roles.add(role);
    }

    public boolean hasRole(String roleName) {

    	for (Role role : roles) {

    		if(role.getAuthority().equals(roleName)) {

    			return true;
    		}
		}

    	return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles; // a lista de Authorities, é o próprio roles que implementa GrantedAuthority
	}

	@Override
	public String getUsername() {
		return email; // utilizaremos o e-mail como sendo o username para autenticação
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // retorna true fixo porque nesta demonstração estes métodos não serão implementados
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // retorna true fixo porque nesta demonstração estes métodos não serão implementados
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // retorna true fixo porque nesta demonstração estes métodos não serão implementados
	}

	@Override
	public boolean isEnabled() {
		return true; // retorna true fixo porque nesta demonstração estes métodos não serão implementados 
	}
}
