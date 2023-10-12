package server.common;

import server.dto.RequestDTO;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class CommonService {

    //populate RequestDTO when receiving packet from client
    public static RequestDTO populateRequestDTO(DatagramPacket receivePacket) {

        RequestDTO requestDTO = new RequestDTO();

        //Get operation char
        byte firstByte = receivePacket.getData()[0];
        char firstChar = (char) firstByte;
        requestDTO.setOperation(firstChar);

        //TO-DO for UUID

        //Get offset
        byte[] bytesForOffset = new byte[4];
        System.arraycopy(receivePacket.getData(), 1, bytesForOffset, 0, bytesForOffset.length);
        int offset = ByteBuffer.wrap(bytesForOffset).order(ByteOrder.LITTLE_ENDIAN).getInt();
        requestDTO.setOffset(offset);

        //Get length
        byte[] bytesForLength = new byte[4];
        System.arraycopy(receivePacket.getData(), 5, bytesForLength, 0, bytesForLength.length);
        int length = ByteBuffer.wrap(bytesForLength).order(ByteOrder.LITTLE_ENDIAN).getInt();
        requestDTO.setOffset(length);

        //Get content
        byte[] bytesForContent = new byte[receivePacket.getLength() - 9];
        System.arraycopy(receivePacket.getData(), 9, bytesForContent, 0, bytesForContent.length);
        String content = new String(bytesForContent, StandardCharsets.UTF_8);
        requestDTO.setContent(content);

        return requestDTO;
    }

    //Convert responseCode and responseMessage to a combined byte[]
    public static byte[] populateResponseBytesWithResponseCode(int responseCode, String responseMessage){

        byte[] responseCodeBytes = responseCodeToByteArray(responseCode);
        byte[] responseStringBytes = responseMessage.getBytes();
        byte[] combinedBytes = new byte[responseCodeBytes.length + responseStringBytes.length];
        System.arraycopy(responseCodeBytes, 0, combinedBytes, 0, responseCodeBytes.length);
        System.arraycopy(responseStringBytes, 0, combinedBytes, responseCodeBytes.length, responseStringBytes.length);

        return combinedBytes;
    }

    public static byte[] responseCodeToByteArray(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

}
