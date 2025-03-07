import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ServerUDP {
    private int port;
    private Map<String, InetSocketAddress> clients = new HashMap<>();

    public ServerUDP(int port) {
        this.port = port;
    }

    public void start() {
        try (DatagramSocket socketServeur = new DatagramSocket(port)) {
            System.out.println("Serveur UDP démarré sur le port " + port);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket paquetRecu = new DatagramPacket(buffer, buffer.length);

                // Attente de réception d'un message
                socketServeur.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                InetAddress clientAddress = paquetRecu.getAddress();
                int clientPort = paquetRecu.getPort();

                System.out.println("Message reçu de " + clientAddress + ":" + clientPort + " → " + message);

                String[] parts = message.split(":", 2);
                if (parts.length < 2) continue;

                String command = parts[0];
                String content = parts[1];

                // Enregistrement d'un nouvel utilisateur
                if (command.equals("register")) {
                    clients.put(content, new InetSocketAddress(clientAddress, clientPort));
                    System.out.println("👤 Nouvel utilisateur enregistré : " + content);
                    envoyerMessage(socketServeur, new InetSocketAddress(clientAddress, clientPort), "Bienvenue " + content + " !");
                }
                // Message privé
                else if (clients.containsKey(command)) {
                    InetSocketAddress destClient = clients.get(command);
                    envoyerMessage(socketServeur, destClient, "💬 Message de " + getPseudo(clientAddress, clientPort) + " : " + content);
                }
                // Message broadcast (envoyé à tout le monde)
                else if (command.equals("broadcast")) {
                    for (InetSocketAddress destClient : clients.values()) {
                        envoyerMessage(socketServeur, destClient, "Broadcast de " + getPseudo(clientAddress, clientPort) + " : " + content);
                    }
                    System.out.println("Message broadcast envoyé à tous les clients.");
                } else {
                    envoyerMessage(socketServeur, new InetSocketAddress(clientAddress, clientPort), "Utilisateur inconnu.");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur dans le serveur: " + e.getMessage());
        }
    }

    private void envoyerMessage(DatagramSocket socket, InetSocketAddress dest, String message) throws Exception {
        byte[] data = message.getBytes();
        DatagramPacket paquet = new DatagramPacket(data, data.length, dest.getAddress(), dest.getPort());
        socket.send(paquet);
        System.out.println("Message envoyé à " + dest);
    }

    private String getPseudo(InetAddress address, int port) {
        for (Map.Entry<String, InetSocketAddress> entry : clients.entrySet()) {
            if (entry.getValue().getAddress().equals(address) && entry.getValue().getPort() == port) {
                return entry.getKey();
            }
        }
        return "Inconnu";
    }

    public static void main(String[] args) {
        int port = 6666; // Choisir un port d'écoute
        ServerUDP server = new ServerUDP(port);
        server.start();
    }
}
