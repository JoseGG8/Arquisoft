package com.udea.lab12026p.mapper;

import com.udea.lab12026p.dto.CustomerDTO;
import com.udea.lab12026p.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    private final CustomerMapper customerMapper = CustomerMapper.INSTANCE;

    @Test
    @DisplayName("toDTO - Debe mapear Customer a CustomerDTO correctamente")
    void toDTO_shouldMapCorrectly() {
        Customer customer = new Customer(1L, "123456", "Carlos", "Perez", 1500.0);

        CustomerDTO dto = customerMapper.toDTO(customer);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("123456", dto.getAccountNumber());
        assertEquals("Carlos", dto.getFirstName());
        assertEquals("Perez", dto.getLastName());
        assertEquals(1500.0, dto.getBalance());
    }

    @Test
    @DisplayName("toDTO - Debe retornar null cuando Customer es null")
    void toDTO_whenNull_shouldReturnNull() {
        CustomerDTO dto = customerMapper.toDTO(null);
        assertNull(dto);
    }

    @Test
    @DisplayName("toEntity - Debe mapear CustomerDTO a Customer correctamente")
    void toEntity_shouldMapCorrectly() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(2L);
        dto.setAccountNumber("654321");
        dto.setFirstName("Maria");
        dto.setLastName("Gomez");
        dto.setBalance(2500.0);

        Customer entity = customerMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("654321", entity.getAccountNumber());
        assertEquals("Maria", entity.getFirstName());
        assertEquals("Gomez", entity.getLastName());
        assertEquals(2500.0, entity.getBalance());
    }

    @Test
    @DisplayName("toEntity - Debe retornar null cuando CustomerDTO es null")
    void toEntity_whenNull_shouldReturnNull() {
        Customer entity = customerMapper.toEntity(null);
        assertNull(entity);
    }
}
