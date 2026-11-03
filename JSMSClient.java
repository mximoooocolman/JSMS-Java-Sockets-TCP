import java.io.*;
import java.net.*;

public class JSMSClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             BufferedReader teclado = new BufferedReader(
                 new InputStreamReader(System.in));
             BufferedReader entrada = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(
                 socket.getOutputStream(), true)) {

            Thread receptor = new Thread(() -> {
                try {
                    String respuesta;
                    while ((respuesta = entrada.readLine()) != null) {
                        System.out.println("\n" + respuesta);
                    }
                } catch (IOException e) {
                    System.out.println("Conexión con el servidor finalizada.");
                }
            });
            receptor.setDaemon(true);
            receptor.start();

            System.out.println("Cliente JSMS. Comandos: CONNECT, JOIN, MSG, QUIT");
            String linea;
            while ((linea = teclado.readLine()) != null) {
                salida.println(linea);
                salida.flush();
                if ("QUIT".equalsIgnoreCase(linea.trim())) break;
            }
        } catch (IOException e) {
            System.err.println("No se pudo conectar: " + e.getMessage());
        }
    }
}
