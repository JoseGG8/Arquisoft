package com.udea.lab12026p.service;

import com.udea.lab12026p.dto.TransactionDTO;
import com.udea.lab12026p.entity.Customer;
import com.udea.lab12026p.entity.Transaction;
import com.udea.lab12026p.repository.CustomerRepository;
import com.udea.lab12026p.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("transferMoney - Debe lanzar IllegalArgumentException si el número de cuenta remitente es nulo")
    void transferMoney_whenSenderAccountNumberIsNull_shouldThrowException() {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber(null);
        dto.setReceiverAccountNumber("222222");
        dto.setAmount(100.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.transferMoney(dto));

        assertEquals("Los números de cuenta del remitente y receptor son obligatorios.", ex.getMessage());
        verifyNoInteractions(customerRepository, transactionRepository);
    }

    @Test
    @DisplayName("transferMoney - Debe lanzar IllegalArgumentException si el número de cuenta receptor es nulo")
    void transferMoney_whenReceiverAccountNumberIsNull_shouldThrowException() {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber("111111");
        dto.setReceiverAccountNumber(null);
        dto.setAmount(100.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.transferMoney(dto));

        assertEquals("Los números de cuenta del remitente y receptor son obligatorios.", ex.getMessage());
        verifyNoInteractions(customerRepository, transactionRepository);
    }

    @Test
    @DisplayName("transferMoney - Debe lanzar IllegalArgumentException si la cuenta del remitente no existe")
    void transferMoney_whenSenderNotFound_shouldThrowException() {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber("111111");
        dto.setReceiverAccountNumber("222222");
        dto.setAmount(100.0);

        when(customerRepository.findByAccountNumber("111111")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.transferMoney(dto));

        assertEquals("La cuenta del remitente no existe.", ex.getMessage());
        verify(customerRepository, times(1)).findByAccountNumber("111111");
        verify(customerRepository, never()).findByAccountNumber("222222");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    @DisplayName("transferMoney - Debe lanzar IllegalArgumentException si la cuenta del receptor no existe")
    void transferMoney_whenReceiverNotFound_shouldThrowException() {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber("111111");
        dto.setReceiverAccountNumber("222222");
        dto.setAmount(100.0);

        Customer sender = new Customer(1L, "111111", "Carlos", "Perez", 500.0);
        when(customerRepository.findByAccountNumber("111111")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("222222")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.transferMoney(dto));

        assertEquals("La cuenta del receptor no existe.", ex.getMessage());
        verify(customerRepository, times(1)).findByAccountNumber("111111");
        verify(customerRepository, times(1)).findByAccountNumber("222222");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    @DisplayName("transferMoney - Debe lanzar IllegalArgumentException si el remitente tiene saldo insuficiente")
    void transferMoney_whenInsufficientBalance_shouldThrowException() {
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber("111111");
        dto.setReceiverAccountNumber("222222");
        dto.setAmount(600.0);

        Customer sender = new Customer(1L, "111111", "Carlos", "Perez", 500.0);
        Customer receiver = new Customer(2L, "222222", "Maria", "Gomez", 300.0);

        when(customerRepository.findByAccountNumber("111111")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("222222")).thenReturn(Optional.of(receiver));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> transactionService.transferMoney(dto));

        assertEquals("Saldo insuficiente en la cuenta del remitente.", ex.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
        verifyNoInteractions(transactionRepository);
    }

    @Test
    @DisplayName("transferMoney - Debe transferir dinero exitosamente, actualizar saldos y retornar TransactionDTO")
    void transferMoney_whenValid_shouldTransferSuccessfully() {
        LocalDateTime timestamp = LocalDateTime.now();
        TransactionDTO dto = new TransactionDTO();
        dto.setSenderAccountNumber("111111");
        dto.setReceiverAccountNumber("222222");
        dto.setAmount(200.0);
        dto.setTimestamp(timestamp);

        Customer sender = new Customer(1L, "111111", "Carlos", "Perez", 1000.0);
        Customer receiver = new Customer(2L, "222222", "Maria", "Gomez", 500.0);

        when(customerRepository.findByAccountNumber("111111")).thenReturn(Optional.of(sender));
        when(customerRepository.findByAccountNumber("222222")).thenReturn(Optional.of(receiver));

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        TransactionDTO result = transactionService.transferMoney(dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("111111", result.getSenderAccountNumber());
        assertEquals("222222", result.getReceiverAccountNumber());
        assertEquals(200.0, result.getAmount());
        assertEquals(timestamp, result.getTimestamp());

        // Verificar que los saldos se modificaron correctamente
        assertEquals(800.0, sender.getBalance());
        assertEquals(700.0, receiver.getBalance());

        // Verificar que se guardaron ambos clientes y la transacción
        verify(customerRepository, times(1)).save(sender);
        verify(customerRepository, times(1)).save(receiver);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("getTransactionsForAccount - Debe retornar transacciones mapeadas cuando existen")
    void getTransactionsForAccount_whenFound_shouldReturnDTOList() {
        LocalDateTime time = LocalDateTime.now();
        Transaction t1 = new Transaction(1L, "111111", "222222", 150.0, time);
        Transaction t2 = new Transaction(2L, "333333", "111111", 50.0, time);

        when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("111111", "111111"))
                .thenReturn(List.of(t1, t2));

        List<TransactionDTO> result = transactionService.getTransactionsForAccount("111111");

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("111111", result.get(0).getSenderAccountNumber());
        assertEquals("222222", result.get(0).getReceiverAccountNumber());
        assertEquals(150.0, result.get(0).getAmount());

        assertEquals(2L, result.get(1).getId());
        assertEquals("333333", result.get(1).getSenderAccountNumber());
        assertEquals("111111", result.get(1).getReceiverAccountNumber());
        assertEquals(50.0, result.get(1).getAmount());

        verify(transactionRepository, times(1))
                .findBySenderAccountNumberOrReceiverAccountNumber("111111", "111111");
    }

    @Test
    @DisplayName("getTransactionsForAccount - Debe retornar lista vacía si no hay transacciones")
    void getTransactionsForAccount_whenNone_shouldReturnEmptyList() {
        when(transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber("999999", "999999"))
                .thenReturn(Collections.emptyList());

        List<TransactionDTO> result = transactionService.getTransactionsForAccount("999999");

        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1))
                .findBySenderAccountNumberOrReceiverAccountNumber("999999", "999999");
    }
}
