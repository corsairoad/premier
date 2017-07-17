package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/11/2017.
 */

public class DropPointMasterResponse {
    @SerializedName("data")
    List<DropPointMaster> dropPointList;

    public List<DropPointMaster> getDropPointList() {
        return dropPointList;
    }

    public void setDropPointList(List<DropPointMaster> dropPointList) {
        this.dropPointList = dropPointList;
    }
}
