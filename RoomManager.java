import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomManager {
    private final Map<String, List<ClientHandler>> salas =
        new ConcurrentHashMap<>();

    public void unirseASala(String nombreSala, ClientHandler cliente) {
        salas.computeIfAbsent(
            nombreSala, k -> new CopyOnWriteArrayList<>()
        ).addIfAbsent(cliente);
    }

    public void salirDeSalas(ClientHandler cliente) {
        for (List<ClientHandler> miembros : salas.values()) {
            miembros.remove(cliente);
        }
        salas.entrySet().removeIf(e -> e.getValue().isEmpty());
    }

    public void broadcast(String nombreSala, String mensaje,
                          ClientHandler emisor) {
        List<ClientHandler> miembros = salas.get(nombreSala);
        if (miembros == null) return;

        for (ClientHandler miembro : miembros) {
            if (miembro != emisor) {
                miembro.enviarMensajeDirecto(mensaje);
            }
        }
    }
}
