package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dev on 1/7/17.
 */

public class DefectResponse {

    @SerializedName("data")
    List<DefectMaster> data;

    public DefectResponse() {
    }

    public List<DefectMaster> getData() {
        return data;
    }

    public void setData(List<DefectMaster> data) {
        this.data = data;
    }
}
