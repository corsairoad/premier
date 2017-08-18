package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX11 on 6/8/2017.
 */

public class PostReprintCheckinResponse {

    @SerializedName("data")
    public PostReprintCheckin.Data data;

    public PostReprintCheckin.Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("type")
        private String type;
        @SerializedName("id")
        private String id;
        @SerializedName("attributes")
        private Attributes attributes;

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public Attributes getAttributes() {
            return attributes;
        }
    }

    public static class Attributes{


        @SerializedName("checker_id")
        private String checkerId;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("droppoint_id")
        private int droppointId;
        @SerializedName("id")
        private int id;
        @SerializedName("license_plat")
        private String licensePlate;
        @SerializedName("ticket_value")
        private String ticketNo;

        public String getCheckerId() {
            return checkerId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getDroppointId() {
            return droppointId;
        }

        public int getId() {
            return id;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public String getTicketNo() {
            return ticketNo;
        }
    }
}
