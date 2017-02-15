package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 2/15/2017.
 */

public class PatchMeResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("type")
        private String type;
        @SerializedName("role_options")
        private int roleOption;

        public String getType() {
            return type;
        }

        public int getRoleOption() {
            return roleOption;
        }
    }
}
