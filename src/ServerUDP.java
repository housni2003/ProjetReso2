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
            System.out.println("Serveur UDP démarré sur " + getLocalIPAddress() + ":" + port);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket paquetRecu = new DatagramPacket(buffer, buffer.length);
                socketServeur.receive(paquetRecu);

                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                InetAddress clientAddress = paquetRecu.getAddress();
                int clientPort = paquetRecu.getPort();
                System.out.println("Message reçu de " + clientAddress + ":" + clientPort + " -> " + message);

                String[] parts = message.split(":", 2);
                if (parts.length < 2) continue;
                String command = parts[0];
                String content = parts[1];

                if (command.equals("register")) {
                    clients.put(content, new InetSocketAddress(clientAddress, clientPort));
                    System.out.println("Nouvel utilisateur enregistré : " + content);
                    envoyerMessage(socketServeur, new InetSocketAddress(clientAddress, clientPort), "Bienvenue " + content + " !");
                } else if (command.equals("broadcast")) {
                    broadcastMessage(socketServeur, "Message de " + getPseudo(clientAddress, clientPort) + " : " + content, clientAddress, clientPort);
                } else if (command.equals("disconnect")) {
                    removeClient(clientAddress, clientPort);
                } else if (command.equals("list")) {
                    StringBuilder listMessage = new StringBuilder("Utilisateurs connectés:\n");
                    for (String pseudo : clients.keySet()) {
                        listMessage.append(pseudo).append("\n");
                    }
                    envoyerMessage(socketServeur, new InetSocketAddress(clientAddress, clientPort), listMessage.toString());
                } else if (clients.containsKey(command)) {
                    InetSocketAddress destClient = clients.get(command);
                    envoyerMessage(socketServeur, destClient, "Message de " + getPseudo(clientAddress, clientPort) + " : " + content);
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

    private void broadcastMessage(DatagramSocket socket, String message, InetAddress senderAddress, int senderPort) throws Exception {
        for (InetSocketAddress client : clients.values()) {
            if (!client.getAddress().equals(senderAddress) || client.getPort() != senderPort) {
                envoyerMessage(socket, client, message);
            }
        }
        System.out.println("Broadcast envoyé.");
    }

    private void removeClient(InetAddress address, int port) {
        String pseudoToRemove = getPseudo(address, port);
        if (!pseudoToRemove.equals("Inconnu")) {
            clients.remove(pseudoToRemove);
            System.out.println(pseudoToRemove + " s'est déconnecté.");
        }
    }

    private String getPseudo(InetAddress address, int port) {
        for (Map.Entry<String, InetSocketAddress> entry : clients.entrySet()) {
            if (entry.getValue().getAddress().equals(address) && entry.getValue().getPort() == port) {
                return entry.getKey();
            }
        }
        return "Inconnu";
    }

    private String getConnectedUsers() {
        return String.join(", ", clients.keySet());
    }

    private String getLocalIPAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    public static void main(String[] args) {
        int port = 6666;
        ServerUDP server = new ServerUDP(port);
        server.start();
    }
}
