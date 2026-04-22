# Práctica: Socket + Threads en Java

Proyecto base orientado a comunicación por **sockets** y manejo de concurrencia con **threads**.

## 1) Objetivo de la actividad

Implementar un sistema de chat cliente-servidor en Java que permita:

- Conexión simultánea de múltiples clientes.
- Registro de usuarios con nombre único.
- Envío de mensajes:
  - Globales (broadcast a todos).
  - Privados (a un usuario específico).
  - Privados múltiples (a varios destinatarios).
- Consulta de usuarios conectados.
- Desconexión ordenada de clientes.

## 2) Tecnologías y herramientas

- **Java** (proyecto Gradle con plugin `java`)
- **Gradle Wrapper** (`./gradlew`) para compilación y ejecución de tareas
- **JUnit 6 / JUnit Jupiter** (dependencias de test ya configuradas)

---

## 3) Estructura del proyecto

```text
practica-socket-thread/
├── build.gradle
├── settings.gradle
├── gradlew / gradlew.bat
└── src/main/java/org/example/
    ├── MainServidor.java
    ├── MainCliente.java
    ├── model/
    │   └── ClientRegistry.java
    ├── server/
    │   └── ClientHandler.java
    └── utils/
        └── Translator.java
```

### Descripción de clases

- **`MainServidor`**: punto de entrada previsto para iniciar el servidor.
- **`MainCliente`**: punto de entrada previsto para iniciar un cliente.
- **`ClientHandler`**: clase prevista para gestionar cada conexión cliente en un hilo independiente.
- **`ClientRegistry`**: clase implementada para registrar, listar y enrutar mensajes entre clientes conectados.
- **`Translator`**: utilitario reservado para lógica auxiliar (por ejemplo, parseo de comandos/protocolo).

## 4) Guía sugerida de implementación (paso a paso)

1. **Servidor (`MainServidor`)**
   - Abrir `ServerSocket` en un puerto fijo o configurable.
   - Aceptar conexiones en loop.
   - Crear un `ClientHandler` por conexión y lanzarlo en un nuevo hilo.

2. **Manejador por cliente (`ClientHandler`)**
   - Negociar/recibir nombre de usuario al conectarse.
   - Registrar cliente en `ClientRegistry`.
   - Leer comandos del cliente y derivarlos a la operación correspondiente.
   - Gestionar desconexión y cleanup en `finally`.

3. **Cliente (`MainCliente`)**
   - Conectar al servidor con `Socket`.
   - Tener dos flujos concurrentes:
     - Lectura de consola para enviar mensajes/comandos.
     - Lectura de socket para mostrar mensajes entrantes.

4. **Protocolo de comandos (sugerencia)**
   - `/all <mensaje>`
   - `/msg <usuario> <mensaje>`
   - `/msgm <u1,u2,...> <mensaje>`
   - `/list`
   - `/exit`

5. **Validaciones mínimas**
   - Evitar nombres vacíos.
   - Manejar destinatarios inexistentes.
   - No reenviar al mismo emisor en broadcast.

---

## 5) Ejemplo de flujo esperado

1. Iniciar servidor.
2. Conectar Cliente A (`Ana`).
3. Conectar Cliente B (`Luis`).
4. `Ana` ejecuta `/all Hola a todos`.
5. `Luis` ejecuta `/msg Ana Hola Ana`.
6. `Ana` ejecuta `/list` y obtiene usuarios conectados.
7. `Luis` ejecuta `/exit`.

---
