package valet.digikom.com.valetparking.domain;

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
    }
}
