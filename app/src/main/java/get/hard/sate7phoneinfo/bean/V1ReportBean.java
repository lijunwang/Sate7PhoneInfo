package get.hard.sate7phoneinfo.bean;

import java.util.List;


public class V1ReportBean {
    //see at https://wiki.tsingk.net:8443/pages/viewpage.action?pageId=65754
    private String type;//device type
    private String imei;
    private String sw;
    private String hw;
    private int battery;
    private int signal;
    private List<Double> gps;
    private int velocity;
    private List<OnOffRecs> onOffRecs;
    private List<CallLogRecs> callLogRecs;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }

    public void setSw(String sw) {
        this.sw = sw;
    }

    public String getSw() {
        return sw;
    }


    public void setHw(String hw) {
        this.hw = hw;
    }

    public String getHw() {
        return hw;
    }


    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getBattery() {
        return battery;
    }


    public void setSignal(int signal) {
        this.signal = signal;
    }

    public int getSignal() {
        return signal;
    }


    public void setGps(List<Double> gps) {
        this.gps = gps;
    }

    public List<Double> getGps() {
        return gps;
    }


    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public int getVelocity() {
        return velocity;
    }


    public void setOnOffRecs(List<OnOffRecs> onOffRecs) {
        this.onOffRecs = onOffRecs;
    }

    public List<OnOffRecs> getOnOffRecs() {
        return onOffRecs;
    }


    public void setCallLogRecs(List<CallLogRecs> callLogRecs) {
        this.callLogRecs = callLogRecs;
    }

    public List<CallLogRecs> getCallLogRecs() {
        return callLogRecs;
    }


    public static class OnOffRecs {

        private String date;
        private int type;


        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }


        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

    }

    public static class CallLogRecs {

        private String date;
        private int duration;
        private int number;
        private int type;


        public void setDate(String date) {
            this.date = date;
        }

        public String getDate() {
            return date;
        }


        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }


        public void setNumber(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }


        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

    }
}




