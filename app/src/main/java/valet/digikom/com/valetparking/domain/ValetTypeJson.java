package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/27/2017.
 */

public class ValetTypeJson {

    @SerializedName("data")
    private List<Data> listData;

    public ValetTypeJson() {
    }

    public List<Data> getListData() {
        return listData;
    }

    public void setListData(List<Data> listData) {
        this.listData = listData;
    }

    public static class Data {
        @SerializedName("type")
        private final String type = "valetfee_site_detail";
        @SerializedName("id")
        private  String id;
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

        public Attrib getAttrib() {
            return attrib;
        }

        public void setAttrib(Attrib attrib) {
            this.attrib = attrib;
        }

        public static class Attrib {
            @SerializedName("vfsd_id")
            private int id;
            @SerializedName("vfsd_name")
            private String valetTypeName;
            @SerializedName("vfsd_desc")
            private String valetTypeDesc;
            @SerializedName("vfsd_fee")
            private int price;
            @SerializedName("vfsd_type")
            private String isStringDefault;

            public Attrib() {
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getValetTypeName() {
                return valetTypeName;
            }

            public void setValetTypeName(String valetTypeName) {
                this.valetTypeName = valetTypeName;
            }

            public String getValetTypeDesc() {
                return valetTypeDesc;
            }

            public void setValetTypeDesc(String valetTypeDesc) {
                this.valetTypeDesc = valetTypeDesc;
            }

            public int getPrice() {
                return price;
            }

            public void setPrice(int price) {
                this.price = price;
            }

            public String getIsStringDefault() {
                return isStringDefault;
            }

            public void setIsStringDefault(String isStringDefault) {
                this.isStringDefault = isStringDefault;
            }
        }
    }

    public static class Table {
        public static final String TABLE_NAME = "valet_type";

        public static final String COL_ID = "_id";
        public static final String COL_JSON_DATA = "json_data";
        public static final String COL_ID_DATA = "id_data";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COL_JSON_DATA + " TEXT, " +
                COL_ID_DATA + " TEXT);";
    }

}
