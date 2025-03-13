import java.net.*;
import java.util.Scanner;

public class ClientUDP {
    private DatagramSocket socketClient;
    private InetAddress adresseServeur;
    private int portServeur;
    private String pseudo;
    private boolean enCoursExecution = true;

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

    private void enregistrer() {
        envoyerMessage("register:" + pseudo);
    }

    public void envoyerMessage(String message) {
        try {
            byte[] envoyees = message.getBytes();
            DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, adresseServeur, portServeur);
            socketClient.send(paquetEnvoye);
        } catch (Exception e) {
            System.err.println("Erreur d'envoi: " + e.getMessage());
        }
    }

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

    public void fermerConnexion() {
        envoyerMessage("disconnect:");
        enCoursExecution = false;
        socketClient.close();
        System.out.println("Connexion fermée.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez votre pseudo: ");
        String pseudo = scanner.nextLine();

        ClientUDP client = new ClientUDP(pseudo, "localhost", 6666);

        Thread receptionThread = new Thread(client::recevoirMessage);
        receptionThread.start();

        while (true) {
            System.out.print("Entrez un message (destinataire:message ou 'broadcast:message') : ");
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit")) {
                client.fermerConnexion();
                break;
            }
            client.envoyerMessage(message);
        }

        scanner.close();
    }
}
