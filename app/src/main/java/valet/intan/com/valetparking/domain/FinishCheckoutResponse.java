package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class FinishCheckoutResponse {
    @SerializedName("data")
    private Data data;

    public FinishCheckoutResponse() {
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private static final String type = "ad_checkout_finish_fine";
        @SerializedName("id")
        private String id;
        @SerializedName("attributes")
        private Attrib attrib;

        public Data() {
        }

        public static String getType() {
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

        public static class Attrib {
            @SerializedName("created_at")
            private String createdAt;
            @SerializedName("deleted_at")
            private String deletedAt;
            @SerializedName("updated_at")
            private String updatedAt;
            @SerializedName("vthd_id")
            private int id;
            @SerializedName("vthd_transact_id")
            private String idTransaction;

            public Attrib() {
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(String createdAt) {
                this.createdAt = createdAt;
            }

            public String getDeletedAt() {
                return deletedAt;
            }

            public void setDeletedAt(String deletedAt) {
                this.deletedAt = deletedAt;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(String updatedAt) {
                this.updatedAt = updatedAt;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getIdTransaction() {
                return idTransaction;
            }

            public void setIdTransaction(String idTransaction) {
                this.idTransaction = idTransaction;
            }
        }
    }


}
