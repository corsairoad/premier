package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 1/8/17.
 */

public class Token {

    public static final String EMAIL = "paulus@donny.id";
    public static final String PASSX = "secret";

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
