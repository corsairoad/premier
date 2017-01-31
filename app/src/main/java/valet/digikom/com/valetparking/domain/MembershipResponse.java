package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/31/2017.
 */

public class MembershipResponse {

    @SerializedName("data")
    List<Data> dataList;

    public List<Data> getDataList() {
        return dataList;
    }

    public static class Data {
        @SerializedName("type")
        private String type;
        @SerializedName("id")
        private String id;
        @SerializedName("attributes")
        private Attr attr;

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public Attr getAttr() {
            return attr;
        }

        public static class Attr {
            @SerializedName("dcdt_id")
            private int id;
            @SerializedName("dcdt_csms_id")
            private int siteId;
            @SerializedName("dcdt_day")
            private int day;
            @SerializedName("dcdt_desc")
            private String desc;
            @SerializedName("dcdt_start")
            private String start;
            @SerializedName("dcdt_end")
            private String end;
            @SerializedName("dcdt_fee")
            private String price;
            @SerializedName("dcdt_name")
            private String name;
            @SerializedName("dcdt_type")
            private String type;
            @SerializedName("dcdt_token")
            private String token;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getDay() {
                return day;
            }

            public void setDay(int day) {
                this.day = day;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getStart() {
                return start;
            }

            public void setStart(String start) {
                this.start = start;
            }

            public String getEnd() {
                return end;
            }

            public void setEnd(String end) {
                this.end = end;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }
        }
    }
}
