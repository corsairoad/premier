package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 2/21/2017.
 */

public class Bank{
    @SerializedName("data")
    private List<Data> dataList;

    public List<Data> getDataList() {
        return dataList;
    }

    public static class Data {
        @SerializedName("data")
        private String type;
        @SerializedName("id")
        private String id;
        @SerializedName("attributes")
        private Attr attr;

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public Attr getAttr() {
            return attr;
        }

        public void setAttr(Attr attr) {
            this.attr = attr;
        }

        public void setId(String id) {
            this.id = id;
        }

        public static class Attr {
            @SerializedName("bnms_id")
            private int bankId;
            @SerializedName("bnms_name")
            private String bankName;
            @SerializedName("bnms_desc")
            private String bankDesc;

            public int getBankId() {
                return bankId;
            }

            public String getBankName() {
                return bankName;
            }

            public String getBankDesc() {
                return bankDesc;
            }

            public void setBankId(int bankId) {
                this.bankId = bankId;
            }

            public void setBankName(String bankName) {
                this.bankName = bankName;
            }

            public void setBankDesc(String bankDesc) {
                this.bankDesc = bankDesc;
            }
        }
    }

    public static class Table {
        public static final String TABLE_NAME = "bank";

        public static final String COL_ID = "_id";
        public static final String COL_BANK_ID = "bank_id";
        public static final String COL_BANK_NAME = "bank_name";
        public static final String COL_BANK_DESC = "bank_desc";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BANK_ID + " INTEGER, " +
                COL_BANK_NAME + " TEXT, " +
                COL_BANK_DESC + " TEXT);";
    }
}
