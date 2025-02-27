import java.net.*;
import java.util.Scanner;

public class ClientUDP {
    private DatagramSocket socketClient;
    private InetAddress adresseServeur;
    private int portServeur;

    public ClientUDP(String host, int port) {
        try {
            socketClient = new DatagramSocket();
            adresseServeur = InetAddress.getByName(host);
            this.portServeur = port;
            System.out.println("Client connecté au serveur UDP " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("Erreur Client: " + e.getMessage());
        }
    }

    public void envoyerMessage(String message) {
        try {
            byte[] envoyees = message.getBytes();
            DatagramPacket paquet = new DatagramPacket(envoyees, envoyees.length, adresseServeur, portServeur);
            socketClient.send(paquet);
        } catch (Exception e) {
            System.err.println("Erreur d'envoi: " + e.getMessage());
        }
    }

    public void recevoirMessage() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket paquetRecu = new DatagramPacket(buffer, buffer.length);
            socketClient.receive(paquetRecu);
            String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());

            System.out.println("Nouveau message : " + reponse);
        } catch (Exception e) {
            System.err.println("Erreur de réception: " + e.getMessage());
        }
    }

    public void fermerConnexion() {
        socketClient.close();
        System.out.println("Connexion fermée.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez votre pseudo : ");
        String pseudo = scanner.nextLine();

        ClientUDP client = new ClientUDP("localhost", 6666);

        // Envoyer le pseudo au serveur pour l'enregistrer
        client.envoyerMessage("pseudo:" + pseudo);

        // Thread pour écouter les messages entrants
        new Thread(() -> {
            while (true) {
                client.recevoirMessage();
            }
        }).start();

        // Interaction avec l'utilisateur pour envoyer des messages
        while (true) {
            System.out.print("Entrez le pseudo du destinataire (ou tapez 'exit' pour quitter) : ");
            String destinataire = scanner.nextLine();
            if (destinataire.equalsIgnoreCase("exit")) {
                break;
            }

            System.out.print("Entrez votre message : ");
            String message = scanner.nextLine();
            client.envoyerMessage("to:" + destinataire + ":" + message);
        }

        client.fermerConnexion();
    }
}
