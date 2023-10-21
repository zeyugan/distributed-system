package server.common;

import server.dto.RequestDTO;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommonService {

    //populate RequestDTO when receiving packet from client
    public static RequestDTO populateRequestDTO(DatagramPacket receivePacket) {

        RequestDTO requestDTO = new RequestDTO();

        //Get operation char
        byte firstByte = receivePacket.getData()[0];
        char firstChar = (char) firstByte;
        requestDTO.setOperation(firstChar);

        System.out.println("receivePacket: " + receivePacket.getData());


        //TO-DO for UUID
        byte[] bytesForUuid = new byte[36];
        System.arraycopy(receivePacket.getData(), 1, bytesForUuid, 0, bytesForUuid.length);
        String uuid = new String(bytesForUuid, StandardCharsets.UTF_8);
        requestDTO.setUuid(uuid);
        System.out.println("uuid: " + uuid);


        //Get offset
        byte[] bytesForOffset = new byte[4];
        System.arraycopy(receivePacket.getData(), 37, bytesForOffset, 0, bytesForOffset.length);
        int offset = ByteBuffer.wrap(bytesForOffset).order(ByteOrder.LITTLE_ENDIAN).getInt();
        requestDTO.setOffset(offset);
        System.out.println("offset: " + offset);


        //Get length
        byte[] bytesForLength = new byte[4];
        System.arraycopy(receivePacket.getData(), 41, bytesForLength, 0, bytesForLength.length);
        int length = ByteBuffer.wrap(bytesForLength).order(ByteOrder.LITTLE_ENDIAN).getInt();
        requestDTO.setLength(length);
        System.out.println("length: " + length);


        //Get content
        byte[] bytesForContent = new byte[receivePacket.getLength() - 45];
        System.arraycopy(receivePacket.getData(), 45, bytesForContent, 0, bytesForContent.length);
        String content = new String(bytesForContent, StandardCharsets.UTF_8);
        requestDTO.setContent(content);
        System.out.println("content: " + content);


        return requestDTO;
    }

    //Convert responseCode and responseMessage to a combined byte[]
    public static byte[] populateResponseBytesWithResponseCode(int responseCode, String responseMessage) {

        byte[] responseCodeBytes = responseCodeToByteArray(responseCode);
        byte[] responseStringBytes = responseMessage.getBytes();
        byte[] combinedBytes = new byte[responseCodeBytes.length + responseStringBytes.length];
        System.arraycopy(responseCodeBytes, 0, combinedBytes, 0, responseCodeBytes.length);
        System.arraycopy(responseStringBytes, 0, combinedBytes, responseCodeBytes.length, responseStringBytes.length);

        return combinedBytes;
    }

    //overload one that takes in a byte array as responseMessage
    public static byte[] populateResponseBytesWithResponseCode(int responseCode, byte[] responseMessage) {

        byte[] responseCodeBytes = responseCodeToByteArray(responseCode);
        byte[] combinedBytes = new byte[responseCodeBytes.length + responseMessage.length];
        System.arraycopy(responseCodeBytes, 0, combinedBytes, 0, responseCodeBytes.length);
        System.arraycopy(responseMessage, 0, combinedBytes, responseCodeBytes.length, responseMessage.length);

        return combinedBytes;
    }

    public static byte[] responseCodeToByteArray(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }


//    public static UUID convertBytesToUUID(byte[] bytes) {
//        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
//        long high = byteBuffer.getLong();
//        long low = byteBuffer.getLong();
//        return new UUID(high, low);
//    }

    public static byte[] convertUUIDToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

}
