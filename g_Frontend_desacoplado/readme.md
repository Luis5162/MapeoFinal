# Sistema de Gestión de Requisitos y Proyectos (Arquitectura Desacoplada)

Este proyecto es una aplicación web robusta diseñada bajo una **arquitectura desacoplada**, separando completamente la lógica de negocio (Backend) de la interfaz de usuario (Frontend). Permite la gestión integral de Empresas, Proyectos, Requisitos de Software y Diagramas UML.

##  Tecnologías Aplicadas

### Backend (API REST)
* **Lenguaje:** Java 17+
* **Framework:** Spring Boot 3 (Web, JPA, Validation)
* **Base de Datos:** MySQL 8
* **ORM:** Hibernate
* **Migración de BD:** Flyway (Control de versiones de base de datos)
* **Herramientas:** Lombok, Maven

### Frontend (Cliente Web)
* **Lenguaje:** Python 3.10+
* **Framework Web:** Flask
* **Consumo de API:** Librería `requests`
* **Manejo de Formularios:** Flask-WTF y WTForms
* **Motor de Plantillas:** Jinja2
* **Estilos:** Bootstrap 5 (Responsive, Badges, Switches)

---

## 📂 Estructura del Proyecto

A continuación se muestra la estructura de directorios simulada del sistema completo (Frontend + Backend):

```text
.
├── backend-java/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/org/uacm/mapeo/
│   │   │   │   ├── controladores/    # Endpoints REST (EmpresaController, etc.)
│   │   │   │   ├── entidades/        # Modelos BD (Empresa, Requisito...)
│   │   │   │   ├── repositorios/     # Interfaces JPA
│   │   │   │   └── MapeoApplication.java
│   │   │   └── resources/
│   │   │       ├── db/migration/     # Scripts SQL (V1, V2, V3...)
│   │   │       └── application.properties
│   └── pom.xml
│
├── frontend-python/
│   ├── static/
│   │   └── css/                      # Estilos personalizados
│   ├── templates/
│   │   ├── form_generico.html        # Renderizado dinámico con WTForms
│   │   ├── lista_empresas.html       # Tabla con paginación
│   │   ├── lista_requisitos.html     # Tabla anidada (Proyecto -> Requisito)
│   │   └── nueva_empresa.html
│   │   │ ...
│   ├── venv/                         # Entorno virtual
│   ├── forms.py                      # Clases de Formulario (WTForms)
│   ├── web.py                        # Controlador principal (Flask)
│   └── requirements.txt
└── README.md

## Instalación y Configuración
Sigue estos pasos para levantar el entorno de desarrollo desde cero.

1. Prerrequisitos
Asegúrate de tener instalado:

- Java JDK 17 o superior.
- Python 3.10 o superior.
- MySQL Server (corriendo en el puerto 3306).
- Maven (opcional si usas el wrapper mvnw).

2. Configuración de Base de Datos
Crea una base de datos vacía en MySQL. Flyway se encargará de crear las tablas automáticamente.

SQL

CREATE DATABASE mapeo_db;
NOTA Asegúrate de que las credenciales en application.properties (Backend) coincidan con tu usuario de MySQL.

3. Ejecución del Backend (Java)
Navega a la carpeta del proyecto Java y ejecuta:

Bash

# Opción A: Usando Maven Wrapper
./mvnw spring-boot:run

# Opción B: Si tienes Maven instalado
mvn spring-boot:run
Verificación: El servidor iniciará en http://localhost:8080.

Flyway ejecutará automáticamente los scripts V1, V2 y V3 para crear tablas y poblar datos iniciales.

4. Ejecución del Frontend (Python)
Navega a la carpeta del proyecto Python.

Paso A: Crear entorno virtual (Recomendado)

Bash
    python -m venv venv
    # Activar en Windows:
    venv\Scripts\activate
    # Activar en Linux/Mac:
    source venv/bin/activate
Paso B: Instalar dependencias

Bash

    pip install flask flask-wtf requests bootstrap-flask
Paso C: Iniciar el servidor

Bash

python web.py
Verificación: El servidor iniciará en http://127.0.0.1:5000.

🖥️ Cómo Ejecutar el Proyecto
Una vez que ambas terminales (Java y Python) estén corriendo:

Abre tu navegador web favorito (Chrome, Edge, Firefox).

Ingresa a la dirección del Frontend:

https://www.google.com/url?sa=E&source=gmail&q=http://127.0.0.1:5000

Navega por las opciones del menú:

Empresas: Podrás listar, crear (con validación), editar y eliminar empresas. Verás los nuevos campos de contacto.

Requisitos: Visualiza la lista paginada y observa cómo se trae el nombre del Proyecto relacionado.

Diagramas: Gestión de diagramas UML asociados a proyectos.

📝 Notas del Desarrollador
Paginación: Tanto en Java (PageRequest) como en Python, la paginación está configurada para mostrar 5 elementos por página.

Validación: Se utiliza form.validate_on_submit() en Python para asegurar la integridad de los datos antes de enviarlos al Backend.

Comunicación: El Frontend no conecta a BD. Todo el tráfico de datos viaja vía JSON a través de HTTP Requests hacia el Backend.