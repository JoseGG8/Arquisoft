package com.udea.lab12026p.mapper;

import com.udea.lab12026p.dto.TransactionDTO;
import com.udea.lab12026p.entity.Transaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    private final TransactionMapper transactionMapper = TransactionMapper.INSTANCE;

    @Test
    @DisplayName("toDTO - Debe mapear Transaction a TransactionDTO correctamente")
    void toDTO_shouldMapCorrectly() {
        LocalDateTime time = LocalDateTime.now();
        Transaction transaction = new Transaction(10L, "111111", "222222", 350.0, time);

        TransactionDTO dto = transactionMapper.toDTO(transaction);

        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("111111", dto.getSenderAccountNumber());
        assertEquals("222222", dto.getReceiverAccountNumber());
        assertEquals(350.0, dto.getAmount());
        assertEquals(time, dto.getTimestamp());
    }

    @Test
    @DisplayName("toDTO - Debe retornar null cuando Transaction es null")
    void toDTO_whenNull_shouldReturnNull() {
        TransactionDTO dto = transactionMapper.toDTO(null);
        assertNull(dto);
    }
}
