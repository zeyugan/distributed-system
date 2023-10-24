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
        System.out.println("offset test:" + offset);

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
        System.out.println("offset:" + offset);
        System.out.println("length:" + length);

        file.seek(offset);
        byte[] buffer = new byte[length];
        file.read(buffer);

        System.out.println("buffer:" + new String(buffer));

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
        if (!uuid.equals(whitelisted) || !uuidList.contains(uuid)) {
            return "1";
        }

        // remove uuid from uuidList
        uuidList.remove(uuid);

        try {
            String content = dto.getContent();

            // split filepath and write content by symbol "|"
            String[] splitContent = content.split("\\|");
            String filePath = splitContent[0];
            String fileContent = splitContent[1];

            // open in rw
            RandomAccessFile file = new RandomAccessFile("./server/storage" + filePath, "rw");
            file.seek(offset);
            file.write(fileContent.getBytes());

            // close file
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }

        return "0";
    }

    // generate a random UUID
    public static String generateUUID() {
        String uuid = UUID.randomUUID().toString();

        // add to list
        uuidList.add(uuid);

        return UUID.randomUUID().toString();
    }

    //copy file service
    public static String copyFile(RequestDTO dto) throws IOException {
        // get offset
        int offset = dto.getOffset();

        // get length, if length is 0 or null, throw exception
        int length = dto.getLength();
        if (length == 0) {
            return "1";
        }

        // get uuid
        String uuid = dto.getUuid();

        // check if uuid is whitelisted or exist in uuidList
        if (!uuid.equals(whitelisted) || !uuidList.contains(uuid)) {
            return "1";
        }

        // remove uuid from uuidList
        uuidList.remove(uuid);

        try {
            String content = dto.getContent();

            // split content into 2 parts by "|"
            String[] splitContent = content.split("\\|");

            // part 1: source file path
            String sourceFilePath = splitContent[0];

            // part 2: destination file path
            String destinationFilePath = splitContent[1];

            // open source file in rw
            RandomAccessFile sourceFile = new RandomAccessFile("./server/storage" + sourceFilePath, "rw");

            RandomAccessFile destinationFile = null;
            try {
                // open destination file in rw
                destinationFile = new RandomAccessFile("./server/storage" + destinationFilePath, "rw");
            }
            catch (IOException e) {
                // if destination file does not exist, try to create a new file
                File file = new File("./server/storage" + destinationFilePath);
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                    destinationFile = new RandomAccessFile("./server/storage" + destinationFilePath, "rw");
                }
                else {
                    System.out.println("Error creating file.");
                    return "1";
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
        }
        catch (IOException e) {
            e.printStackTrace();
            return "1";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "1";
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


}
