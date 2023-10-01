package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Scanner;

public class server {
    public static void main(String[] args) throws IOException {
        try {
            // Step 1 : Create a socket to listen at port 1234
            DatagramSocket ds = new DatagramSocket(1234);
            byte[] receive = new byte[65535];

            Scanner consoleSc = new Scanner(System.in);

            DatagramPacket DpReceive = null;

            while (true) {
                String inp = consoleSc.nextLine();
                // if user input exit, close the server
                if (inp.equals("exit")) {
                    System.out.println("Server closed");
                    ds.close();
                    break;
                }

                // Step 2 : create a DatgramPacket to receive the data.
                DpReceive = new DatagramPacket(receive, receive.length);

                // Step 3 : revieve the data in byte buffer.
                ds.receive(DpReceive);

                System.out.println("Client:-" + data(receive));

                // Clear the buffer after every message.
                receive = new byte[65535];

                System.out.println("Server is running...");
            }
        } catch (Exception e) {
            System.out.println("Server closed unexpectedly.");
            // print error message
            System.out.println(e.getMessage());
            // print stack trace
            e.printStackTrace();
            System.exit(1);
        }
        // print close success
        System.out.println("Server closed successfully.");
        System.exit(0);
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