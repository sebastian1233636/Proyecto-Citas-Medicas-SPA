# Sistema Web SPA de Gestión de Citas Médicas

Este proyecto es una implementación de un sistema web para la gestión de citas médicas en línea, desarrollado bajo el enfoque SPA (Single Page Application). El sistema permite a pacientes agendar citas con médicos especialistas, a los médicos gestionar su agenda de atención, y al administrador supervisar registros médicos. Toda la información se almacena en una base de datos **MySQL**.

## Tecnologías Utilizadas

- **Frontend (SPA):** React
- **Backend:** Java + Spring Boot (RESTful API)
- **Base de Datos:** MySQL
- **Seguridad:** JWT (JSON Web Token) para autenticación y autorización
- **Comunicación:** API RESTful mediante peticiones asíncronas (`fetch`)

## Arquitectura

El sistema está basado en una arquitectura cliente-servidor desacoplada:

- El **frontend** realiza el renderizado del lado del cliente (SPA).
- El **backend** expone servicios RESTful seguros con Spring Boot.
- La **autenticación y autorización** se gestionan mediante tokens **JWT**.
- Los datos se almacenan en una base de datos **relacional MySQL**.

## Integrantes del Proyecto

- Sebastián Álvarez Gómez  
- Esteban Sánchez Sánchez  
- Anthony Li Perera  

## Funcionalidades

### Autenticación y Registro

- Registro e inicio de sesión para **pacientes** y **médicos**.
- Control de acceso basado en **roles** mediante JWT.
- Navegación fluida entre login y registro.


### Gestión de Médicos

1. **Registro de médicos:** Médicos pueden registrarse. Requiere aprobación del administrador.
2. **Perfil:** Configuración de especialidad, localidad, horarios, frecuencia de atención, costo, foto y presentación.
3. **Gestión de citas:** Listado de citas, filtros por estado o paciente, orden cronológico, posibilidad de **completar citas** con anotaciones.


### Gestión de Pacientes

1. **Búsqueda de médicos:** Sin necesidad de iniciar sesión, búsqueda por especialidad y ubicación. Se listan horarios próximos 3 días.
2. **Horario extendido:** Visualización de citas disponibles avanzando o retrocediendo en el calendario.
3. **Reserva de cita:** Selección de espacio disponible. Si no ha iniciado sesión, se le solicita hacerlo.
4. **Confirmación de cita:** Visualización de resumen y confirmación o cancelación.
5. **Historial de citas:** Listado de citas anteriores con filtros por médico o estado.
6. **Registro de pacientes:** Formulario con ID, nombre y doble ingreso de clave.


### Gestión del Administrador

1. **Login de administrador:** Autenticación por ID y clave. El rol determina el acceso.
2. **Aprobación de médicos:** Listado de médicos registrados y opción de aprobar nuevos registros.


## Seguridad

- **Autenticación:** basada en JWT, sin uso de sesiones en el servidor.
- **Autorización:** acceso a endpoints controlado según el **rol del usuario**.
- **Persistencia de sesión:** tokens almacenados de forma segura (e.g., `localStorage`).
- **Validación:** cada petición protegida al backend valida el token JWT.


## Comunicación Cliente-Servidor

- Comunicación basada en **servicios RESTful**.
- Las peticiones asíncronas desde el frontend usan `fetch`.
- Las respuestas y solicitudes se manejan en formato **JSON**.
