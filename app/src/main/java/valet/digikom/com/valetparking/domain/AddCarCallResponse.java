package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/19/2017.
 */

public class AddCarCallResponse {
    @SerializedName("data")
    private Data data;

    public AddCarCallResponse() {
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {
        @SerializedName("type")
        String type;
        @SerializedName("id")
        String id;

        public Data() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
