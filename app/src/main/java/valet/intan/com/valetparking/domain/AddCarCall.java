package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/19/2017.
 */

public class AddCarCall {
    @SerializedName("type")
    private final String type = "ad_car_call";
    @SerializedName("attributes")
    private Attribute attribute;

    public AddCarCall() {
    }

    public String getType() {
        return type;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public static class Attribute {

        @SerializedName("vthdDrmsIdCO")
        private String idDropTo;
        @SerializedName("vthdDateCarDriving")
        private String arrivedTime;

        public Attribute() {
        }

        public String getIdDropTo() {
            return idDropTo;
        }

        public void setIdDropTo(String idDropTo) {
            this.idDropTo = idDropTo;
        }

        public String getArrivedTime() {
            return arrivedTime;
        }

        public void setArrivedTime(String arrivedTime) {
            this.arrivedTime = arrivedTime;
        }

        public static class Table {
            public static final String TABLE_NAME = "called_car";

            public static final String COL_ID = "_id";
            public static final String COL_RESPONSE_ID = "response_id";
            public static final String COL_JSON_RESPONSE = "json_response";

            public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_RESPONSE_ID + " INTEGER, " +
                    COL_JSON_RESPONSE + " TEXT);";
        }
    }
}
