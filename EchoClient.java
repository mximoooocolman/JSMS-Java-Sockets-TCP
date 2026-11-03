import java.io.*;
import java.net.*;

public class EchoClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             BufferedReader teclado = new BufferedReader(
                 new InputStreamReader(System.in));
             BufferedReader entrada = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(
                 socket.getOutputStream(), true)) {

            System.out.println("Conectado. Escribí CHAO para salir.");
            String mensaje;
            while ((mensaje = teclado.readLine()) != null) {
                salida.println(mensaje);
                if ("CHAO".equalsIgnoreCase(mensaje.trim())) {
                    break;
                }
                String respuesta = entrada.readLine();
                if (respuesta == null) break;
                System.out.println("Servidor: " + respuesta);
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}
