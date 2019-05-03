package hkcc.ccn3165.project2_wifiscanner;

public class WIFIInformation {
    private String longitude;
    private String latitude;
    private String BSSID;
    private String SSID;
    private String date;
    private String time;


    public WIFIInformation(String longitude, String latitude, String BSSID, String SSID,String date, String time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.date = date;
        this.time = time;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
