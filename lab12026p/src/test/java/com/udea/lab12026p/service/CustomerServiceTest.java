package com.udea.lab12026p.service;

import com.udea.lab12026p.dto.CustomerDTO;
import com.udea.lab12026p.entity.Customer;
import com.udea.lab12026p.mapper.CustomerMapper;
import com.udea.lab12026p.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("getAllCustomers - Debe retornar lista mapeada cuando existen clientes")
    void getAllCustomers_whenCustomersExist_shouldReturnDTOList() {
        Customer customer1 = new Customer(1L, "123456", "Carlos", "Perez", 1500.0);
        Customer customer2 = new Customer(2L, "654321", "Maria", "Gomez", 2500.0);

        CustomerDTO dto1 = new CustomerDTO();
        dto1.setId(1L);
        dto1.setFirstName("Carlos");

        CustomerDTO dto2 = new CustomerDTO();
        dto2.setId(2L);
        dto2.setFirstName("Maria");

        when(customerRepository.findAll()).thenReturn(List.of(customer1, customer2));
        when(customerMapper.toDTO(customer1)).thenReturn(dto1);
        when(customerMapper.toDTO(customer2)).thenReturn(dto2);

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Carlos", result.get(0).getFirstName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("Maria", result.get(1).getFirstName());

        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, times(1)).toDTO(customer1);
        verify(customerMapper, times(1)).toDTO(customer2);
    }

    @Test
    @DisplayName("getAllCustomers - Debe retornar lista vacía cuando no hay clientes")
    void getAllCustomers_whenNoCustomers_shouldReturnEmptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        List<CustomerDTO> result = customerService.getAllCustomers();

        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findAll();
        verifyNoInteractions(customerMapper);
    }

    @Test
    @DisplayName("getCustomerById - Debe retornar CustomerDTO cuando existe el ID")
    void getCustomerById_whenCustomerExists_shouldReturnDTO() {
        Customer customer = new Customer(1L, "123456", "Carlos", "Perez", 1500.0);
        CustomerDTO dto = new CustomerDTO();
        dto.setId(1L);
        dto.setFirstName("Carlos");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDTO(customer)).thenReturn(dto);

        CustomerDTO result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Carlos", result.getFirstName());

        verify(customerRepository, times(1)).findById(1L);
        verify(customerMapper, times(1)).toDTO(customer);
    }

    @Test
    @DisplayName("getCustomerById - Debe lanzar RuntimeException cuando el cliente no existe")
    void getCustomerById_whenCustomerDoesNotExist_shouldThrowException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> customerService.getCustomerById(99L));

        assertEquals("Cliente no encontrado", exception.getMessage());
        verify(customerRepository, times(1)).findById(99L);
        verifyNoInteractions(customerMapper);
    }

    @Test
    @DisplayName("createCustomer - Debe mapear, guardar y retornar el DTO creado")
    void createCustomer_whenValidDTO_shouldSaveAndReturnDTO() {
        CustomerDTO inputDto = new CustomerDTO();
        inputDto.setFirstName("Andres");
        inputDto.setLastName("Lopez");
        inputDto.setAccountNumber("789012");
        inputDto.setBalance(3000.0);

        Customer entityToSave = new Customer(null, "789012", "Andres", "Lopez", 3000.0);
        Customer savedEntity = new Customer(3L, "789012", "Andres", "Lopez", 3000.0);

        CustomerDTO createdDto = new CustomerDTO();
        createdDto.setId(3L);
        createdDto.setFirstName("Andres");
        createdDto.setLastName("Lopez");
        createdDto.setAccountNumber("789012");
        createdDto.setBalance(3000.0);

        when(customerMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(customerRepository.save(entityToSave)).thenReturn(savedEntity);
        when(customerMapper.toDTO(savedEntity)).thenReturn(createdDto);

        CustomerDTO result = customerService.createCustomer(inputDto);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Andres", result.getFirstName());
        assertEquals(3000.0, result.getBalance());

        verify(customerMapper, times(1)).toEntity(inputDto);
        verify(customerRepository, times(1)).save(entityToSave);
        verify(customerMapper, times(1)).toDTO(savedEntity);
    }
}
