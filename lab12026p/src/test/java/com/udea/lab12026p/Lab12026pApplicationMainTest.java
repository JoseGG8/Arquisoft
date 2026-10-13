package com.udea.lab12026p;

import com.udea.lab12026p.entity.Customer;
import com.udea.lab12026p.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class Lab12026pApplicationMainTest {

    @Mock
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("initDatabase - Debe sembrar clientes si la base de datos está vacía (count == 0)")
    void initDatabase_whenDatabaseIsEmpty_shouldSeedCustomers() throws Exception {
        Lab12026pApplication app = new Lab12026pApplication();
        when(customerRepository.count()).thenReturn(0L);

        CommandLineRunner runner = app.initDatabase(customerRepository);
        assertNotNull(runner);
        runner.run();

        verify(customerRepository, times(1)).count();
        verify(customerRepository, times(2)).save(any(Customer.class));
    }

    @Test
    @DisplayName("initDatabase - No debe sembrar clientes si la base de datos ya tiene registros (count > 0)")
    void initDatabase_whenDatabaseNotEmpty_shouldNotSeedCustomers() throws Exception {
        Lab12026pApplication app = new Lab12026pApplication();
        when(customerRepository.count()).thenReturn(2L);

        CommandLineRunner runner = app.initDatabase(customerRepository);
        assertNotNull(runner);
        runner.run();

        verify(customerRepository, times(1)).count();
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("main - Debe invocar SpringApplication.run")
    void main_shouldInvokeSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(() -> SpringApplication.run(Lab12026pApplication.class, new String[]{}))
                    .thenReturn(null);

            Lab12026pApplication.main(new String[]{});

            mockedSpringApplication.verify(() -> SpringApplication.run(Lab12026pApplication.class, new String[]{}));
        }
    }
}
