package server;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Service {

    /***
     * Service 1: Read specified file and return the content in String
     * @param query query string in format of: "READ 00000001 00000020 /dir/sample.txt"
     *              where first 4 digits are the command code
     *              next 8 digit is an integer representing the offset
     *              next 8 digit is an integer representing the length of the content to be read
     *              remaining is the file path from [root directory]/storage
     * @return content of the file in String
     */
    public static String readFile(String query) throws IOException {

        // print current working directory
        System.out.println("Current working directory: " + System.getProperty("user.dir"));

        // parse offset
        int offset = Integer.parseInt(query.substring(4, 12));
        // parse length
        int length = Integer.parseInt(query.substring(12, 20));
        // parse file path
        String filePath = query.substring(20);

        // read the file
        // Open the file in read-only mode
        RandomAccessFile file = new RandomAccessFile("./server/storage"+filePath, "r");
        file.seek(offset);
        byte[] buffer = new byte[length];
        file.read(buffer);

        // Close the file
        file.close();

        // Convert the byte array to a string
        return new String(buffer);
    }

    /***
     * Service 2: Write specified file with the content
     * @param query query string in format of: "WRIT0000000100000020/dir/sample.txt|test content"
     *              where the first 4 digits are the command code
     *              next 8 digit is an integer representing the offset
     *              next 8 digit is an integer representing the length of the content to be written
     *              next is the file path from [root directory]/storage until | character
     *              remaining is the content to be written
     *              Note: the content to be written must be the same length or shorter as the length specified
     *              in the query
     * @return 0 if write is successful, 1 otherwise
     */
    public static String writeFile(String query) throws IOException {
        try {
            // parse offset
            int offset = Integer.parseInt(query.substring(4, 12));
            // parse length
            int length = Integer.parseInt(query.substring(12, 20));
            // parse file path
            String filePath = query.substring(20, query.indexOf("|"));
            // parse content
            String content = query.substring(query.indexOf("|") + 1);

            // read the file
            // Open the file in read-only mode
            RandomAccessFile file = new RandomAccessFile("./server/storage"+filePath, "rw");
            file.seek(offset);
            file.write(content.getBytes());

            // Close the file
            file.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "1";
        }

        // Convert the byte array to a string
        return "0";
    }
}
