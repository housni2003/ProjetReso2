import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {
    public static void main(String[] args) throws SocketException {

        DatagramSocket test = new DatagramSocket(5);

        int startPort = 0;  // Exemple : plage de test
        int endPort =200;

        PortScannerUDP.scanUDPPorts(startPort, endPort);

        ClientUDP client1 = new ClientUDP();
    }
}

