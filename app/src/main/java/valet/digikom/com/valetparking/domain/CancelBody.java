package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 2/18/2017.
 */

public class CancelBody {
    @SerializedName("data")
    private Data data;

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("type")
        private final String type = "ad_valet_cancelation";
        @SerializedName("attributes")
        private Attr attributes;

        public void setAttributes(Attr attributes) {
            this.attributes = attributes;
        }

        public static class Attr {
            @SerializedName("vthdCancelInfo")
            private String cancelInfo;

            public void setCancelInfo(String cancelInfo) {
                this.cancelInfo = cancelInfo;
            }
        }
    }

    public static class Builder {
        private String cancelInfo;

        public Builder setCancelInfo(String cancelInfo) {
            this.cancelInfo = cancelInfo;
            return this;
        }

        public CancelBody build() {
            CancelBody cancelBody = new CancelBody();
            Data data = new Data();
            Data.Attr attr = new Data.Attr();
            attr.setCancelInfo(cancelInfo);
            data.setAttributes(attr);
            cancelBody.setData(data);
            return cancelBody;
        }
    }
}
