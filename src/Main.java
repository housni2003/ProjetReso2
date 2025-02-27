import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {
    public static void main(String[] args) {
        int port = 6666;

        // Démarrage du serveur UDP dans un thread séparé
        Thread serverThread = new Thread(() -> {
            ServerUDP server = new ServerUDP(port);
            server.start();
        });
        serverThread.start();

        // Petite pause pour s'assurer que le serveur est prêt avant d'envoyer un message
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Création du client UDP
        ClientUDP client = new ClientUDP();
        client.EmettreMessage("Hello, serveur UDP !");
        client.recevoirMessage();
        client.fermerConnexion();

        // Arrêter proprement après le test
        System.exit(0);
    }
}


