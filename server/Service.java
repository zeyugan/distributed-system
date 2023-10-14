package server;

import server.dto.RequestDTO;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Service {


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
}
