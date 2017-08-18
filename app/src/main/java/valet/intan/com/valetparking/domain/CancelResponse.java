package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 2/18/2017.
 */

public class CancelResponse {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("type")
        private String type;
        @SerializedName("id")
        private String id;

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }
    }
}
