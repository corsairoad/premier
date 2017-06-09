package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX11 on 6/8/2017.
 */

public class GetReprintCheckinResponse {

    @SerializedName("data")
    private List<Data> listData;
    @SerializedName("meta")
    private Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public List<Data> getListData() {
        return listData;
    }

    public class Data {

        @SerializedName("type")
        private String type = "reprint_checkin";
        @SerializedName("id")
        private String id;
        @SerializedName("attributes")
        private Attributes attributes;
        @SerializedName("links")
        private Link link;

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public Link getLink() {
            return link;
        }
    }

    public class Attributes {
        @SerializedName("apps_time")
        private String appTime;
        @SerializedName("checker_id")
        private int checkerId;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("droppoint_id")
        private int droppointId;
        @SerializedName("id")
        private int id;
        @SerializedName("license_plat")
        private String licensePlate;
        @SerializedName("ticket_value")
        private String noTicket;

        public String getAppTime() {
            return appTime;
        }

        public int getCheckerId() {
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

        public String getNoTicket() {
            return noTicket;
        }
    }

    private class Link{
        @SerializedName("self")
        Self self;

        public Self getSelf() {
            return self;
        }
    }

    private class Self{
        @SerializedName("href")
        private String href;

        public String getHref() {
            return href;
        }
    }

    private class Meta {

        @SerializedName("page")
        private Page page;

        public Page getPage() {
            return page;
        }
    }

    private class Page {
        @SerializedName("total")
        private int total;
        @SerializedName("last")
        private int last;
        @SerializedName("number")
        private int number;
        @SerializedName("size")
        private int size;

        public int getTotal() {
            return total;
        }

        public int getLast() {
            return last;
        }

        public int getNumber() {
            return number;
        }

        public int getSize() {
            return size;
        }
    }
}



