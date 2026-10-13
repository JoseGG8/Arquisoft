# Informe de Laboratorio: Implementación de Pipeline CI/CD con GitHub Actions, JUnit 5, JaCoCo, SonarCloud y Docker

**Responsable:** José Luis Gil García  
**C.C:** 1034988407  
**Correo:** [j.gil@udea.edu.co](mailto:j.gil@udea.edu.co)  
**Enlace al proyecto de GitHub:** [https://github.com/JoseGG8/Arquisoft](https://github.com/JoseGG8/Arquisoft)  

---

## 1. Introducción

En el desarrollo de software moderno y de nivel empresarial, la adopción de prácticas de Integración Continua y Despliegue Continuo (CI/CD) resulta imprescindible para garantizar la calidad, mantenibilidad y entrega confiable de valor. Un pipeline automatizado permite detectar errores tempranamente mediante la ejecución de pruebas unitarias, evaluar la salud del código a través de herramientas de análisis estático y empaquetar aplicaciones de forma reproducible en contenedores independientes de la infraestructura.

En la presente práctica de laboratorio se diseñó e implementó un flujo completo de CI/CD para una aplicación bancaria desarrollada con **Spring Boot 3** y **Java 23**. La experiencia abordó desde el diseño de pruebas unitarias exhaustivas con **JUnit 5** y **MockMvc** para cada uno de los endpoints de la API REST, la medición y generación de reportes de cobertura con **JaCoCo**, la integración y análisis de deuda técnica con **SonarCloud**, hasta la containerización y publicación automatizada de imágenes mediante **Docker** y **GitHub Actions**. A lo largo de la práctica, se enfatizó en la identificación y resolución de inconsistencias críticas en el flujo de integración continua, tales como discrepancias de ramas, problemas de incompatibilidad de compiladores y optimización del empaquetado en contenedores.

---

## 2. Objetivos

### 2.1. Objetivo General
Diseñar e implementar un ciclo de vida de desarrollo e integración continua (CI/CD) automatizado utilizando GitHub Actions, validando la lógica del negocio mediante pruebas unitarias en JUnit 5, inspección de calidad con SonarCloud y containerización reproducible con Docker.

### 2.2. Objetivos Específicos
* Diseñar pruebas unitarias aisladas para los controladores REST (`CustomerController` y `TransactionController`) empleando JUnit 5, Mockito y MockMvc, eliminando la dependencia de servicios de base de datos externos durante la fase de prueba.
* Integrar la herramienta JaCoCo en el ciclo de construcción de Maven para generar métricas detalladas de cobertura de código ejecutable.
* Configurar e integrar SonarCloud en el flujo de trabajo para ejecutar análisis estático de código, detección de vulnerabilidades, *code smells* y consumo de reportes de cobertura.
* Construir un contenedor Docker optimizado y multi-etapa (*multi-stage build*) para empaquetar la aplicación en una imagen ligera basada en Eclipse Temurin JRE 23.
* Analizar, depurar y corregir las inconsistencias del archivo de workflow `build.yml` de GitHub Actions, asegurando la correcta sincronización de eventos de disparo (*triggers*), ramas de trabajo y credenciales de despliegue.

---

## 3. Herramientas y Tecnologías Utilizadas

* **Java 23 (Eclipse Temurin JDK / JRE):** Lenguaje de programación base y entorno de ejecución.
* **Spring Boot 3:** Framework para la construcción de la API REST empresarial, inyección de dependencias y persistencia con Spring Data JPA.
* **Apache Maven:** Gestor de ciclo de vida del proyecto, dependencias y plugins de construcción.
* **JUnit 5 (Jupiter):** Framework estándar para la definición, estructuración y aserción de pruebas unitarias.
* **Mockito & Spring Test (MockMvc):** Librerías para la creación de dobles de prueba (*mocks*) y simulación de peticiones HTTP en entornos aislados (*standalone*).
* **JaCoCo (Java Code Coverage):** Plugin de Maven para la instrumentación de código y cálculo de métricas de cobertura de pruebas.
* **SonarCloud:** Plataforma basada en la nube para la inspección continua de la calidad del código, deuda técnica y seguridad.
* **Docker & Docker Hub:** Plataforma de containerización para la creación de imágenes y registro de artefactos en contenedores.
* **GitHub Actions:** Orquestador de flujos de trabajo de Integración y Despliegue Continuo (CI/CD).
* **Git:** Sistema de control de versiones distribuido.

---

## 4. Metodología y Procedimiento

El desarrollo de la práctica se estructuró en cinco fases metodológicas iterativas, orientadas a garantizar la robustez del código y la estabilidad de la tubería de integración continua.

### 4.1. Diseño e Implementación de Pruebas Unitarias con JUnit 5 y MockMvc

Inicialmente, el proyecto contaba únicamente con una prueba genérica de arranque de contexto (`@SpringBootTest`). Al ejecutarse en entornos de integración continua o máquinas sin una instancia activa de MySQL en `localhost:3306`, la prueba fallaba invariablemente por errores de conexión de base de datos (`CommunicationsException / Connection refused`).

Para solucionar esta limitación y garantizar pruebas unitarias reales, se reestructuró por completo el archivo principal de pruebas [Lab12026pApplicationTests.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/test/java/com/udea/lab12026p/Lab12026pApplicationTests.java), adoptando las siguientes pautas:
1. **Aislamiento con Mockito:** Se utilizó la extensión `@ExtendWith(MockitoExtension.class)`, sustituyendo la carga del contexto pesado de Spring por inyecciones simuladas mediante `@Mock` para los servicios (`CustomerService`, `TransactionService`) y `@InjectMocks` para los controladores.
2. **Entorno Standalone con MockMvc:** Se configuró `MockMvcBuilders.standaloneSetup(...)` junto con un `ObjectMapper` adaptado al módulo `JavaTimeModule` para serializar y deserializar fechas `LocalDateTime`.
3. **Casos de prueba implementados (11 escenarios en total):**
   * **`CustomerController` (`/api/customers`):**
     * `getAllCustomers`: Verificación de respuesta HTTP 200 OK y estructura JSON ante listas con elementos y listas vacías.
     * `getCustomerById`: Consulta exitosa por ID (200 OK) y verificación de propagación de excepciones cuando el cliente no existe.
     * `createCustomer`: Creación satisfactoria con cálculo de saldo (200 OK) y comprobación de lanzamiento de `IllegalArgumentException` cuando el campo `balance` es nulo.
   * **`TransactionController` (`/api/transactions`):**
     * `transferMoney`: Transferencia exitosa entre cuentas (200 OK) y casos de fallo con HTTP 400 Bad Request por saldo insuficiente o cuenta inexistente.
     * `getTransactionsByAccount`: Obtención del historial de transacciones para una cuenta determinada y validación de respuesta vacía cuando no existen movimientos.
4. **Resolución de advertencias de nulabilidad (*Null type safety*):** El analizador estático de Eclipse JDT generaba múltiples advertencias debido a anotaciones `@NonNullApi` en dependencias de Spring. Se resolvieron aplicando `@SuppressWarnings("null")` en los componentes correspondientes sin alterar la integridad del código fuente.

### 4.2. Medición de Cobertura con JaCoCo

Para cuantificar qué porcentaje del código de negocio es validado por las pruebas unitarias, se configuró el plugin `jacoco-maven-plugin` (versión `0.8.12`) en el archivo [pom.xml](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/pom.xml):
* **Fase `prepare-agent`:** Instrumenta las clases en tiempo de ejecución configurando la propiedad `argLine` para recolectar las trazas de ejecución en `target/jacoco.exec`.
* **Fase `report`:** Vinculada al ciclo de vida `test` para generar automáticamente los reportes legibles en formatos XML (`target/site/jacoco/jacoco.xml`) y HTML (`target/site/jacoco/index.html`).

### 4.3. Configuración y Análisis Estático con SonarCloud

Se integró el plugin de escaneo `sonar-maven-plugin` en el pipeline de GitHub Actions para evaluar la calidad del código contra el Quality Gate de SonarCloud. Para asegurar que SonarCloud reflejara fielmente la cobertura obtenida por JaCoCo, se especificó de manera explícita la ruta del informe mediante el parámetro:
```bash
-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
```
Asimismo, se definieron las variables de entorno `SONAR_TOKEN` (almacenada en los *Secrets* de GitHub) y `GITHUB_TOKEN` para la autenticación y permisos de comentario sobre Pull Requests.

### 4.4. Containerización con Dockerfile Multi-Etapa y Depuración de Compilación

La containerización de la aplicación Spring Boot se definió en [lab12026p/Dockerfile](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/Dockerfile) mediante un esquema multi-etapa (*multi-stage build*). Durante este proceso se presentó un error crítico durante la construcción de la imagen en Docker:

```text
Fatal error compiling: java.lang.ExceptionInInitializerError: com.sun.tools.javac.tree.EndPosTable
WARNING: sun.misc.Unsafe::objectFieldOffset has been called by lombok.permit.Permit
```

* **Causa Raíz:** El archivo Dockerfile utilizaba `FROM maven:latest AS build`, la cual descargaba la versión más reciente de OpenJDK (JDK 24/latest). Por su parte, el archivo `pom.xml` utilizaba la versión `1.18.30` de Lombok. Debido a cambios internos en la API privada del compilador `javac` de versiones modernas de Java, el procesador de anotaciones de Lombok colapsaba al inicializarse.
* **Solución Implementada:**
  1. Se actualizó la dependencia y el procesador de Lombok a la versión oficial compatible **`1.18.36`** dentro del `pom.xml`, fijando también la propiedad `<java.version>23</java.version>`.
  2. Se modificó la primera etapa del Dockerfile para utilizar la imagen oficial **`maven:3.9-eclipse-temurin-23 AS build`**, sincronizando la versión del compilador con el runtime final **`eclipse-temurin:23-jre`**.
  3. Se declaró la directiva `<finalName>lab12026p</finalName>` en el `pom.xml`, garantizando que el artefacto generado en `target/lab12026p.jar` coincidiera de forma determinista con la ruta esperada por la instrucción `COPY` del contenedor y el paso de carga de artefactos del workflow.

### 4.5. Automatización del Pipeline CI/CD con GitHub Actions y Aprendizaje de Inconsistencias

El pipeline principal se formalizó en [.github/workflows/build.yml](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/.github/workflows/build.yml). Durante las pruebas iniciales del pipeline a través de Pull Requests se presentó una eventualidad fundamental para el aprendizaje del equipo: **el workflow no se disparaba al abrir el PR y GitHub permitía hacer *merge* sin ejecutar ninguna validación**.

Al analizar el comportamiento del orquestador se identificaron las siguientes inconsistencias y sus respectivas soluciones:

1. **Configuración de Disparadores para Pull Requests desde Cualquier Rama:**
   * *Problema inicial:* El workflow tenía declarado `on: pull_request: branches: [main]`. Al trabajar con la rama base `master` y abrir el Pull Request desde `dev/tests`, GitHub Actions descartaba el evento por no coincidir la rama destino.
   * *Decisión de diseño y solución:* **Se eligió poner en el `build.yml` la configuración que corresponde para que cuando se abra un Pull Request desde cualquier rama hacia las ramas principales (`master` o `main`) se ejecute automáticamente el pipeline**. De este modo, cualquier desarrollador que trabaje en una rama de características (*feature branch*), corrección (*hotfix*) o desarrollo (`dev/*`) verá ejecutarse de inmediato la batería de pruebas y el análisis de calidad al solicitar la integración:
     ```yaml
     on:
       pull_request:
         branches:
           - master
           - main
       push:
         branches:
           - master
           - main
       workflow_dispatch:
     ```
     *(Esta directiva garantiza que cualquier rama de origen active el ciclo de validación, mientras que `workflow_dispatch` permite disparar el pipeline de forma manual en cualquier momento).*
2. **Inconsistencias de Caché de Maven:**
   * *Problema:* Se mezclaban llamadas a la acción deprecada `actions/cache@v3` con la caché integrada de `actions/setup-java@v4`.
   * *Solución:* Se unificó la estrategia en todos los jobs (`tests`, `sonar`, `build`) utilizando la directiva nativa `cache: 'maven'` con `cache-dependency-path: 'lab12026p/pom.xml'`, acelerando significativamente los tiempos de construcción en los runners de Ubuntu.
3. **Redundancia y Permisos en SonarCloud:**
   * *Problema:* La falta del permiso `pull-requests: write` impedía que SonarCloud decorara los PRs con los resultados del análisis.
   * *Solución:* Se configuraron explícitamente los permisos `contents: read` y `pull-requests: write`.
4. **Despliegue de Docker:**
   * La acción `mr-smithers-excellent/docker-build-push@v6` fue configurada con el contexto `directory: lab12026p` y `dockerfile: lab12026p/Dockerfile`, consumiendo las credenciales seguras `DOCKER_USERNAME` y `DOCKER_PASSWORD`.
5. **Orquestación del Despliegue en Render vía Deploy Hook:**
   * Se incorporó un job final `deploy` en `build.yml` dependiente de `build`. Este job se ejecuta tras la aprobación o push en la rama principal y dispara el webhook seguro de Render (`curl -f -X POST "$RENDER_DEPLOY_HOOK"`). De este modo, el despliegue en la nube ocurre exclusivamente cuando las pruebas unitarias y el análisis de calidad han concluido con éxito.

### 4.6. Despliegue en la Nube (Render) y Transición a Base de Datos H2 en Memoria

Al desplegar la imagen generada en la plataforma Render, la aplicación presentó un fallo en el arranque (`CommunicationsException: Communications link failure / Connection refused`), finalizando con estado `Exited with status 1`:
* **Causa:** El contenedor intentaba conectarse a `localhost:3306`, buscando una instancia local de MySQL que no existe dentro del contenedor aislado en la nube.
* **Solución Implementada:**
  1. Se añadió la dependencia `com.h2database:h2` en `pom.xml` con alcance `runtime`.
  2. Se configuró [application.properties](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/main/resources/application.properties) con una base de datos H2 en memoria (`jdbc:h2:mem:bankudea;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL`) y el puerto dinámico de Render (`server.port=${PORT:8080}`).
  3. Se mantuvo la compatibilidad para bases de datos externas mediante variables de entorno (`${SPRING_DATASOURCE_URL:...}`).
  4. Se implementó un semillero de datos iniciales en [Lab12026pApplication.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/main/java/com/udea/lab12026p/Lab12026pApplication.java) mediante `CommandLineRunner` para poblar clientes de prueba automáticamente al arrancar.

---

## 5. Resultados Obtenidos

1. **Pruebas Unitarias Robustas y Cobertura Integral:**
   * Se alcanzaron **38 pruebas unitarias** ejecutadas de manera satisfactoria (`Tests run: 38, Failures: 0, Errors: 0, Skipped: 0`), abarcando controladores, servicios con lógica de negocio, mapeadores MapStruct, entidades JPA y DTOs.
   * La eliminación del acoplamiento a la base de datos MySQL mediante mocks aislados con Mockito permitió que las pruebas se ejecuten limpiamente en menos de 12 segundos.
2. **Generación Automatizada de Cobertura (100% JaCoCo):**
   * JaCoCo generó con éxito el reporte auditando el 100% de las instrucciones, líneas y ramas del código de producción (`lab12026p`), superando con creces el umbral mínimo del 80% exigido por los Quality Gates de SonarCloud.
3. **Pipeline de Integración Continua Operativo:**
   * El flujo de trabajo en GitHub Actions ejecuta secuencialmente los tres jobs planificados:
     * **`tests`:** Compila, ejecuta las pruebas JUnit 5 y genera las métricas de cobertura.
     * **`sonar`:** Ejecuta el análisis de calidad e inspección estática, reportando los resultados a SonarCloud.
     * **`build`:** Empaqueta el ejecutable `.jar`, almacena el artefacto en GitHub Actions y construye/publica la imagen de Docker en Docker Hub.
4. **Despliegue Exitoso en la Nube (Render):**
   * La transición a la base de datos H2 en memoria y la vinculación dinámica a `${PORT:8080}` permitieron que el contenedor de Docker arranque de forma autónoma en Render, respondiendo a peticiones HTTP sin dependencias de red externas.

---

## 6. Conclusiones

* **Importancia de las Pruebas Unitarias Aisladas:** El desacoplamiento de las capas web y de servicio mediante Mockito y MockMvc es un pilar fundamental en arquitecturas empresariales. Evitar el levantamiento de infraestructura externa (como bases de datos reales) en pruebas unitarias previene falsos negativos en CI/CD y asegura una retroalimentación inmediata al desarrollador.
* **Sincronización Rigurosa en la Configuración de Pipelines:** La experiencia con GitHub Actions evidenció que pequeños desajustes en la nomenclatura de ramas (`master` frente a `main`) o en la definición de eventos de disparo pueden deshabilitar silenciosamente la ejecución de controles de calidad. La definición explícita de disparadores y la configuración de reglas de protección de ramas (*Branch Protection Rules*) son indispensables para evitar fusiones accidentales de código sin validar.
* **Compatibilidad de Herramientas y JDKs Modernos:** El uso de versiones vanguardistas de Java (Java 23) requiere una estricta alineación de las librerías que realizan manipulación de bytecode y procesamiento de anotaciones (como Lombok). Emplear imágenes base fijas y específicas en Docker (`maven:3.9-eclipse-temurin-23`) en lugar de etiquetas flotantes (`latest`) previene comportamientos erráticos e incompatibilidades imprevistas en los entornos de compilación.
* **Valor del Análisis Estático Continuo:** La integración coordinada de JaCoCo y SonarCloud dentro de la tubería de CI/CD promueve una cultura de desarrollo guiada por la calidad, permitiendo que cada Pull Request sea evaluado de forma automática en términos de cobertura, seguridad y mantenibilidad antes de incorporarse a la rama principal.
