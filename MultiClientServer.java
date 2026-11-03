import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MultiClientServer {
    private static final int PUERTO = 8080;
    private static final int MAX_HILOS = 10;
    private static final ExecutorService pool =
        Executors.newFixedThreadPool(MAX_HILOS);
    private static final RoomManager roomManager = new RoomManager();
    private static volatile boolean apagando = false;

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            apagando = true;
            System.out.println("Iniciando apagado controlado...");
            ClientHandler.notificarApagado();
            pool.shutdown();
            try {
                if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                }
            } catch (InterruptedException e) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));

        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor JSMS escuchando en puerto " + PUERTO);

            while (!apagando) {
                try {
                    Socket socket = servidor.accept();
                    System.out.println("Nuevo cliente: " +
                        socket.getInetAddress());
                    pool.execute(new ClientHandler(socket, roomManager));
                } catch (SocketException e) {
                    if (!apagando) {
                        System.err.println("Error aceptando conexión: " +
                            e.getMessage());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error del servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
