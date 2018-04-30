//package sample.pojo;

public class ConnectionData {
    private String dateTime;
    private String dataPackets;

    public ConnectionData(String dateTime, String datePackets) {
        this.dateTime = dateTime;
        this.dataPackets = datePackets;
    }

    public ConnectionData() {
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDataPackets() {
        return dataPackets;
    }

    public void setDataPackets(String dataPackets) {
        this.dataPackets = dataPackets;
    }
}