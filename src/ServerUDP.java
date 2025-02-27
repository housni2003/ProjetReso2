import java.net.*;

public class ServerUDP {

    private int port;

    // Constructeur qui permet de spécifier le port d'écoute
    public ServerUDP(int port) {
        this.port = port;
    }

    // Méthode pour démarrer le serveur
    public void start() {
        try (DatagramSocket socketServeur = new DatagramSocket(port)) {
            System.out.println("Serveur UDP démarré sur le port " + port + "...");

            while (true) { // Boucle infinie pour écouter plusieurs requêtes
                byte[] recues = new byte[1024]; // Tampon pour les données reçues
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);

                // Attente de la réception d'un paquet
                socketServeur.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                System.out.println("Message reçu: " + message);

                // Répondre au client avec un accusé de réception
                InetAddress adrClient = paquetRecu.getAddress();
                int portClient = paquetRecu.getPort();
                String reponse = "Accusé de réception du serveur UDP";
                byte[] envoyees = reponse.getBytes();
                DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, adrClient, portClient);
                socketServeur.send(paquetEnvoye);

                System.out.println("Réponse envoyée au client.");
            }
        } catch (Exception e) {
            System.err.println("Erreur dans le serveur: " + e.getMessage());
        }
    }

    // Point d'entrée du programme (démarrage manuel du serveur)
    public static void main(String[] args) {
        int port = 6666; // Port par défaut
        ServerUDP serveur = new ServerUDP(port);
        serveur.start();
    }
}
