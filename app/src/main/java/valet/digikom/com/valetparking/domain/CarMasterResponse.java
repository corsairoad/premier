package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/9/2017.
 */

public class CarMasterResponse {

    @SerializedName("data")
    private List<CarMaster> data;

    public CarMasterResponse() {
    }

    public List<CarMaster> getData() {
        return data;
    }

    public void setData(List<CarMaster> data) {
        this.data = data;
    }
}
