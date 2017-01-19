package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/19/2017.
 */

public class AddCarCallBody {
    @SerializedName("data")
    AddCarCall addCarCall;

    public AddCarCallBody() {
    }

    public AddCarCall getAddCarCall() {
        return addCarCall;
    }

    public void setAddCarCall(AddCarCall addCarCall) {
        this.addCarCall = addCarCall;
    }
}
