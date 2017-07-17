package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/28/2017.
 */

public class Disclaimer {

    @SerializedName("data")
    private List<Data> dataList;

    public Disclaimer() {
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }

    public static class Data {
        @SerializedName("type")
        private final String type = "disclaimer_master";
        @SerializedName("id")
        private String id;
        @SerializedName("attributes")
        private Attrib attrib;

        public Data() {
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Attrib getAttrib() {
            return attrib;
        }

        public void setAttrib(Attrib attrib) {
            this.attrib = attrib;
        }

        public static class Attrib{
            @SerializedName("dlms_id")
            private int id;
            @SerializedName("dlms_name")
            private String dscName;
            @SerializedName("dlms_desc")
            private String dscDesc;

            public Attrib() {
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getDscName() {
                return dscName;
            }

            public void setDscName(String dscName) {
                this.dscName = dscName;
            }

            public String getDscDesc() {
                return dscDesc;
            }

            public void setDscDesc(String dscDesc) {
                this.dscDesc = dscDesc;
            }
        }
    }

    public static class Table {
        public static final String TABLE_NAME = "disclaimer";

        public static final String COL_ID = "_id";
        public static final String COL_ID_DISCLAIMER = "id_disclaimer";
        public static final String COL_JSON_DATA = "json_data";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ID_DISCLAIMER + " INTEGER, " +
                COL_JSON_DATA + " TEXT);";
    }

}
