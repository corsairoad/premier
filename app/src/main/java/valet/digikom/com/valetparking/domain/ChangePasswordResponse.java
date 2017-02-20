package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 2/20/2017.
 */

public class ChangePasswordResponse {
    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private String code;
    @SerializedName("data")
    private Data data;

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("type")
        String type;

        public String getType() {
            return type;
        }
    }
}
