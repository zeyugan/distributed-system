package server;

import server.dto.FileSubscriptionDTO;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FileSubscriptionServer {

    private static byte[] receiveData = new byte[1024];

    private static DatagramSocket serverSocket = null;

    public static void main(String[] args) throws IOException {
        DatagramPacket receivePacket = null;
        String response;
        try {
            //server port number
            serverSocket = new DatagramSocket(1234);

            //For store the map of the subscribed files
            Map<String, FileSubscriptionDTO> subscriptions = new HashMap<>();

            while (true) {
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());

                //split the request by ,  format: fileName,timeDurationInMinutes
                String[] requestParts = request.split(",");

                int duration = Integer.parseInt(requestParts[1]);
                String filename = requestParts[0];
                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();

                if (subscriptions.containsKey(address + ":" + port) ) {
                    response = "There is an existing subscription for your specified file.";
                } else {
                    FileSubscriptionDTO fileSubscriptionDTO = populateFileSubscriptionDTO(address, port, filename, duration);
                    subscriptions.put(address + ":" + port, fileSubscriptionDTO);
                    response = "You are now registered for updates for " + duration + " minutes.";
                }

                sendMessagesToClient(response, receivePacket);

                //print out the list of subscriptions
                System.out.println("===========List of servers and subscripted files===========");
                subscriptions.forEach((key, value) -> System.out.println(key + ": " + value.toString()));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }

    }

    private static FileSubscriptionDTO populateFileSubscriptionDTO(InetAddress address, int port, String filename, long duration) {
        FileSubscriptionDTO fileSubscriptionDTO = new FileSubscriptionDTO();

        LocalDateTime currentDateTime = LocalDateTime.now();
        // Define a custom date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // Format and print the current date-time using the formatter
        String formattedCurrentDateTime = currentDateTime.format(formatter);
        fileSubscriptionDTO.setRegisterTimeStamp(formattedCurrentDateTime);
        fileSubscriptionDTO.setFileName(filename);
        fileSubscriptionDTO.setDuration(duration);
        return fileSubscriptionDTO;
    }

    private static void sendMessagesToClient(String message, DatagramPacket receivePacket) {
        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                receivePacket.getAddress(), receivePacket.getPort());
        try {
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}