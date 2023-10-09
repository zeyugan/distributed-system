package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class FileSubscriptionClient {
    public static void main(String[] args) {

        DatagramSocket clientSocket = null;

        try {
            clientSocket = new DatagramSocket(1236);
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 1234;  // Same port used by the server

            // request for the requested file for subscription
            String request = "locationOfSample.txt,30"; // Format: filename,duration
            byte[] sendData = request.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);


            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String notification = new String(receivePacket.getData(), 0, receivePacket.getLength());

            System.out.println("Received file subscription from server: " + notification);

        } catch (IOException  e) {
            e.printStackTrace();
        } finally {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
    }
}
