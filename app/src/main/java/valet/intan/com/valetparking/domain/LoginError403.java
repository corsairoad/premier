package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fadlymunandar on 8/4/17.
 */

public class LoginError403 {

    @SerializedName("error")
    private ErrorBody errorBody;

    public ErrorBody getErrorBody() {
        return errorBody;
    }

    public class ErrorBody{
        @SerializedName("status")
        private String status;
        @SerializedName("message")
        private String message;
        @SerializedName("details")
        List<LoggedInDeviceDetail> deviceDetails;

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public List<LoggedInDeviceDetail> getDeviceDetails() {
            return deviceDetails;
        }

        @Override
        public String toString() {
            String deviceId = "";
            String site = "";
            String lobby = "";

            for (LoggedInDeviceDetail device : getDeviceDetails()) {
                deviceId = deviceId + device.getDevice() + ",";
                site = site + device.getSite();
                lobby = lobby + device.getLobby();
            }
            return String.format("Device id: %s\nSite: %s\nLobby: %s\n", deviceId, site, lobby);
        }
    }

    public class LoggedInDeviceDetail {
        @SerializedName("device")
        private String device;
        @SerializedName("site")
        private String site;
        @SerializedName("lobby")
        private String lobby;

        public String getDevice() {
            return device;
        }

        public String getSite() {
            return site;
        }

        public String getLobby() {
            return lobby;
        }
    }

    @Override
    public String toString() {
        return String.format("%s\n%s\n", errorBody.getMessage(), errorBody);
    }
}
