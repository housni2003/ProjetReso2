import java.net.DatagramSocket;
import java.net.SocketException;

public class PortScannerUDP {

    public static void scanUDPPorts(int startPort, int endPort) {
        System.out.println("Scanning UDP ports from " + startPort + " to " + endPort + "...");

        for (int port = startPort; port <= endPort; port++) {
            try {
                DatagramSocket socket = new DatagramSocket(port);
                socket.close();
                System.out.println("Port " + port + " : OUVERT");
            } catch (SocketException e) {
                System.out.println("Port " + port + " : FERME");
            }
        }
    }
}

