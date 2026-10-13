package com.udea.lab12026p.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    @DisplayName("CustomerDTO - Debe instanciar y probar todos los constructores, getters y setters")
    void testCustomerDTO() {
        CustomerDTO dto1 = new CustomerDTO();
        dto1.setId(1L);
        dto1.setFirstName("Juan");
        dto1.setLastName("Perez");
        dto1.setAccountNumber("123456");
        dto1.setBalance(1500.0);

        assertEquals(1L, dto1.getId());
        assertEquals("Juan", dto1.getFirstName());
        assertEquals("Perez", dto1.getLastName());
        assertEquals("123456", dto1.getAccountNumber());
        assertEquals(1500.0, dto1.getBalance());

        // Constructor con parámetros
        CustomerDTO dto2 = new CustomerDTO(2L, "Maria", "Gomez", "654321", 2000.0);
        assertNotNull(dto2);
    }

    @Test
    @DisplayName("TransactionDTO - Debe instanciar y probar todos los constructores, getters y setters")
    void testTransactionDTO() {
        LocalDateTime now = LocalDateTime.now();
        TransactionDTO dto1 = new TransactionDTO();
        dto1.setId(5L);
        dto1.setSenderAccountNumber("111111");
        dto1.setReceiverAccountNumber("222222");
        dto1.setAmount(100.0);
        dto1.setTimestamp(now);

        assertEquals(5L, dto1.getId());
        assertEquals("111111", dto1.getSenderAccountNumber());
        assertEquals("222222", dto1.getReceiverAccountNumber());
        assertEquals(100.0, dto1.getAmount());
        assertEquals(now, dto1.getTimestamp());

        // Constructor con parámetros
        TransactionDTO dto2 = new TransactionDTO(6L, "333333", "444444", 250.0, now);
        assertEquals(6L, dto2.getId());
        assertEquals("333333", dto2.getSenderAccountNumber());
        assertEquals("444444", dto2.getReceiverAccountNumber());
        assertEquals(250.0, dto2.getAmount());
        assertEquals(now, dto2.getTimestamp());
    }

    @Test
    @DisplayName("TransferRequestDTO - Debe instanciar y probar todos los getters y setters")
    void testTransferRequestDTO() {
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setSenderAccountNumber("555555");
        requestDTO.setReceiverAccountNumber("666666");
        requestDTO.setAmount(500.0);

        assertEquals("555555", requestDTO.getSenderAccountNumber());
        assertEquals("666666", requestDTO.getReceiverAccountNumber());
        assertEquals(500.0, requestDTO.getAmount());
    }
}
