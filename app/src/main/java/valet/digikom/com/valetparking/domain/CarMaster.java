package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/9/2017.
 */

public class CarMaster {

    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private String type;
    @SerializedName("attributes")
    private Attrib attrib;

    public CarMaster() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Attrib getAttrib() {
        return attrib;
    }

    public void setAttrib(Attrib attrib) {
        this.attrib = attrib;
    }

    public static class Attrib {
        @SerializedName("cbms_id")
        private int id_attrib;
        @SerializedName("cbms_name")
        private String carName;
        @SerializedName("cbms_logo")
        private String logo;

        public Attrib() {
        }

        public int getId_attrib() {
            return id_attrib;
        }

        public void setId_attrib(int id_attrib) {
            this.id_attrib = id_attrib;
        }

        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }

    public static class Table {
        public static final String TABLE_NAME = "car_master";

        public static final String COL_ID = "_id";
        public static final String COL_CAR_NAME = "car_name";
        public static final String COL_CAR_ID = "id_car";
        public static final String COL_CAR_LOGO = "logo";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "( " + COL_ID + " INTEGER PRIMARY KEY, " +
                COL_CAR_NAME + " TEXT, " +
                COL_CAR_ID + " INTEGER," +
                COL_CAR_LOGO + " TEXT);";
    }
}
