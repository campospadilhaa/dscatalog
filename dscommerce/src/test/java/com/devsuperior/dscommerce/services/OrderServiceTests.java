package com.devsuperior.dscommerce.services;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.OrderFactory;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.tests.UserFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

	@InjectMocks
	private OrderService orderService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private AuthService authService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private OrderItemRepository orderItemRepository;

	@Mock
	private UserService userService;

	private Long existingOrderId;
	private Long nonExistingOrderId;

	private long existingProductId;
	private long nonExistingProductId;

	private Order order;
	private OrderDTO orderDTO;

	private User userAdmin;
	private User userClient;

	private Product product;

	@BeforeEach
	void setUp() throws Exception {

		existingOrderId = 1L;
		nonExistingOrderId = 2L;

		existingProductId = 1L;
		nonExistingProductId = 2L;

		userAdmin = UserFactory.createUserAdmin(1L, "Jef");
		userClient = UserFactory.createUserClient(2L, "Bob");

		order = OrderFactory.createOrder(userClient);

		orderDTO = new OrderDTO(order);

		product = ProductFactory.createProduct();

		Mockito.when(orderRepository.findById(existingOrderId)).thenReturn(Optional.of(order));
		Mockito.when(orderRepository.findById(nonExistingOrderId)).thenReturn(Optional.empty());

		Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
		Mockito.when(productRepository.getReferenceById(nonExistingProductId)).thenThrow(EntityNotFoundException.class);

		Mockito.when(orderRepository.save(ArgumentMatchers.any())).thenReturn(order);
		Mockito.when(orderItemRepository.saveAll(ArgumentMatchers.any())).thenReturn(new ArrayList<>(order.getItems()));
	}

	@Test
	public void findByIdDeveRetornaOrderDTOquandoIdExistsAndLogadoComAdmin() {

		Mockito.doNothing().when(authService).validateSelfOrAdmin(ArgumentMatchers.any());

		OrderDTO orderDTOresult = orderService.findById(existingOrderId);

		Assertions.assertNotNull(orderDTOresult);
		Assertions.assertEquals(orderDTOresult.getId(), existingOrderId);
	}

	@Test
	public void findByIdDeveRetornaOrderDTOquandoIdExistsAndLogadoComClient() {

		Mockito.doNothing().when(authService).validateSelfOrAdmin(ArgumentMatchers.any());

		OrderDTO orderDTOresult = orderService.findById(existingOrderId);

		Assertions.assertNotNull(orderDTOresult);
		Assertions.assertEquals(orderDTOresult.getId(), existingOrderId);
	}

	@Test
	public void findByIdDeveRetornaForbiddenExceptionQuandoIdExistsAndLogadoComOutroClient() {

		Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(ArgumentMatchers.any());

		Assertions.assertThrows(ForbiddenException.class, () -> {
			@SuppressWarnings("unused")
			OrderDTO orderDTOresult = orderService.findById(existingOrderId);
		});
	}

	@Test
	public void findByIdDeveRetornaResourceNotFoundExceptionQuandoIdDoesNotExists() {

		Mockito.doNothing().when(authService).validateSelfOrAdmin(ArgumentMatchers.any());

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			OrderDTO orderDTOresult = orderService.findById(nonExistingOrderId);
		});
	}

	@Test
	public void insertDeveRetornarOrderDTOquandoAdminLogado() {

		Mockito.when(userService.authenticated()).thenReturn(userAdmin);

		OrderDTO orderDTOresult = orderService.insert(orderDTO);

		Assertions.assertNotNull(orderDTOresult);
		Assertions.assertEquals(orderDTOresult.getId(), existingOrderId);
	}

	@Test
	public void insertDeveRetornarOrderDTOquandoClientLogado() {

		Mockito.when(userService.authenticated()).thenReturn(userClient);

		OrderDTO orderDTOresult = orderService.insert(orderDTO);

		Assertions.assertNotNull(orderDTOresult);
	}

	@Test
	public void insertDeveRetornarUsernameNotFoundExceptionQuandoUsuarioNaoLogado() {

		Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

		// colocando um usuário "vazio" para simular a situação de usuário não autenticado
		order.setClient(new User());
		orderDTO = new OrderDTO(order);

		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			OrderDTO orderDTOresult = orderService.insert(orderDTO);
		});
	}

	@Test
	public void insertDeveRetornarEntityNotFoundExceptionQuandoProductIdNotExists() {

		Mockito.when(userService.authenticated()).thenReturn(userClient);

		product.setId(nonExistingProductId);

		OrderItem orderItem = new OrderItem(order, product, 2, 10.0);
		order.getItems().add(orderItem);

		orderDTO = new OrderDTO(order);

		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			OrderDTO orderDTOresult = orderService.insert(orderDTO);
		});		
	}
}