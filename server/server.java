package server;// Java program to illustrate Server side
// Implementation using DatagramSocket

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class server {

    static final Integer PORT = 1234;
    static final Integer BUFFER_SIZE = 65535;


    public static void main(String[] args) throws IOException {
        try {
            // Step 1 : Create a socket to listen at port 1234
            DatagramSocket ds = new DatagramSocket(PORT);
            byte[] receive = new byte[BUFFER_SIZE];

            DatagramPacket dpReceive = null;
            while (true) {

                // parse received packet
                dpReceive = new DatagramPacket(receive, BUFFER_SIZE);
                ds.receive(dpReceive);

                System.out.println("Client:-" + data(receive));

                // Exit the server if the client sends "bye"
                if (data(receive).toString().equals("exit")) {
                    break;
                }

                // Clear the buffer after every message.
                receive = new byte[BUFFER_SIZE];
            }

            System.out.println("Shutting down server");
            ds.close();
            System.exit(0);

        } catch (Exception e) {
            System.out.println("Server Crashed");
            e.printStackTrace();
            System.exit(1);
        }
    }

    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data(byte[] a) {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0) {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
