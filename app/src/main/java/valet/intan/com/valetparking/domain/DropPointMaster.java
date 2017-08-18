package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/11/2017.
 */

public class DropPointMaster {

    public static class Table {
        public static final String TABLE_NAME = "drop_point";

        public static final String COL_ID = "_id";
        public static final String COL_TYPE = "type";
        public static final String COL_DROP_ID = "drop_id";
        public static final String COL_DROP_NAME = "drop_name";
        public static final String COL_DROP_DESC = "drop_desc";
        public static final String COL_LATITUDE = "lat";
        public static final String COL_LONGITUDE = "lng";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "( " +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_TYPE + " TEXT, " +
                COL_DROP_ID + " INTEGER, " +
                COL_DROP_NAME + " TEXT, " +
                COL_DROP_DESC + " TEXT, " +
                COL_LATITUDE + " DOUBLE, " +
                COL_LONGITUDE + " DOUBLE)";
    }

    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private String id;
    @SerializedName("attributes")
    private Attrib attrib;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

        @SerializedName("drms_id")
        private int dropId;
        @SerializedName("drms_desc")
        private String dropDesc;
        @SerializedName("drms_name")
        private String dropName;
        @SerializedName("drms_latitude")
        private double latitude;
        @SerializedName("drms_longitude")
        private double longitude;
        @SerializedName("drms_type")
        private String dropType;


        public Attrib() {
        }

        public int getDropId() {
            return dropId;
        }

        public void setDropId(int dropId) {
            this.dropId = dropId;
        }

        public String getDropDesc() {
            return dropDesc;
        }

        public void setDropDesc(String dropDesc) {
            this.dropDesc = dropDesc;
        }

        public String getDropName() {
            return dropName;
        }

        public void setDropName(String dropName) {
            this.dropName = dropName;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getDropType() {
            return dropType;
        }

        public void setDropType(String dropType) {
            this.dropType = dropType;
        }
    }
}
