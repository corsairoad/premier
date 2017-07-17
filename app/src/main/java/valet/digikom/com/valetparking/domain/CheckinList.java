package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 3/1/2017.
 */

public class CheckinList {

    @SerializedName("data")
    List<EntryCheckinResponse.Data> checkinResponseList;

    public List<EntryCheckinResponse.Data> getCheckinResponseList() {
        return checkinResponseList;
    }

    public void setCheckinResponseList(List<EntryCheckinResponse.Data> checkinResponseList) {
        this.checkinResponseList = checkinResponseList;
    }
}
