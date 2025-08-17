# Proyecto HTTP Server en Java

Servidor HTTP en Java puro que muestra archivos estáticos y provee un servicio básico mediante un endpoint REST.

---

## 🚀 Comenzando

Estas instrucciones te permitirán obtener una copia del proyecto en funcionamiento en tu máquina local para propósitos de desarrollo y pruebas.

### 📋 Prerrequisitos

Necesitas tener instalado:

- Java 8 o superior

- Maven

---

### 🔧 Instalación

Pasos para ejecutar el servidor en tu entorno local:

1. Clonar el repositorio

    git clone https://github.com/Sebs2807/AREP-Taller1.git
   
    cd arep

3. Compilar el proyecto
    
    mvn clean install

4. Ejecutar el servidor

    mvn exec:java

El servidor quedará corriendo en el puerto **36000**


### ⚙️ Ejecución de pruebas

mvn test

### 📂 Estructura del proyecto

```text
│   pom.xml
│
├───src
│   ├───main
│   │   └───java
│   │       └───eci
│   │           └───escuelaing
│   │               └───edu
│   │                   └───co
│   │                           HttpServer.class
│   │                           HttpServer.java
│   │
│   └───test
│       └───java
│           └───eci
│               └───escuelaing
│                   └───edu
│                       └───co
│                               AppTest.java
│
└───www
    │   404.html
    │   index.html
    │   script.js
    │   style.css
    │
    └───images
            cielo.jpg
            footer.png
```

### 📡 Endpoints disponibles

- http://localhost:36000/ (Sirve index.html desde la carpeta www).

- http://localhost:36000/app/hello?name=TuNombre (Devuelve un JSON con un saludo).
  
- http://localhost:36000/[archivo] (Sirve cualquier recurso dentro de www).

### 🛠️ Construido con

- Java - Lenguaje de programación

- Maven - Gestión de dependencias y ejecución

### ✒️ Autores

Sebastian Velasquez - Sebs2807

