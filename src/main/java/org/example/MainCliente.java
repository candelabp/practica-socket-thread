package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MainCliente {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);

        try (Socket socket = new Socket("127.0.0.1", 5000); //se conecta al servidor
             DataInputStream entrada = new DataInputStream(socket.getInputStream()); //se lee lo que manda el servidor
             DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) { //para enviar mensajes al servidor

            System.out.print("Ingrese su nombre de usuario: ");
            String nombre = teclado.nextLine();
            salida.writeUTF(nombre);
            salida.flush();

            Thread listener = new Thread(() -> {
                try {
                    while (true) {
                        String msg = entrada.readUTF();
                        System.out.println("\n" + msg);
                        System.out.print("> ");
                    }
                } catch (IOException e) {
                    System.out.println("\n[CLIENTE] Desconectado del servidor.");
                }
            });
            listener.start();

            System.out.print("> ");
            while (true) {
                String input = teclado.nextLine();
                salida.writeUTF(input);
                salida.flush();

                if (input.equalsIgnoreCase("EXIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("[CLIENTE] Error: " + e.getMessage());
        }
    }
}