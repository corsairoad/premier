package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DIGIKOM-EX11 on 6/8/2017.
 */

public class PostReprintCheckin {

    @SerializedName("data")
    private Data data;


    public PostReprintCheckin(PostReprintCheckin.Builder builder) {
        setData(builder.data);
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("attributes")
        private Attributes attributes;
        @SerializedName("type")
        private String type = "reprint_checkin";

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static class Attributes {
            @SerializedName("apps_time")
            private String appsTime;
            @SerializedName("license_plat")
            private String licensePlate;
            @SerializedName("ticket_value")
            private String ticketNo;

            public void setAppsTime(String appsTime) {
                this.appsTime = appsTime;
            }

            public void setLicensePlate(String licensePlate) {
                this.licensePlate = licensePlate;
            }

            public void setTicketNo(String ticketNo) {
                this.ticketNo = ticketNo;
            }
        }
    }


    public static class Builder {
        private String appsTime;
        private String licensePlate;
        private String ticketNo;

       private Data data;

        public Builder setAppsTime(String appsTime) {
            if (appsTime == null) {
                appsTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }

            this.appsTime = appsTime;
            return this;
        }

        public Builder setLicensePlate(String plate) {
            this.licensePlate = plate;
            return this;
        }

        public Builder setTicketNo(String ticketNo) {
            this.ticketNo = ticketNo;
            return this;
        }

        public PostReprintCheckin build(){
            Data.Attributes attributes = new Data.Attributes();
            attributes.setAppsTime(this.appsTime);
            attributes.setLicensePlate(this.licensePlate);
            attributes.setTicketNo(this.ticketNo);

            data = new Data();
            data.setAttributes(attributes);

            return new PostReprintCheckin(this);
        }


    }
}
