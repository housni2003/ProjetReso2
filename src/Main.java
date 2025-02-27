public class Main {
    public static void main(String[] args) {
        int port = 6666;

        // Démarrage du serveur UDP dans un thread
        Thread serverThread = new Thread(() -> {
            ServerUDP server = new ServerUDP(port);
            server.start();
        });
        serverThread.start();

        // Pause pour laisser le serveur démarrer
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Création et test du client UDP
        ClientUDP client = new ClientUDP("localhost", port);
        client.envoyerMessage();
        client.recevoirReponse();
        client.fermerConnexion();

        // Ferme l'application après le test
        System.exit(0);
    }
}
