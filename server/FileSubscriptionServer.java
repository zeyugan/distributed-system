package server;

import server.common.CommonService;
import server.dto.FileSubscriptionDTO;
import server.dto.RequestDTO;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileSubscriptionServer {

    private static byte[] receiveData = new byte[1024];

    private static DatagramSocket serverSocket = null;

    private static Map<String, FileSubscriptionDTO> subscriptions = new HashMap<>();

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static void main(String[] args) throws IOException {
        DatagramPacket receivePacket = null;
        String response;
        try {
            //server port number
            serverSocket = new DatagramSocket(6666);

            //For store the map of the subscribed files

            while (true) {
                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

//                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());

                RequestDTO requestDTO = CommonService.populateRequestDTO(receivePacket);
                String filename  = requestDTO.getContent();
                int duration = requestDTO.getOffset();

                InetAddress address = receivePacket.getAddress();
                int port = receivePacket.getPort();

                if (subscriptions.containsKey(address + ":" + port) ) {
                    response = "There is an existing subscription for your specified file.";
                } else {
                    FileSubscriptionDTO fileSubscriptionDTO = populateFileSubscriptionDTO(address, port, filename, duration);
                    subscriptions.put(address + ":" + port, fileSubscriptionDTO);
                    response = "You are now registered for updates for " + duration + " minutes.";
                }

                byte[] responseBytes = CommonService.populateResponseBytesWithResponseCode(2, response);
                sendMessagesToClient(responseBytes, receivePacket);

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

    //For file content update, check subscription map to see whether there is any existing subscription for this file
    //and send the updated content to the client
    private static void checkAndProcessForFileSubscription(String filename, String updatedContent) {
        Iterator<Map.Entry<String, FileSubscriptionDTO>> iterator = subscriptions.entrySet().iterator();

        // Iterate and remove items based on a condition
        while (iterator.hasNext()) {
            Map.Entry<String, FileSubscriptionDTO> entry = iterator.next();
            if (entry.getValue().getFileName().equalsIgnoreCase(filename)) {
                try {
                    Date dateObject = dateFormat.parse(entry.getValue().getRegisterTimeStamp());
                    long subscriptionEndTimeInMilliSeconds = dateObject.getTime() + entry.getValue().getDuration();
                    long currentMillis = System.currentTimeMillis();

                    if (subscriptionEndTimeInMilliSeconds < currentMillis) {
                        //process to remove the subscription from subscriptions hashmap
                        iterator.remove();
                    } else {
                        //process to send any file updates to the subscribed client
                        DatagramPacket sendPacket = new DatagramPacket(receiveData, receiveData.length);
                        sendPacket.setAddress(InetAddress.getByName(entry.getKey().split(":")[0]));
                        sendPacket.setPort(Integer.parseInt(entry.getKey().split(":")[1]));
                        byte[] responseBytes = CommonService.populateResponseBytesWithResponseCode(2, updatedContent);
                        sendMessagesToClient(responseBytes, sendPacket);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }

            }

        }
    }

    private static FileSubscriptionDTO populateFileSubscriptionDTO(InetAddress address, int port, String filename, long duration) {
        FileSubscriptionDTO fileSubscriptionDTO = new FileSubscriptionDTO();
        fileSubscriptionDTO.setRegisterTimeStamp(getCurrentTime());
        fileSubscriptionDTO.setFileName(filename);
        fileSubscriptionDTO.setDuration(duration);
        return fileSubscriptionDTO;
    }

    private static String getCurrentTime() {
        Date dateObject = new Date();
        // Format and print the current date-time using the formatter
        String formattedCurrentDateTime = dateFormat.format(dateObject);
        return formattedCurrentDateTime;
    }




    private static void sendMessagesToClient(byte[] sendData, DatagramPacket receivePacket) {
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                receivePacket.getAddress(), receivePacket.getPort());
        try {
            serverSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }






}
