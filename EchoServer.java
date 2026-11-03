import java.io.*;
import java.net.*;

public class EchoServer {
    public static void main(String[] args) {
        int puerto = 8080;

        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor de eco iniciado en puerto " + puerto);

            try (Socket cliente = servidor.accept();
                 BufferedReader entrada = new BufferedReader(
                     new InputStreamReader(cliente.getInputStream()));
                 PrintWriter salida = new PrintWriter(
                     cliente.getOutputStream(), true)) {

                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    if ("CHAO".equalsIgnoreCase(mensaje.trim())) {
                        break;
                    }
                    salida.println("ECO: " + mensaje);
                }
            }
        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }
}
