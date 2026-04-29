package org.example;

import org.example.model.ClientRegistry;
import org.example.server.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//Se encarga de crear el servidor y de que cada cliente tenga su propio hilo.
public class MainServidor {
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        ClientRegistry registry = new ClientRegistry();

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("[SISTEMA] Servidor iniciado en el puerto " + PUERTO); //se crea el servidor

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SISTEMA] Nuevo cliente conectado desde " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, registry); //Crea un objeto ClientHandler
                handler.start(); //Inicia un hilo (cada ciente tiene su hilo)
            }
        } catch (IOException e) {
            System.out.println("[ERROR] No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}