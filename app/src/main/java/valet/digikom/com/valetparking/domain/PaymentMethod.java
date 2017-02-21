package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 2/21/2017.
 */

public class PaymentMethod {

    @SerializedName("data")
    private List<Data> dataList;

    public List<Data> getDataList() {
        return dataList;
    }

    public static class Data {
        @SerializedName("type")
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

        public static class Attr {
            @SerializedName("pyto_desc")
            private String paymentDesc;
            @SerializedName("pyto_id")
            private int paymentId;
            @SerializedName("pyto_name")
            private String paymentName;
            @SerializedName("pyto_field_post")
            private String paymentFieldPost;
            @SerializedName("pyto_rel_get_api")
            private String paymentGetApi;
            @SerializedName("pyto_rel_post_api")
            private String paymentPostApi;
            @SerializedName("pyto_pycs_id")
            private int paymentCategoryId;

            public String getPaymentDesc() {
                return paymentDesc;
            }

            public int getPaymentId() {
                return paymentId;
            }

            public String getPaymentName() {
                return paymentName;
            }

            public String getPaymentFieldPost() {
                return paymentFieldPost;
            }

            public String getPaymentGetApi() {
                return paymentGetApi;
            }

            public String getPaymentPostApi() {
                return paymentPostApi;
            }

            public int getPaymentCategoryId() {
                return paymentCategoryId;
            }

            public void setPaymentDesc(String paymentDesc) {
                this.paymentDesc = paymentDesc;
            }

            public void setPaymentId(int paymentId) {
                this.paymentId = paymentId;
            }

            public void setPaymentName(String paymentName) {
                this.paymentName = paymentName;
            }

            public void setPaymentFieldPost(String paymentFieldPost) {
                this.paymentFieldPost = paymentFieldPost;
            }

            public void setPaymentGetApi(String paymentGetApi) {
                this.paymentGetApi = paymentGetApi;
            }

            public void setPaymentPostApi(String paymentPostApi) {
                this.paymentPostApi = paymentPostApi;
            }

            public void setPaymentCategoryId(int paymentCategoryId) {
                this.paymentCategoryId = paymentCategoryId;
            }
        }
    }

    public static class Table {
        public static final String TABLE_NAME = "master_payment";

        public static final String COL_ID = "_id";
        public static final String COL_PYMNT_ID = "pymnt_id";
        public static final String COL_PYMNT_NAME = "pymnt_name";
        public static final String COL_PYMNT_DESC = "pymnt_desc";
        public static final String COL_PYMNT_FIELD_POST = "field_post";
        public static final String COL_PYMNT_CATEGORY_ID = "pymnt_category_id";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PYMNT_ID + " INTEGER, " +
                COL_PYMNT_NAME + " TEXT, " +
                COL_PYMNT_DESC + " TEXT, " +
                COL_PYMNT_FIELD_POST + " TEXT, " +
                COL_PYMNT_CATEGORY_ID + " INTEGER);";
    }
}
