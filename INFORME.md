# Informe de Laboratorio: Implementación de Pipeline CI/CD con GitHub Actions, JUnit 5, JaCoCo, SonarCloud, Docker y Despliegue Continuo en Render

**Responsable:** José Luis Gil García  
**C.C:** 1034988407  
**Correo:** [j.gil@udea.edu.co](mailto:j.gil@udea.edu.co)  
**Enlace al proyecto de GitHub:** [https://github.com/JoseGG8/Arquisoft](https://github.com/JoseGG8/Arquisoft)  
**Enlace a la API desplegada en Render:** [https://arquisoft-uidq.onrender.com](https://arquisoft-uidq.onrender.com)  

---

## 1. Introducción

En la ingeniería de software moderna y las arquitecturas nativas de la nube, la adopción integral de prácticas de **Integración Continua (CI)** y **Despliegue Continuo (CD)** constituye un pilar indispensable para garantizar la calidad, estabilidad y entrega ágil de valor. Mientras que la Integración Continua (CI) se encarga de validar automáticamente cada cambio mediante compilación, ejecución exhaustiva de pruebas unitarias y análisis estático de código, el Despliegue Continuo (CD) cierra el ciclo DevOps automatizando la entrega y puesta en producción del software validado hacia plataformas en la nube, eliminando por completo los despliegues manuales propensos a error humano y garantizando que cada versión aprobada esté inmediatamente disponible para el usuario final.

**GitHub Actions** se destaca en la industria como un orquestador integral de CI/CD capaz de ejecutar flujos de trabajo declarativos orientados a eventos (*event-driven workflows*). Mediante este motor, es posible encadenar etapas secuenciales con dependencias estrictas (*jobs* y directivas `needs`), gestionar secretos de forma segura y comunicarse con plataformas externas mediante APIs y *Deploy Hooks*.

En la presente práctica de laboratorio se diseñó e implementó un flujo completo de CI/CD para una aplicación bancaria transaccional desarrollada con **Spring Boot 3** y **Java 23**. La experiencia abordó de manera integral tanto la fase de integración como la de despliegue:
* **Fase de Integración Continua (CI):** Diseño de una suite integral de 38 pruebas unitarias con **JUnit 5** y **Mockito** para controladores, servicios de lógica de negocio, mapeadores MapStruct y entidades JPA; medición rigurosa de cobertura con **JaCoCo** (alcanzando el **100%** de cobertura en instrucciones y ramas); y auditoría de calidad de código con el Quality Gate de **SonarCloud**.
* **Fase de Despliegue Continuo (CD) en el Proyecto:** Empaquetado determinista y multi-etapa (*multi-stage build*) con **Docker**, publicación automática de la imagen en **Docker Hub** (`latest`), y orquestación del despliegue desatendido en la nube pública (**Render**) a través de un webhook protegido (*Deploy Hook*). Para resolver el desafío de desplegar en un entorno de nube efímero sin bases de datos externas acopladas, se diseñó una estrategia de persistencia autónoma basada en **H2 en memoria** con semillero de datos iniciales y vinculación dinámica de puertos (`$PORT`), asegurando un arranque en la nube 100% libre de fallos de conectividad.

---

## 2. Objetivos

### 2.1. Objetivo General
Diseñar e implementar un ciclo de vida completo de Integración Continua y Despliegue Continuo (CI/CD) automatizado con GitHub Actions para una API REST en Spring Boot 3 y Java 23, validando la lógica de negocio mediante pruebas unitarias en JUnit 5, asegurando alta calidad con JaCoCo y SonarCloud, empaquetando en contenedores Docker y desplegando de forma autónoma y continua en la plataforma Render.

### 2.2. Objetivos Específicos
* Diseñar e implementar pruebas unitarias exhaustivas y aisladas (controladores, servicios, DTOs, entidades y mapeadores) empleando JUnit 5, Mockito y MockMvc, alcanzando una cobertura de código superior al 80% requerida por estándares de calidad.
* Integrar JaCoCo en el ciclo de Maven para generar reportes auditables de cobertura y sincronizarlos con SonarCloud para el control automático de Quality Gates.
* Construir una imagen de contenedor Docker optimizada mediante construcción multi-etapa (*multi-stage build*) con Eclipse Temurin JDK/JRE 23 y publicarla de manera automática en el registro Docker Hub.
* Automatizar el Despliegue Continuo (CD) hacia la plataforma Render orquestado por GitHub Actions mediante un Deploy Hook condicional, garantizando que el despliegue se active automáticamente únicamente tras la aprobación de pruebas y el merge en las ramas principales (`master`/`main`).
* Adaptar la arquitectura de persistencia de la aplicación para entornos en la nube (Render), desacoplándola de instancias MySQL locales mediante una base de datos H2 en memoria con compatibilidad MySQL, semillero automático (`CommandLineRunner`) y vinculación dinámica al puerto asignado por el proveedor de nube (`${PORT:8080}`).
* Analizar, depurar y corregir inconsistencias en el workflow `build.yml` de GitHub Actions, diferenciando la ejecución de validaciones en Pull Requests frente a las acciones de despliegue en eventos `push`.

---

## 3. Herramientas y Tecnologías Utilizadas

* **Java 23 (Eclipse Temurin JDK / JRE):** Lenguaje de programación base y entorno de ejecución moderno.
* **Spring Boot 3:** Framework para la construcción de la API REST empresarial, inyección de dependencias y persistencia con Spring Data JPA.
* **Apache Maven:** Gestor de ciclo de vida del proyecto, dependencias y plugins de construcción.
* **JUnit 5 (Jupiter):** Framework estándar para la definición, estructuración y aserción de pruebas unitarias.
* **Mockito & Spring Test (MockMvc):** Librerías para dobles de prueba (*mocks*) y simulación de peticiones HTTP en entornos aislados.
* **JaCoCo (Java Code Coverage):** Plugin de instrumentación de bytecode y métricas de cobertura de pruebas.
* **SonarCloud:** Plataforma de análisis estático continuo para control de calidad, cobertura, deuda técnica y seguridad.
* **Docker & Docker Hub:** Plataforma de containerización multi-etapa y registro público de imágenes de contenedores.
* **Render:** Plataforma de computación en la nube utilizada para el alojamiento y ejecución de la aplicación containerizada, configurada para despliegue continuo desatendido a través de webhooks seguros (*Deploy Hooks*).
* **H2 Database Engine:** Motor de base de datos relacional embebida en memoria para entornos de pruebas y despliegues autónomos en la nube.
* **GitHub Actions:** Orquestador de flujos de trabajo de Integración y Despliegue Continuo (CI/CD).
* **Git:** Sistema de control de versiones distribuido.

---

## 4. Metodología y Procedimiento

El desarrollo de la práctica se estructuró en seis fases metodológicas iterativas, orientadas a garantizar la robustez del código, la calidad estática del software y la automatización del despliegue continuo en la nube.

### 4.1. Diseño e Implementación de Pruebas Unitarias Exhaustivas (JUnit 5 y Mockito)

Inicialmente, el proyecto contaba únicamente con una prueba genérica de arranque de contexto (`@SpringBootTest`). Al ejecutarse en entornos de integración continua sin una instancia activa de MySQL en `localhost:3306`, la prueba colapsaba invariablemente por errores de conexión (`CommunicationsException / Connection refused`). Además, las pruebas iniciales aisladas únicamente evaluaban los controladores mediante dobles de prueba (`@Mock`), lo que provocaba que la cobertura real de las clases de servicio, entidades y mapeadores fuera del **0%** (alcanzando solo un 28.6% global).

Para solucionar esto de raíz y garantizar una validación exhaustiva tanto de la capa web como de la lógica de negocio pura, se construyó una suite de **38 pruebas unitarias** distribuidas estratégicamente:

1. **Aislamiento de Controladores con MockMvc ([Lab12026pApplicationTests.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/test/java/com/udea/lab12026p/Lab12026pApplicationTests.java) - 11 pruebas):**
   * Se configuró `MockMvcBuilders.standaloneSetup(...)` con `@ExtendWith(MockitoExtension.class)` y soporte para serialización de fechas `LocalDateTime`.
   * **`CustomerController` (`/api/customers`):** Verificación de estados HTTP 200 OK en listados poblados y vacíos, consulta por ID, excepciones de clientes no encontrados y validación de saldo no nulo en creación.
   * **`TransactionController` (`/api/transactions`):** Validación de transferencias exitosas, manejo de errores HTTP 400 Bad Request por saldo insuficiente o cuentas no existentes, y consulta de historial de transacciones por cuenta.

2. **Validación de Lógica de Negocio en Servicios ([CustomerServiceTest.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/test/java/com/udea/lab12026p/service/CustomerServiceTest.java) y [TransactionServiceTest.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/test/java/com/udea/lab12026p/service/TransactionServiceTest.java) - 13 pruebas):**
   * En [TransactionService.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/main/java/com/udea/lab12026p/service/TransactionService.java) se evaluaron minuciosamente todas las ramas de control: números de cuenta nulos (origen y destino), inexistencia de cuenta remitente, inexistencia de cuenta receptora, saldo insuficiente, actualización y persistencia correcta de saldos (`sender.setBalance(...)`, `receiver.setBalance(...)`), guardado de la entidad transacción y retorno del DTO con timestamp.
   * En [CustomerService.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/main/java/com/udea/lab12026p/service/CustomerService.java) se probaron los métodos de consulta general, búsqueda por ID (con lanzamiento de `RuntimeException("Cliente no encontrado")`) y guardado/conversión de entidades.

3. **Pruebas de Mapeadores MapStruct ([CustomerMapperTest.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/test/java/com/udea/lab12026p/mapper/CustomerMapperTest.java) y [TransactionMapperTest.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/test/java/com/udea/lab12026p/mapper/TransactionMapperTest.java) - 6 pruebas):**
   * Validación del mapeo bidireccional entre DTOs y Entidades JPA, garantizando el manejo adecuado de entradas nulas generadas por las clases de implementación.

4. **Pruebas de Integridad de DTOs y Entidades JPA ([DtoTest.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/test/java/com/udea/lab12026p/dto/DtoTest.java) y [EntityTest.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/test/java/com/udea/lab12026p/entity/EntityTest.java) - 5 pruebas):**
   * Cobertura al 100% de constructores por defecto y con parámetros, getters, setters y métodos autogenerados (`equals`, `hashCode`, `toString`) en `CustomerDTO`, `TransactionDTO`, `TransferRequestDTO`, `Customer` y `Transaction`.

5. **Pruebas del Semillero y Arranque ([Lab12026pApplicationMainTest.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/test/java/com/udea/lab12026p/Lab12026pApplicationMainTest.java) - 3 pruebas):**
   * Verificación del bean `CommandLineRunner initDatabase` ante bases de datos vacías (ejecución de semillero) vs con datos existentes, así como la invocación del método `main`.

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
5. **Orquestación del Despliegue Continuo (CD) en Render vía Deploy Hook:**
   * **Automatización con GitHub Actions:** Para cerrar el ciclo DevOps, se implementó el job `deploy` dependiente estrictamente de `build` (`needs: build`). El despliegue continuo se diseñó bajo una política de protección por eventos:
     ```yaml
     deploy:
       needs: build
       name: Deploy to Render
       runs-on: ubuntu-latest
       if: (github.event_name == 'push' || github.event_name == 'workflow_dispatch')
       steps:
         - name: Trigger Render Deploy Hook
           env:
             RENDER_DEPLOY_HOOK: ${{ secrets.RENDER_DEPLOY_HOOK_URL }}
           run: |
             if [ -n "$RENDER_DEPLOY_HOOK" ]; then
               echo "Llamando al Deploy Hook de Render..."
               curl -f -X POST "$RENDER_DEPLOY_HOOK"
               echo "Despliegue en Render disparado exitosamente."
             fi
     ```
   * **Diferenciación Arquitectónica CI vs. CD:**
     * Durante la revisión de **Pull Requests** (`pull_request`), el job `deploy` es omitido intencionalmente (*skipped*) por la condición `if`, asegurando que el código no aprobado nunca impacte el entorno en vivo.
     * Al realizar el **Merge** a las ramas troncales (`master` o `main`), el evento se transforma en `push`, desencadenando la construcción de la imagen en Docker Hub y disparando el *Deploy Hook* de Render sin requerir intervención manual.

### 4.6. Abordaje del Despliegue en la Nube (Render) y Arquitectura de Persistencia Autónoma

Al desplegar por primera vez la imagen de Docker en la plataforma Render mediante el webhook automatizado, se presentó un fallo crítico durante el arranque del contenedor (`CommunicationsException: Communications link failure / Connection refused`), finalizando con estado de salida `Exited with status 1`:

* **Diagnóstico del Problema en la Nube:** El contenedor aislado en Render intentaba conectarse por defecto a `localhost:3306`. Como los contenedores en la nube no cuentan con una instancia externa de MySQL ejecutándose en la misma interfaz de red local, el driver de MySQL rechazaba la conexión.
* **Estrategia Específica Implementada en el Proyecto:**
  1. **Transición a Base de Datos H2 en Memoria:** Se incorporó la dependencia `com.h2database:h2` en el [pom.xml](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/pom.xml) con alcance de tiempo de ejecución (`runtime`).
  2. **Configuración Resiliente en [application.properties](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/main/resources/application.properties):** Se configuró una URL por defecto con emulación de dialecto MySQL:
     ```properties
     spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:h2:mem:bankudea;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL}
     spring.datasource.driver-class-name=${SPRING_DATASOURCE_DRIVER:org.h2.Driver}
     spring.datasource.username=${SPRING_DATASOURCE_USERNAME:sa}
     spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
     ```
     Esta técnica otorga autonomía total al contenedor en la nube y, a su vez, preserva la flexibilidad de conectarse a un MySQL externo si se inyecta la variable de entorno `SPRING_DATASOURCE_URL`.
  3. **Vinculación Dinámica de Puertos para Render:** Render asigna aleatoriamente el puerto de escucha del contenedor a través de la variable de entorno `$PORT`. Se configuró la directiva `server.port=${PORT:8080}`, permitiendo que Spring Boot se enlace dinámicamente al puerto asignado por la infraestructura del proveedor sin colisiones.
  4. **Semillero de Datos Automático:** Para asegurar que la API REST responda con registros de prueba inmediatamente tras el arranque en la nube sin depender de migraciones manuales, se implementó en [Lab12026pApplication.java](file:///c:/Users/Jose%20Luis%20GIl/Documents/Cursos%202026-2/Arquisoft/lab12026p/src/main/java/com/udea/lab12026p/Lab12026pApplication.java) un componente `CommandLineRunner` que inserta clientes iniciales cuando la base de datos se encuentra vacía.

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
4. **Despliegue Continuo Automatizado en la Nube (Render):**
   * Tras la fusión (*merge*) en `master`, el job `deploy` invocó exitosamente el Deploy Hook de Render.
   * La aplicación se encuentra desplegada y operativa en producción en la siguiente URL pública:
     * **URL Base del Servicio:** [`https://arquisoft-uidq.onrender.com`](https://arquisoft-uidq.onrender.com)
     * **Endpoint de Clientes (GET / POST):** [`https://arquisoft-uidq.onrender.com/api/customers`](https://arquisoft-uidq.onrender.com/api/customers)
     * **Endpoint de Transacciones:** [`https://arquisoft-uidq.onrender.com/api/transactions/123456`](https://arquisoft-uidq.onrender.com/api/transactions/123456)
     * **Consola de Base de Datos H2:** [`https://arquisoft-uidq.onrender.com/h2-console`](https://arquisoft-uidq.onrender.com/h2-console)
   * La transición a la base de datos H2 en memoria y la vinculación dinámica a `${PORT:8080}` permitieron que el contenedor de Docker arranque de forma autónoma en Render, respondiendo a peticiones HTTP sin dependencias de red externas.

---

## 6. Conclusiones

* **Automatización del Despliegue Continuo (CD) con GitHub Actions:** La orquestación de despliegues desatendidos mediante webhooks seguros (*Deploy Hooks*) acoplados a eventos `push` elimina la intervención manual y minimiza el tiempo de entrega (*Time-to-Market*). Condicionar el despliegue al paso exitoso de las compuertas de calidad (*Quality Gates*) asegura que ninguna versión defectuosa llegue a los entornos productivos.
* **Separación de Responsabilidades entre CI y CD:** Es esencial que las ramas de desarrollo y los Pull Requests ejecuten estrictamente la fase de Integración Continua (compilación, pruebas y análisis estático) sin disparar despliegues, reservando la fase de Despliegue Continuo exclusivamente para las fusiones en las ramas troncales de producción (`master`/`main`).
* **Autonomía y Desacoplamiento en Entornos de Nube:** Diseñar aplicaciones desacopladas de infraestructuras rígidas locales (mediante bases de datos embebidas en memoria como H2 y mapeo dinámico de puertos mediante variables de entorno `${PORT}`) resulta indispensable para lograr contenedores portables que puedan ejecutarse en plataformas *Serverless* o *PaaS* como Render de forma predecible y autónoma.
* **Importancia de las Pruebas Unitarias Aisladas:** El desacoplamiento de las capas web y de servicio mediante Mockito y MockMvc es un pilar fundamental en arquitecturas empresariales. Evitar el levantamiento de infraestructura externa en pruebas unitarias previene falsos negativos en CI/CD y asegura una retroalimentación inmediata al desarrollador.
* **Sincronización Rigurosa en la Configuración de Pipelines:** La experiencia con GitHub Actions evidenció que pequeños desajustes en la nomenclatura de ramas (`master` frente a `main`) o en la definición de eventos de disparo pueden deshabilitar silenciosamente la ejecución de controles de calidad. La definición explícita de disparadores y la configuración de reglas de protección de ramas (*Branch Protection Rules*) son indispensables para evitar fusiones accidentales de código sin validar.
* **Compatibilidad de Herramientas y JDKs Modernos:** El uso de versiones vanguardistas de Java (Java 23) requiere una estricta alineación de las librerías que realizan manipulación de bytecode y procesamiento de anotaciones (como Lombok). Emplear imágenes base fijas y específicas en Docker (`maven:3.9-eclipse-temurin-23`) en lugar de etiquetas flotantes (`latest`) previene comportamientos erráticos e incompatibilidades imprevistas en los entornos de compilación.
* **Valor del Análisis Estático Continuo:** La integración coordinada de JaCoCo y SonarCloud dentro de la tubería de CI/CD promueve una cultura de desarrollo guiada por la calidad, permitiendo que cada Pull Request sea evaluado de forma automática en términos de cobertura, seguridad y mantenibilidad antes de incorporarse a la rama principal.
