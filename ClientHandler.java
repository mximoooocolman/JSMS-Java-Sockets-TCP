import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private static final ConcurrentHashMap<String, ClientHandler> USUARIOS =
        new ConcurrentHashMap<>();

    private final Socket socket;
    private final RoomManager roomManager;
    private final ProtocolParser parser = new ProtocolParser();

    private PrintWriter salida;
    private String usuario;
    private String salaActual;

    public ClientHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(30000);
            socket.setKeepAlive(true);

            try (BufferedReader entrada = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(
                     socket.getOutputStream(), true)) {

                this.salida = out;
                enviarMensajeDirecto("Conectado. Use CONNECT usuario.");

                String linea;
                while ((linea = entrada.readLine()) != null) {
                    try {
                        procesar(parser.parsear(linea));
                    } catch (IllegalArgumentException ex) {
                        enviarMensajeDirecto("400 ERROR: " + ex.getMessage());
                    }
                }
            }
        } catch (SocketTimeoutException e) {
            enviarMensajeDirecto("408 TIMEOUT: cliente inactivo.");
        } catch (SocketException e) {
            System.out.println("Conexión interrumpida: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error de I/O: " + e.getMessage());
        } finally {
            limpiarConexion();
        }
    }

    private void procesar(Command comando) {
        String tipo = comando.getTipo();
        String argumento = comando.getArgumento();

        if (!tipo.equals("CONNECT") && usuario == null
                && !tipo.equals("QUIT")) {
            enviarMensajeDirecto("401 ERROR: primero debe ejecutar CONNECT.");
            return;
        }

        switch (tipo) {
            case "CONNECT":
                conectar(argumento);
                break;
            case "JOIN":
                unirse(argumento);
                break;
            case "MSG":
                enviarASala(argumento);
                break;
            case "QUIT":
                enviarMensajeDirecto("BYE");
                try { socket.close(); } catch (IOException ignored) {}
                break;
        }
    }

    private void conectar(String nombre) {
        if (usuario != null) {
            enviarMensajeDirecto("409 ERROR: ya está conectado como " + usuario);
            return;
        }
        ClientHandler anterior = USUARIOS.putIfAbsent(nombre, this);
        if (anterior != null) {
            enviarMensajeDirecto("401 ERROR: nombre de usuario ocupado.");
        } else {
            usuario = nombre;
            enviarMensajeDirecto("200 OK: usuario registrado " + usuario);
        }
    }

    private void unirse(String nombreSala) {
        if (usuario == null) return;
        if (salaActual != null) {
            roomManager.salirDeSalas(this);
        }
        salaActual = nombreSala;
        roomManager.unirseASala(nombreSala, this);
        enviarMensajeDirecto("JOINED " + nombreSala);
    }

    private void enviarASala(String texto) {
        if (salaActual == null) {
            enviarMensajeDirecto("400 ERROR: primero debe ejecutar JOIN sala.");
            return;
        }
        roomManager.broadcast(
            salaActual, "BROADCAST [" + usuario + "]: " + texto, this
        );
        enviarMensajeDirecto("200 MSG enviado.");
    }

    public synchronized void enviarMensajeDirecto(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
            salida.flush();
        }
    }

    private void limpiarConexion() {
        roomManager.salirDeSalas(this);
        if (usuario != null) {
            USUARIOS.remove(usuario, this);
        }
        try {
            if (!socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
        System.out.println("Cliente desconectado: " + usuario);
    }

    public static void notificarApagado() {
        for (ClientHandler cliente : USUARIOS.values()) {
            cliente.enviarMensajeDirecto(
                "SERVER_SHUTDOWN: el servidor se está apagando."
            );
        }
    }
}
