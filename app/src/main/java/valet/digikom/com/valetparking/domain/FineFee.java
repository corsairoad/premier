package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class FineFee {

    public static final String LOST_TICKET = "TICKET-LOST";
    public static final String STAY_OVERNIGHT = "HOSPITALIZED";
    @SerializedName("data")
    List<Fine> data;

    public FineFee() {
    }

    public List<Fine> getData() {
        return data;
    }

    public void setData(List<Fine> data) {
        this.data = data;
    }

    public static class Fine {
        @SerializedName("type")
        private static final String type = "finefee_site_detail";
        @SerializedName("id")
        private String id;
        @SerializedName("attributes")
        private Attrib attrib;

        public Fine() {
        }

        public static String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Attrib getAttrib() {
            return attrib;
        }

        public void setAttrib(Attrib attrib) {
            this.attrib = attrib;
        }

        public static class Attrib {
            @SerializedName("id")
            private int id;
            @SerializedName("ffsd_fee")
            private int fee;
            @SerializedName("ffsd_name")
            private String name;
            @SerializedName("ffsd_desc")
            private String desc;
            @SerializedName("ffsd_type")
            private String fine_type;

            public Attrib() {
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getFee() {
                return fee;
            }

            public void setFee(int fee) {
                this.fee = fee;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDesc() {
                return desc;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public String getFine_type() {
                return fine_type;
            }

            public void setFine_type(String fine_type) {
                this.fine_type = fine_type;
            }
        }

    }

    public static class Table {
        public static final String TABLE_NAME = "fine_fee";

        public static final String COL_ID = "_ID";
        public static final String COL_FINE_ID = "id_fine";
        public static final String COL_JSON_FINE = "json_fine";
        public static final String COL_FINE_TYPE = "fine_type";

        public static final String CREATE = "CREATE TABLE "  + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FINE_ID + " INTEGER, " +
                COL_JSON_FINE + " TEXT, " +
                COL_FINE_TYPE + " TEXT);";
    }

}
