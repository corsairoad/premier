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

    public static class Table {
        public static final String TABLE_NAME = "entry_checkin_failed";

        public static final String COL_ID = "_id";
        public static final String COL_JSON_DATA = "json";
        public static final String COL_FAKE_VTHD_ID = "fake_vthd_id";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_JSON_DATA + " TEXT, " +
                COL_FAKE_VTHD_ID + " TEXT);";

        //public static final String
    }
}
