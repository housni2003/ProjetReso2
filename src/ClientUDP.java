import java.net.*;
import java.util.Scanner;

/**
 * Classe représentant un client UDP permettant d'envoyer et de recevoir des messages.
 */
public class ClientUDP {
    private DatagramSocket socketClient;
    private InetAddress adresseServeur;
    private int portServeur;
    private String pseudo;

    /**
     * Constructeur du client UDP.
     *
     * @param pseudo         Le pseudo de l'utilisateur.
     * @param serveurAdresse L'adresse du serveur.
     * @param port           Le port du serveur.
     */
    public ClientUDP(String pseudo, String serveurAdresse, int port) {
        try {
            this.pseudo = pseudo;
            this.socketClient = new DatagramSocket();
            this.adresseServeur = InetAddress.getByName(serveurAdresse);
            this.portServeur = port;
            System.out.println("Client UDP initialisé en tant que " + pseudo);
            enregistrer();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du client: " + e.getMessage());
        }
    }

    /**
     * Enregistre l'utilisateur auprès du serveur en envoyant un message "register:pseudo".
     */
    private void enregistrer() {
        envoyerMessage("register:" + pseudo);
    }

    /**
     * Envoie un message au serveur.
     *
     * @param message Le message à envoyer.
     */
    public void envoyerMessage(String message) {
        try {
            byte[] envoyees = message.getBytes();
            DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, adresseServeur, portServeur);
            socketClient.send(paquetEnvoye);
            System.out.println("Message envoyé: " + message);
        } catch (Exception e) {
            System.err.println("Erreur d'envoi: " + e.getMessage());
        }
    }

    /**
     * Attend et affiche un message reçu du serveur.
     */
    public void recevoirMessage() {
        try {
            byte[] recues = new byte[1024];
            DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
            socketClient.receive(paquetRecu);
            String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
            System.out.println("Message du serveur: " + reponse);
        } catch (Exception e) {
            System.err.println("Erreur de réception: " + e.getMessage());
        }
    }

    /**
     * Ferme la connexion du client.
     */
    public void fermerConnexion() {
        socketClient.close();
        System.out.println("Connexion fermée.");
    }

    /**
     * Programme principal permettant d'exécuter le client UDP.
     *
     * @param args Arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre pseudo: ");
        String pseudo = scanner.nextLine();

        ClientUDP client = new ClientUDP(pseudo, "localhost", 6666);

        // Thread pour écouter les messages entrants
        Thread receptionThread = new Thread(() -> {
            while (true) {
                client.recevoirMessage();
            }
        });
        receptionThread.start();

        // Boucle pour envoyer des messages
        while (true) {
            System.out.print("Entrez un message (destinataire:message ou 'broadcast:message') : ");
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit")) break;
            client.envoyerMessage(message);
        }

        client.fermerConnexion();
        scanner.close();
    }
}
