Task Manager CLI
Este es un proyecto personal desarrollado en Java para gestionar tareas desde la consola. El objetivo principal de este repositorio es aplicar principios de Clean Architecture y Programación Orientada a Objetos (POO) mientras avanzo en mi carrera de licenciatura en Informatica

Tecnologías y Conceptos Aplicados
Java 21.

Clean Architecture: Separación de responsabilidades en capas (model, repository, impl).

Encapsulamiento: Uso de modificadores de acceso, getters y setters para proteger la integridad de los datos.

Interfaces: Implementación de contratos para desacoplar la lógica de negocio del almacenamiento.

Java Time API: Gestión de fechas con LocalDate.

Arquitectura del Proyecto
El proyecto está organizado siguiendo una estructura que permite la escalabilidad:

model: Contiene la entidad Task, que representa el corazón de la aplicación.

repository: Define la interfaz (contrato) para las operaciones de persistencia.

impl: Implementación actual en memoria (InMemoryTaskRepository) usando ArrayList. Próximamente: Persistencia en base de datos o JSON.

Cómo ejecutarlo
Clona el repositorio:

Bash
git clone https://github.com/adrieljoshua22/task-manager-java.git
Abre el proyecto en tu IDE favorito (IntelliJ IDEA recomendado).

Ejecuta la clase Main.java.

Roadmap / Próximos pasos
[ ] Generación automática de IDs.

[ ] Persistencia de datos en archivos locales.

[ ] Unit Testing para la lógica del repositorio.

[ ] Implementar un sistema de prioridades para las tareas.
