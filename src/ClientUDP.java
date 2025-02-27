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
            System.out.println("📤 Message envoyé : " + message);
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

            InetAddress adrServeur = paquetRecu.getAddress();
            int portServeur = paquetRecu.getPort();
            System.out.println("📩 Nouveau message de " + adrServeur.getHostAddress() + ":" + portServeur + " -> " + reponse);
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
        ClientUDP client = new ClientUDP("localhost", 6666);

        // Thread pour écouter les messages entrants
        new Thread(() -> {
            while (true) {
                client.recevoirMessage();
            }
        }).start();

        while (true) {
            System.out.print("Votre message: ");
            String message = scanner.nextLine();
            client.envoyerMessage(message);
        }
    }
}
