package com.udea.lab12026p.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    @Test
    @DisplayName("Customer Entity - Debe instanciar y probar todos los constructores, getters y setters")
    void testCustomerEntity() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setAccountNumber("123456");
        customer.setFirstName("Carlos");
        customer.setLastName("Perez");
        customer.setBalance(1500.0);

        assertEquals(1L, customer.getId());
        assertEquals("123456", customer.getAccountNumber());
        assertEquals("Carlos", customer.getFirstName());
        assertEquals("Perez", customer.getLastName());
        assertEquals(1500.0, customer.getBalance());

        // Constructor con parámetros
        Customer customer2 = new Customer(2L, "654321", "Maria", "Gomez", 2500.0);
        assertEquals(2L, customer2.getId());
        assertEquals("654321", customer2.getAccountNumber());
        assertEquals("Maria", customer2.getFirstName());
        assertEquals("Gomez", customer2.getLastName());
        assertEquals(2500.0, customer2.getBalance());
    }

    @Test
    @DisplayName("Transaction Entity - Debe instanciar y probar todos los constructores, getters y setters")
    void testTransactionEntity() {
        LocalDateTime now = LocalDateTime.now();
        Transaction transaction = new Transaction();
        transaction.setId(10L);
        transaction.setSenderAccountNumber("111111");
        transaction.setReceiverAccountNumber("222222");
        transaction.setAmount(100.0);
        transaction.setTimestamp(now);

        assertEquals(10L, transaction.getId());
        assertEquals("111111", transaction.getSenderAccountNumber());
        assertEquals("222222", transaction.getReceiverAccountNumber());
        assertEquals(100.0, transaction.getAmount());
        assertEquals(now, transaction.getTimestamp());

        // Constructor con parámetros
        Transaction transaction2 = new Transaction(11L, "333333", "444444", 250.0, now);
        assertEquals(11L, transaction2.getId());
        assertEquals("333333", transaction2.getSenderAccountNumber());
        assertEquals("444444", transaction2.getReceiverAccountNumber());
        assertEquals(250.0, transaction2.getAmount());
        assertEquals(now, transaction2.getTimestamp());

        // Probar métodos generados por Lombok (@Data) si aplican
        assertNotNull(transaction.toString());
        Transaction transaction3 = new Transaction(10L, "111111", "222222", 100.0, now);
        assertEquals(transaction, transaction3);
        assertEquals(transaction.hashCode(), transaction3.hashCode());
    }
}
