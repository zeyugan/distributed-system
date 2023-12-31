package server;

import server.dto.RequestDTO;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;

public class Service {


    // active uuid list
    public static ArrayList<String> uuidList = new ArrayList<>();

    static String whitelisted = "000000000000000000000000000000000000";


    /***
     * Service 1: Read specified file from offset with length
     * @return specified file content
     */
    public static String readFile(RequestDTO dto) throws IOException {

        // print current working directory
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        // get offset
        int offset = dto.getOffset();

        // get length, if length is 0 or null, set buffer size to 65536
        int length = dto.getLength();
        if (length == 0) {
            length = 65536;
        }


        // get file path
        String filePath = dto.getContent();

        // read the file
        // Open the file in read-only mode
        RandomAccessFile file = new RandomAccessFile("./server/storage" + filePath, "r");

        file.seek(offset);
        byte[] buffer = new byte[length];
        file.read(buffer);

        // Close the file
        file.close();

        // Convert the byte array to a string
        return new String(buffer);
    }


    public static String writeFile(RequestDTO dto) throws IOException {
        // get offset
        int offset = dto.getOffset();

        // get uuid
        String uuid = dto.getUuid();

        // check if uuid is whitelisted or exist in uuidList
        if (!uuid.equals(whitelisted) && !uuidList.contains(uuid)) {
            return "UUID is invalid";
        }

        // remove uuid from uuidList
        uuidList.remove(uuid);

        String content = dto.getContent();

        // split filepath and write content by symbol "|". split only once
        String[] splitContent = content.split("\\|",2);
        String filePath = splitContent[0];
        String fileContent = splitContent[1];

        // check if file exists
        checkFileExist(filePath);

        try {

            // open in rw
            RandomAccessFile file = new RandomAccessFile("./server/storage" + filePath, "rw");

            // if offset is larger than file length, append to the end of file
            if (offset > file.length()) {
                file.seek(file.length());
                file.write(fileContent.getBytes());
            }
            // else insert at offset and push remaining content back
            else {
                // read remaining content
                file.seek(offset);
                byte[] remainingContent = new byte[(int) (file.length() - offset)];
                file.read(remainingContent);

                // write content at offset
                file.seek(offset);
                file.write(fileContent.getBytes());

                // append remaining content
                file.seek(offset + fileContent.getBytes().length);
                file.write(remainingContent);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return "0";
    }

    // generate a random UUID
    public static String generateUUID() {
        String uuid = UUID.randomUUID().toString();

        // add to list
        uuidList.add(uuid);

        return uuid;
    }

    //copy file service
    public static String copyFile(RequestDTO dto) throws IOException {
        // get offset
        int offset = dto.getOffset();

        // get length, if length is 0 or null, throw exception
        int length = dto.getLength();
        if (length == 0) {
            return "requested length is 0";
        }

        // get uuid
        String uuid = dto.getUuid();

        // check if uuid is whitelisted or exist in uuidList
        if (!uuid.equals(whitelisted) && !uuidList.contains(uuid)) {
            return "UUID is invalid";
        }

        // remove uuid from uuidList
        uuidList.remove(uuid);

        String content = dto.getContent();

        // split content into 2 parts by "|". split only once
        String[] splitContent = content.split("\\|", 2);

        // part 1: source file path
        String sourceFilePath = splitContent[0];

        // part 2: destination file path
        String destinationFilePath = splitContent[1];

        // check source file exists
        checkFileExist(sourceFilePath);


        try {


            // open source file in rw
            RandomAccessFile sourceFile = new RandomAccessFile("./server/storage" + sourceFilePath, "rw");


            RandomAccessFile destinationFile = null;
            try {
                // check destination file exists
                checkFileExist(destinationFilePath);
                // open destination file in rw
                destinationFile = new RandomAccessFile("./server/storage" + destinationFilePath, "rw");
            } catch (IOException e) {
                // if destination file does not exist, try to create a new file
                File file = new File("./server/storage" + destinationFilePath);
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                    destinationFile = new RandomAccessFile("./server/storage" + destinationFilePath, "rw");
                } else {
                    System.out.println("Error creating file.");
                    return "Error creating file";
                }
            }

            // seek to offset
            sourceFile.seek(offset);

            // read source file content
            byte[] buffer = new byte[length];
            sourceFile.read(buffer);

            // append to the end of destination file
            destinationFile.seek(destinationFile.length());
            destinationFile.write(buffer);

            // close files
            sourceFile.close();
            destinationFile.close();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        return "0";
    }


    // return file last modified time
    public static String getLastModifiedTime(RequestDTO dto) {
        // print current working directory
        System.out.println("Current working directory: " + System.getProperty("user.dir"));

        String filePath = dto.getContent();
        File file = new File("./server/storage" + filePath);
        long lastModified = file.lastModified();
        return String.valueOf(lastModified);
    }

    public static void checkFileExist(String content) throws IOException {
        File file = new File("./server/storage" + content);
        if(!file.exists()){
            throw new IOException();
        }
    }




}
