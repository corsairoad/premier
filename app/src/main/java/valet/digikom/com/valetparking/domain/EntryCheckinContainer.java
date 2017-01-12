package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/12/2017.
 */

public class EntryCheckinContainer {

    @SerializedName("data")
    private EntryCheckin entryCheckin;

    public EntryCheckinContainer() {
    }

    public EntryCheckin getEntryCheckin() {
        return entryCheckin;
    }

    public void setEntryCheckin(EntryCheckin entryCheckin) {
        this.entryCheckin = entryCheckin;
    }
}
