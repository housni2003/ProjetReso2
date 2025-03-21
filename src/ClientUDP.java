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
    private boolean enCoursExecution = true;

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
     * Si le pseudo est déjà pris, il redemande un autre pseudo.
     */
    private void enregistrer() {
        boolean pseudoValide = false;
        while (!pseudoValide) {
            envoyerMessage("register:" + pseudo);

            // Attente d'une réponse du serveur
            try {
                byte[] recues = new byte[1024];
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                socketClient.receive(paquetRecu);
                String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());

                if (reponse.equals("Pseudo déjà pris. Veuillez en choisir un autre.")) {
                    // Si le serveur dit que le pseudo est déjà pris, redemander un autre pseudo
                    System.out.println("Le pseudo est déjà pris. Veuillez en choisir un autre.");
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Entrez un nouveau pseudo: ");
                    pseudo = scanner.nextLine();
                } else {
                    System.out.println(reponse);  // Message de bienvenue
                    pseudoValide = true;
                }
            } catch (Exception e) {
                System.err.println("Erreur d'envoi: " + e.getMessage());
            }
        }
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
        } catch (Exception e) {
            System.err.println("Erreur d'envoi: " + e.getMessage());
        }
    }

    /**
     * Attend et affiche un message reçu du serveur.
     */
    public void recevoirMessage() {
        try {
            while (enCoursExecution) {
                byte[] recues = new byte[1024];
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                socketClient.receive(paquetRecu);
                String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                System.out.println("Message du serveur: " + reponse);
            }
        } catch (Exception e) {
            if (enCoursExecution) {
                System.err.println("Erreur de réception: " + e.getMessage());
            }
        }
    }

    /**
     * Ferme la connexion du client en envoyant un message de déconnexion.
     */
    public void fermerConnexion() {
        envoyerMessage("disconnect:");
        enCoursExecution = false;
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

        ClientUDP client = new ClientUDP(pseudo, "localhost", 1234);

        // Thread pour recevoir les messages
        Thread receptionThread = new Thread(client::recevoirMessage);
        receptionThread.start();

        while (true) {
            System.out.print("Entrez un message (destinataire:message, 'broadcast:message' ou 'list') : ");
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit")) {
                client.fermerConnexion();
                break;
            } else if (message.equalsIgnoreCase("list")) {
                client.envoyerMessage("list:");
            } else {
                client.envoyerMessage(message);
            }
        }

        scanner.close();
    }
}
