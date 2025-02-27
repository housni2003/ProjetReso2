import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class ServerUDP {
    private int port;
    private Set<InetSocketAddress> clients; // Stocke les clients connectés

    public ServerUDP(int port) {
        this.port = port;
        this.clients = new HashSet<>();
    }

    public void start() {
        try (DatagramSocket socketServeur = new DatagramSocket(port)) {
            System.out.println("Serveur UDP RX302 démarré sur le port " + port + "...");

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket paquetRecu = new DatagramPacket(buffer, buffer.length);

                // Attente de réception d'un message client
                socketServeur.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());

                // Récupération de l'adresse et du port du client
                InetSocketAddress clientAddress = new InetSocketAddress(paquetRecu.getAddress(), paquetRecu.getPort());

                // Enregistrement du client s'il est nouveau
                if (!clients.contains(clientAddress)) {
                    clients.add(clientAddress);
                    System.out.println("Nouveau client : " + clientAddress);
                }

                System.out.println("📩 Nouveau message de " + clientAddress + " -> " + message);

                // Relayer le message à tous les clients sauf celui qui l'a envoyé
                for (InetSocketAddress client : clients) {
                    if (!client.equals(clientAddress)) { // Ne pas renvoyer au même client
                        byte[] reponseData = message.getBytes();
                        DatagramPacket paquetEnvoye = new DatagramPacket(
                                reponseData, reponseData.length, client.getAddress(), client.getPort());
                        socketServeur.send(paquetEnvoye);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur dans le serveur: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServerUDP serveur = new ServerUDP(6666);
        serveur.start();
    }
}
