package org.example.server;

import org.example.model.ClientRegistry;
import org.example.utils.Translator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//Cada cliente conectado tiene su propio ClientHandler.
public class ClientHandler extends Thread {
    private final Socket socket;
    private final ClientRegistry registry;

    private DataInputStream entrada;
    private DataOutputStream salida;
    private String nombreUsuario;

    public ClientHandler(Socket socket, ClientRegistry registry) {
        this.socket = socket;
        this.registry = registry;
    }

    @Override
    public void run() {
        try {
            entrada = new DataInputStream(socket.getInputStream()); //lee lo que manda el cliente
            salida = new DataOutputStream(socket.getOutputStream()); //para enviar respuestas
// se lee el nombre propuesto por el cliente, se valida y registra
            String nombrePropuesto = entrada.readUTF();
            nombreUsuario = registry.registrarNombreUnico(nombrePropuesto, this);

            enviarMensaje(
                    "--- BIENVENIDO " + nombreUsuario + " ---\n" +
                            "Comandos disponibles:\n" +
                            "  AYUDA\n" +
                            "  FECHA\n" +
                            "  LISTAR\n" +
                            "  BINARIO <texto>\n" +
                            "  MAYUS <texto>\n" +
                            "  CONTAR <texto>\n" +
                            "  ALL <mensaje>\n" +
                            "  <usuario> <mensaje>\n" +
                            "  <usuario1,usuario2> <mensaje>\n" +
                            "  EXIT"
            );
//while de lectura, espera mensajes del cliente
            while (true) {
                String peticion = entrada.readUTF();
                System.out.println("[LOG][" + nombreUsuario + "] " + peticion);

                if (peticion == null) {
                    continue;
                }

                peticion = peticion.trim();
                if (peticion.isEmpty()) {
                    enviarMensaje("No se recibió ningún comando.");
                    continue;
                }

                if (peticion.equalsIgnoreCase("EXIT")) {
                    enviarMensaje("Desconectando...");
                    break;
                }

                procesarComando(peticion);
            }
        } catch (IOException e) {
            System.out.println("[ERROR][" + nombreUsuario + "] Conexión perdida: " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    private void procesarComando(String msg) throws IOException {
        String upper = msg.toUpperCase();

        if (upper.equals("AYUDA")) {
            enviarMensaje(
                    "AYUDA:\n" +
                            "- FECHA: muestra fecha y hora\n" +
                            "- LISTAR: muestra clientes conectados\n" +
                            "- BINARIO texto: convierte a binario\n" +
                            "- MAYUS texto: convierte a mayúsculas\n" +
                            "- CONTAR texto: cuenta caracteres\n" +
                            "- ALL mensaje: envía a todos\n" +
                            "- USUARIO mensaje: envía a un cliente\n" +
                            "- USUARIO1,USUARIO2 mensaje: envía a varios\n" +
                            "- EXIT: salir"
            );
            return;
        }
//IF donde se procesan los comandos.
        if (upper.equals("FECHA")) {
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            enviarMensaje("Fecha y hora actual: " + fecha);
            return;
        }

        if (upper.equals("LISTAR")) {
            enviarMensaje(registry.listarClientes());
            return;
        }

        if (upper.startsWith("BINARIO") || upper.startsWith("MAYUS") || upper.startsWith("CONTAR")) {
            enviarMensaje(Translator.traducir(msg));
            return;
        }

        if (upper.startsWith("ALL ")) {
            String contenido = msg.substring(4).trim();
            if (contenido.isEmpty()) {
                enviarMensaje("Debe escribir un mensaje después de ALL.");
                return;
            }

            registry.enviarATodos(nombreUsuario, contenido);
            enviarMensaje("Mensaje enviado a todos.");
            return;
        }

        int primerEspacio = msg.indexOf(' ');
        if (primerEspacio == -1) {
            enviarMensaje("Comando inválido. Use AYUDA para ver la ayuda.");
            return;
        }

        String destinos = msg.substring(0, primerEspacio).trim();
        String contenido = msg.substring(primerEspacio + 1).trim();

        if (contenido.isEmpty()) {
            enviarMensaje("Debe escribir un mensaje.");
            return;
        }

        if (destinos.contains(",")) {
            String[] lista = destinos.split(",");
            String resultado = registry.enviarAMultiples(nombreUsuario, lista, contenido);
            enviarMensaje(resultado);
            return;
        }

        String resultado = registry.enviarAUno(nombreUsuario, destinos, contenido);
        enviarMensaje(resultado);
    }

    public void enviarMensaje(String msg) {
        try {
            salida.writeUTF(msg);
            salida.flush();
        } catch (IOException e) {
            System.out.println("[ERROR][" + nombreUsuario + "] No se pudo enviar mensaje: " + e.getMessage());
        }
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    private void desconectar() { //se elimina el registro, se cierra el socket y se libera el hilo.
        registry.quitarCliente(nombreUsuario);

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("[ERROR][" + nombreUsuario + "] Error al cerrar conexión: " + e.getMessage());
        }
    }
}