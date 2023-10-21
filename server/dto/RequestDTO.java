package server.dto;

import java.util.UUID;

public class RequestDTO {

    private char operation;
    private String uuid;
    private int offset;
    private int length;
    private String content;

    public char getOperation() {
        return operation;
    }

    public void setOperation(char operation) {
        this.operation = operation;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RequestDTO{" +
                "operation=" + operation +
                ", uuid='" + uuid + '\'' +
                ", offset=" + offset +
                ", length=" + length +
                ", content='" + content + '\'' +
                '}';
    }
}
