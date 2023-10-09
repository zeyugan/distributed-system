package server;

import java.nio.ByteBuffer;

public class Network {
    public static byte[] htonl(int hostInt) {
        return ByteBuffer.allocate(4).putInt(hostInt).array();
    }

    public static int ntohl(String networkIntStr) {
        byte[] networkIntBytes = networkIntStr.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(networkIntBytes);
        return buffer.getInt();
    }
}
