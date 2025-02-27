import java.net.DatagramSocket;
import java.net.SocketException;

public class Main {
    public static void main(String[] args) throws SocketException {

        DatagramSocket test = new DatagramSocket(5);

        int startPort = 10000;  // Exemple : plage de test
        int endPort =30000;

        PortScannerUDP.scanUDPPorts(startPort, endPort);
    }
}

