package server;

import server.common.CommonService;
import server.dto.FileSubscriptionDTO;
import server.dto.RequestDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.*;
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


    public static void main(String[] args) {
        DatagramPacket receivePacket = null;
        String response;
        byte[] responseBytes;
        try {
            //server port number
            serverSocket = new DatagramSocket(6666);

            //For store the map of the subscribed files

            while (true) {
                try {
                    receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);

//                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    RequestDTO requestDTO = CommonService.populateRequestDTO(receivePacket);

                    char operation = requestDTO.getOperation();

                    // switch case for different operations
                    switch (operation) {
                        case 'R':
                            //read file
                            response = Service.readFile(requestDTO);
                            break;
                        case 'W':
                            //write file
                            response = Service.writeFile(requestDTO);
                            checkAndProcessForFileSubscription(requestDTO.getContent().split("\\|")[0], requestDTO.getContent().substring(requestDTO.getContent().split("\\|")[0].length() + 1));
                            break;
                        case 'S':
                            // subscribe to file
                            Service.checkFileExist(requestDTO.getContent());
                            response = processForFileSubcription(receivePacket,requestDTO);
                            break;
                        case 'I':
                            // request UUID
                            response = Service.generateUUID();
                            break;
                        case 'T':
                            // get last modified time
                            response = Service.getLastModifiedTime(requestDTO);
                            break;
                        case 'C':
                            // copy content service
                            response = Service.copyFile(requestDTO);
                            break;
                        default:
                            response = "Invalid operation";
                    }

                    responseBytes = CommonService.populateResponseBytesWithResponseCode(0, response);

                    sendMessagesToClient(responseBytes, receivePacket);

                    //print out the list of subscriptions
                    System.out.println("===========List of servers and subscripted files===========");
                    subscriptions.forEach((key, value) -> System.out.println(key + ": " + value.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                    response = "Invalid operation";
                    responseBytes = CommonService.populateResponseBytesWithResponseCode(2, response);
                    sendMessagesToClient(responseBytes, receivePacket);
                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }

    }

    private static String processForFileSubcription(DatagramPacket receivePacket,RequestDTO requestDTO) {
        String filename = requestDTO.getContent();
        int duration = requestDTO.getLength();

        InetAddress address = receivePacket.getAddress();
        int port = receivePacket.getPort();
        FileSubscriptionDTO fileSubscriptionDTO = populateFileSubscriptionDTO(address, port, filename, duration);
        checkExpiredFileSubscription();
        if (subscriptions.containsKey(address + ":" + port)) {
            subscriptions.get(address + ":" + port).setDuration(fileSubscriptionDTO.getDuration());
            subscriptions.get(address + ":" + port).setFileName(fileSubscriptionDTO.getFileName());
            subscriptions.get(address + ":" + port).setRegisterTimeStamp(fileSubscriptionDTO.getRegisterTimeStamp());
        } else {
            subscriptions.put(address + ":" + port, fileSubscriptionDTO);
        }
        return  "You are now registered for updates for " + duration + " minutes.";
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
                        sendPacket.setAddress(InetAddress.getByName(entry.getKey().split(":")[0].substring(1)));
                        sendPacket.setPort(Integer.parseInt(entry.getKey().split(":")[1]));
                        byte[] responseBytes = CommonService.populateResponseBytesWithResponseCode(2, updatedContent);
                        sendMessagesToClient(responseBytes, sendPacket);
                    }
                } catch (ParseException | UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static void checkExpiredFileSubscription() {
        Iterator<Map.Entry<String, FileSubscriptionDTO>> iterator = subscriptions.entrySet().iterator();

        // Iterate and remove items based on a condition
        try {
            while (iterator.hasNext()) {
                Map.Entry<String, FileSubscriptionDTO> entry = iterator.next();
                Date dateObject = dateFormat.parse(entry.getValue().getRegisterTimeStamp());
                long subscriptionEndTimeInMilliSeconds = dateObject.getTime() + entry.getValue().getDuration();
                long currentMillis = System.currentTimeMillis();
                if (subscriptionEndTimeInMilliSeconds < currentMillis) {
                    //process to remove the subscription from subscriptions hashmap
                    iterator.remove();
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
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
