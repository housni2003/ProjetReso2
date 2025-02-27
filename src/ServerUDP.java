import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ServerUDP {
    private int port;
    private Map<String, InetSocketAddress> clients; // Stocke les clients avec leur pseudo comme clé et leur adresse comme valeur

    public ServerUDP(int port) {
        this.port = port;
        this.clients = new HashMap<>();
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

                // Si le message contient le pseudo du client
                if (message.startsWith("pseudo:")) {
                    // Le client envoie son pseudo au serveur
                    String pseudo = message.split(":")[1].trim();

                    // Enregistrement du client avec son pseudo
                    if (!clients.containsKey(pseudo)) {
                        clients.put(pseudo, clientAddress);
                        System.out.println("Nouveau client ajouté : " + pseudo + " (" + clientAddress + ")");
                    } else {
                        System.out.println("Le pseudo " + pseudo + " est déjà utilisé.");
                    }

                } else if (message.startsWith("to:")) {
                    // Le message est destiné à un autre client
                    String[] parts = message.split(":", 3); // Format : to:<pseudo>:<message>
                    if (parts.length == 3) {
                        String pseudoDestinataire = parts[1].trim();
                        String messageDestinataire = parts[2].trim();

                        // Vérifier si le destinataire est dans la liste des clients
                        InetSocketAddress destinataire = clients.get(pseudoDestinataire);
                        if (destinataire != null) {
                            // Envoie du message au destinataire
                            byte[] reponseData = messageDestinataire.getBytes();
                            DatagramPacket paquetEnvoye = new DatagramPacket(
                                    reponseData, reponseData.length, destinataire.getAddress(), destinataire.getPort());
                            socketServeur.send(paquetEnvoye);
                            System.out.println("Message envoyé à " + pseudoDestinataire + ": " + messageDestinataire);
                        } else {
                            System.out.println("Le client " + pseudoDestinataire + " n'est pas connecté.");
                        }
                    }
                } else {
                    System.out.println("Message non compris: " + message);
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
