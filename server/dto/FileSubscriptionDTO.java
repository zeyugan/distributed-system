package server.dto;

public class FileSubscriptionDTO {
    private String registerTimeStamp;
    private long duration;
    private String fileName;

    public FileSubscriptionDTO() {
    }

    public String getRegisterTimeStamp() {
        return registerTimeStamp;
    }

    public void setRegisterTimeStamp(String registerTimeStamp) {
        this.registerTimeStamp = registerTimeStamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "dto.FileSubscriptionDTO{" +
                "registerTimeStamp='" + registerTimeStamp + '\'' +
                ", duration=" + duration +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}