import java.net.*;

public class ClientUDP {
    private DatagramSocket socketClient;
    private InetAddress adresseClient;
    private byte[] envoyees; // tampon d'émission
    private byte[] recues = new byte[1024]; // tampon de réception

    public ClientUDP() {
        try {
            // Initialisation des attributs dans le constructeur
            socketClient = new DatagramSocket();
            adresseClient = InetAddress.getByName("localhost");
            System.out.println("Client UDP initialisé.");
        } catch (UnknownHostException e) {
            System.err.println("Erreur: Hôte inconnu.");
        } catch (SocketException e) {
            System.err.println("Erreur: Impossible de créer le socket.");
        }
    }

    public void EmettreMessage(String message) {
        try {
            // 2 - Émettre
            envoyees = message.getBytes();
            DatagramPacket messageEnvoye = new DatagramPacket(envoyees, envoyees.length, adresseClient, 6666);
            socketClient.send(messageEnvoye);
            System.out.println("Message envoyé: " + message);
        } catch (Exception e) {
            System.err.println("Erreur d'envoi: " + e.getMessage());
        }
    }

    public void recevoirMessage() {
        try {
            // 3 - Recevoir
            DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
            socketClient.receive(paquetRecu);
            String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
            System.out.println("Depuis le serveur: " + reponse);
        } catch (Exception e) {
            System.err.println("Erreur de réception: " + e.getMessage());
        }
    }
}
