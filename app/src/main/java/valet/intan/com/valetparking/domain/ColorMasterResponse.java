package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/10/2017.
 */

public class ColorMasterResponse {

    @SerializedName("data")
    private List<ColorMaster> colorMasterList;

    public ColorMasterResponse() {
    }

    public List<ColorMaster> getColorMasterList() {
        return colorMasterList;
    }

    public void setColorMasterList(List<ColorMaster> colorMasterList) {
        this.colorMasterList = colorMasterList;
    }
}
