package org.example.utils;

public class Translator {

    public static String traducir(String texto) {
        String upper = texto.toUpperCase();
//Convierte cada caracter en binario
        if (upper.startsWith("BINARIO")) {
            String sub = texto.length() > 8 ? texto.substring(8).trim() : "";
            StringBuilder sb = new StringBuilder();

            for (char c : sub.toCharArray()) {
                sb.append(Integer.toBinaryString(c)).append(" ");
            }

            return "Binario: " + sb.toString().trim();
        }
//convierte el texto a mayusculas
        if (upper.startsWith("MAYUS")) {
            String sub = texto.length() > 6 ? texto.substring(6).trim() : "";
            return "Mayús: " + sub.toUpperCase();
        }
// cuenta los caracteres del texto
        if (upper.startsWith("CONTAR")) {
            String sub = texto.length() > 7 ? texto.substring(7).trim() : "";
            return "Caracteres: " + sub.length();
        }

        return "Error en el traductor.";
    }
}