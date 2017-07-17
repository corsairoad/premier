package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 2/14/2017.
 */

public class ClosingResponse {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    private static class Data {
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
