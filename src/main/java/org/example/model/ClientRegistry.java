package org.example.model;

import org.example.server.ClientHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClientRegistry {
    private final Map<String, ClientHandler> clientesConectados = new ConcurrentHashMap<>();

    public String registrarNombreUnico(String nombrePropuesto, ClientHandler handler) {
        String base = nombrePropuesto == null ? "USUARIO" : nombrePropuesto.trim().replace(" ", "_");
        if (base.isEmpty()) {
            base = "USUARIO";
        }

        String candidato = base.toUpperCase();
        int contador = 1;

        while (clientesConectados.containsKey(candidato)) {
            candidato = (base + "_" + contador).toUpperCase();
            contador++;
        }

        clientesConectados.put(candidato, handler);
        System.out.println("[SISTEMA] Cliente registrado: " + candidato);
        return candidato;
    }

    public void quitarCliente(String nombreUsuario) {
        if (nombreUsuario != null) {
            clientesConectados.remove(nombreUsuario.toUpperCase());
            System.out.println("[SISTEMA] Cliente desconectado: " + nombreUsuario);
        }
    }

    public String listarClientes() {
        if (clientesConectados.isEmpty()) {
            return "No hay clientes conectados.";
        }

        return "Clientes conectados: " + String.join(", ", clientesConectados.keySet());
    }

    public void enviarATodos(String emisor, String mensaje) {
        for (ClientHandler cliente : clientesConectados.values()) {
            if (!cliente.getNombreUsuario().equalsIgnoreCase(emisor)) {
                cliente.enviarMensaje("[TODOS] " + emisor + ": " + mensaje);
            }
        }
    }

    public String enviarAUno(String emisor, String receptor, String mensaje) {
        ClientHandler destino = clientesConectados.get(receptor.toUpperCase());

        if (destino != null) {
            destino.enviarMensaje("[PRIVADO] " + emisor + ": " + mensaje);
            return "Mensaje enviado a " + receptor + ".";
        }

        return "Usuario no encontrado: " + receptor;
    }

    public String enviarAMultiples(String emisor, String[] receptores, String mensaje) {
        StringBuilder inexistentes = new StringBuilder();
        boolean algunoExiste = false;

        for (String receptor : receptores) {
            String destino = receptor.trim().toUpperCase();
            if (destino.isEmpty()) {
                continue;
            }

            ClientHandler handler = clientesConectados.get(destino);
            if (handler != null) {
                handler.enviarMensaje("[PRIVADO] " + emisor + ": " + mensaje);
                algunoExiste = true;
            } else {
                if (!inexistentes.isEmpty()) {
                    inexistentes.append(", ");
                }
                inexistentes.append(destino);
            }
        }

        if (algunoExiste && inexistentes.isEmpty()) {
            return "Mensaje enviado a los destinatarios existentes.";
        }

        if (algunoExiste) {
            return "Mensaje enviado a algunos destinatarios. No encontrados: " + inexistentes;
        }

        return "Ninguno de los destinatarios existe: " + inexistentes;
    }
}