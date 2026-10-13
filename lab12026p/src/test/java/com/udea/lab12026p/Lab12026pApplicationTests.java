package com.udea.lab12026p;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.udea.lab12026p.controller.CustomerController;
import com.udea.lab12026p.controller.TransactionController;
import com.udea.lab12026p.dto.CustomerDTO;
import com.udea.lab12026p.dto.TransactionDTO;
import com.udea.lab12026p.service.CustomerService;
import com.udea.lab12026p.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias con JUnit 5 y Mockito para cada endpoint de la API REST.
 * Se utilizan MockMvc en modo standalone para aislar los controladores
 * de la capa de persistencia y base de datos externa.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class Lab12026pApplicationTests {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // =========================================================================
    // PRUEBAS PARA CUSTOMER CONTROLLER (/api/customers)
    // =========================================================================
    @Nested
    @DisplayName("Pruebas Unitarias para CustomerController (/api/customers)")
    @SuppressWarnings("null")
    class CustomerControllerUnitTests {

        @Mock
        private CustomerService customerService;

        @InjectMocks
        private CustomerController customerController;

        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
        }

        private CustomerDTO buildCustomerDTO(Long id, String firstName, String lastName, String accountNumber, Double balance) {
            CustomerDTO dto = new CustomerDTO();
            dto.setId(id);
            dto.setFirstName(firstName);
            dto.setLastName(lastName);
            dto.setAccountNumber(accountNumber);
            dto.setBalance(balance);
            return dto;
        }

        @Test
        @DisplayName("GET /api/customers - Debe retornar lista de clientes con estado 200 OK")
        void getAllCustomers_shouldReturnListOfCustomers() throws Exception {
            CustomerDTO customer1 = buildCustomerDTO(1L, "Carlos", "Perez", "123456", 1500.0);
            CustomerDTO customer2 = buildCustomerDTO(2L, "Maria", "Gomez", "654321", 2500.0);
            when(customerService.getAllCustomers()).thenReturn(List.of(customer1, customer2));

            mockMvc.perform(get("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].firstName", is("Carlos")))
                    .andExpect(jsonPath("$[0].lastName", is("Perez")))
                    .andExpect(jsonPath("$[0].accountNumber", is("123456")))
                    .andExpect(jsonPath("$[0].balance", is(1500.0)))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].firstName", is("Maria")))
                    .andExpect(jsonPath("$[1].accountNumber", is("654321")));

            verify(customerService, times(1)).getAllCustomers();
        }

        @Test
        @DisplayName("GET /api/customers - Debe retornar lista vacía cuando no hay clientes")
        void getAllCustomers_whenEmpty_shouldReturnEmptyList() throws Exception {
            when(customerService.getAllCustomers()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(customerService, times(1)).getAllCustomers();
        }

        @Test
        @DisplayName("GET /api/customers/{id} - Debe retornar un cliente cuando existe el ID")
        void getCustomerById_whenExists_shouldReturnCustomer() throws Exception {
            CustomerDTO customer = buildCustomerDTO(1L, "Carlos", "Perez", "123456", 1500.0);
            when(customerService.getCustomerById(1L)).thenReturn(customer);

            mockMvc.perform(get("/api/customers/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.firstName", is("Carlos")))
                    .andExpect(jsonPath("$.lastName", is("Perez")))
                    .andExpect(jsonPath("$.accountNumber", is("123456")))
                    .andExpect(jsonPath("$.balance", is(1500.0)));

            verify(customerService, times(1)).getCustomerById(1L);
        }

        @Test
        @DisplayName("GET /api/customers/{id} - Debe propagar excepción cuando cliente no existe")
        void getCustomerById_whenNotFound_shouldThrowException() {
            when(customerService.getCustomerById(99L)).thenThrow(new RuntimeException("Cliente no encontrado"));

            assertThrows(Exception.class, () -> mockMvc.perform(get("/api/customers/99")));
            verify(customerService, times(1)).getCustomerById(99L);
        }

        @Test
        @DisplayName("POST /api/customers - Debe crear un cliente exitosamente con balance")
        void createCustomer_whenValid_shouldReturnCreatedCustomer() throws Exception {
            CustomerDTO inputDTO = buildCustomerDTO(null, "Andres", "Lopez", "789012", 3000.0);
            CustomerDTO createdDTO = buildCustomerDTO(3L, "Andres", "Lopez", "789012", 3000.0);

            when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(createdDTO);

            mockMvc.perform(post("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(3)))
                    .andExpect(jsonPath("$.firstName", is("Andres")))
                    .andExpect(jsonPath("$.lastName", is("Lopez")))
                    .andExpect(jsonPath("$.accountNumber", is("789012")))
                    .andExpect(jsonPath("$.balance", is(3000.0)));

            verify(customerService, times(1)).createCustomer(any(CustomerDTO.class));
        }

        @Test
        @DisplayName("POST /api/customers - Debe lanzar IllegalArgumentException si balance es nulo")
        void createCustomer_whenBalanceIsNull_shouldThrowException() {
            CustomerDTO inputDTO = buildCustomerDTO(null, "Andres", "Lopez", "789012", null);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                customerController.createCustomer(inputDTO);
            });

            assertEquals("Balance cannot be null", exception.getMessage());
            verify(customerService, never()).createCustomer(any(CustomerDTO.class));
        }
    }

    // =========================================================================
    // PRUEBAS PARA TRANSACTION CONTROLLER (/api/transactions)
    // =========================================================================
    @Nested
    @DisplayName("Pruebas Unitarias para TransactionController (/api/transactions)")
    @SuppressWarnings("null")
    class TransactionControllerUnitTests {

        @Mock
        private TransactionService transactionService;

        @InjectMocks
        private TransactionController transactionController;

        private MockMvc mockMvc;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
        }

        private TransactionDTO buildTransactionDTO(Long id, String sender, String receiver, Double amount, LocalDateTime timestamp) {
            TransactionDTO dto = new TransactionDTO();
            dto.setId(id);
            dto.setSenderAccountNumber(sender);
            dto.setReceiverAccountNumber(receiver);
            dto.setAmount(amount);
            dto.setTimestamp(timestamp);
            return dto;
        }

        @Test
        @DisplayName("POST /api/transactions - Transferencia exitosa retorna 200 OK y objeto transacción")
        void transferMoney_whenSuccessful_shouldReturnTransaction() throws Exception {
            LocalDateTime now = LocalDateTime.now();
            TransactionDTO inputDTO = buildTransactionDTO(null, "111111", "222222", 500.0, now);
            TransactionDTO savedDTO = buildTransactionDTO(10L, "111111", "222222", 500.0, now);

            when(transactionService.transferMoney(any(TransactionDTO.class))).thenReturn(savedDTO);

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(10)))
                    .andExpect(jsonPath("$.senderAccountNumber", is("111111")))
                    .andExpect(jsonPath("$.receiverAccountNumber", is("222222")))
                    .andExpect(jsonPath("$.amount", is(500.0)));

            verify(transactionService, times(1)).transferMoney(any(TransactionDTO.class));
        }

        @Test
        @DisplayName("POST /api/transactions - Saldo insuficiente retorna 400 Bad Request con mensaje de error")
        void transferMoney_whenInsufficientBalance_shouldReturnBadRequest() throws Exception {
            TransactionDTO inputDTO = buildTransactionDTO(null, "111111", "222222", 999999.0, LocalDateTime.now());

            when(transactionService.transferMoney(any(TransactionDTO.class)))
                    .thenThrow(new IllegalArgumentException("Saldo insuficiente en la cuenta del remitente."));

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Saldo insuficiente en la cuenta del remitente."));

            verify(transactionService, times(1)).transferMoney(any(TransactionDTO.class));
        }

        @Test
        @DisplayName("POST /api/transactions - Cuentas nulas o no existentes retorna 400 Bad Request")
        void transferMoney_whenAccountNotFound_shouldReturnBadRequest() throws Exception {
            TransactionDTO inputDTO = buildTransactionDTO(null, "000000", "222222", 100.0, LocalDateTime.now());

            when(transactionService.transferMoney(any(TransactionDTO.class)))
                    .thenThrow(new IllegalArgumentException("La cuenta del remitente no existe."));

            mockMvc.perform(post("/api/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("La cuenta del remitente no existe."));

            verify(transactionService, times(1)).transferMoney(any(TransactionDTO.class));
        }

        @Test
        @DisplayName("GET /api/transactions/{accountNumber} - Debe retornar lista de transacciones")
        void getTransactionsByAccount_shouldReturnList() throws Exception {
            LocalDateTime now = LocalDateTime.now();
            TransactionDTO tx1 = buildTransactionDTO(1L, "111111", "222222", 100.0, now);
            TransactionDTO tx2 = buildTransactionDTO(2L, "333333", "111111", 200.0, now);

            when(transactionService.getTransactionsForAccount("111111")).thenReturn(List.of(tx1, tx2));

            mockMvc.perform(get("/api/transactions/111111")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].senderAccountNumber", is("111111")))
                    .andExpect(jsonPath("$[0].amount", is(100.0)))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].receiverAccountNumber", is("111111")))
                    .andExpect(jsonPath("$[1].amount", is(200.0)));

            verify(transactionService, times(1)).getTransactionsForAccount("111111");
        }

        @Test
        @DisplayName("GET /api/transactions/{accountNumber} - Debe retornar lista vacía si no hay transacciones")
        void getTransactionsByAccount_whenEmpty_shouldReturnEmptyList() throws Exception {
            when(transactionService.getTransactionsForAccount("999999")).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/transactions/999999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(transactionService, times(1)).getTransactionsForAccount("999999");
        }
    }
}
