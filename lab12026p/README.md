# Lab 12026p - Proyecto Spring Boot

Proyecto de demostración desarrollado con **Spring Boot** que implementa una aplicación backend con gestión de datos y validación.

## Tecnologías Utilizadas

- **Java 17** - Lenguaje de programación
- **Spring Boot 3.5.11** - Framework principal
- **Spring Data JPA** - Persistencia y acceso a datos
- **MySQL** - Base de datos relacional
- **MapStruct 1.5.5** - Mapeo de objetos DTOs
- **Lombok 1.18.30** - Reducción de código boilerplate
- **Jackson 2.15.0** - Serialización/deserialización JSON
- **Maven** - Gestor de dependencias y construcción

## Descripción

Este proyecto es una aplicación REST construida con Spring Boot que proporciona una API para gestionar entidades con persistencia en base de datos MySQL. Utiliza JPA para el acceso a datos y MapStruct para el mapeo entre entidades y DTOs.

## Requisitos

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+

## Construcción y Ejecución

```bash
# Compilar el proyecto
./mvnw clean install

# Ejecutar la aplicación
./mvnw spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`
