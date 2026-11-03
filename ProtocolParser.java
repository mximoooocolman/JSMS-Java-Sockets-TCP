public class ProtocolParser {
    public Command parsear(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            throw new IllegalArgumentException("El comando está vacío.");
        }

        String[] partes = linea.trim().split("\\s+", 2);
        String tipo = partes[0].toUpperCase();
        String argumento = partes.length > 1 ? partes[1].trim() : "";

        switch (tipo) {
            case "CONNECT":
            case "JOIN":
            case "MSG":
                if (argumento.isEmpty()) {
                    throw new IllegalArgumentException(
                        "El comando " + tipo + " requiere un argumento.");
                }
                break;
            case "QUIT":
                if (!argumento.isEmpty()) {
                    throw new IllegalArgumentException(
                        "QUIT no admite argumentos.");
                }
                break;
            default:
                throw new IllegalArgumentException(
                    "Comando no reconocido: " + tipo);
        }
        return new Command(tipo, argumento);
    }
}
