package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/9/2017.
 */

public class AdditionalItemsResponse {

    @SerializedName("data")
    List<AdditionalItems> data;

    public List<AdditionalItems> getData() {
        return data;
    }

    public void setData(List<AdditionalItems> data) {
        this.data = data;
    }
}
