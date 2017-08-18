package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 2/14/2017.
 */

public class ClosingBody {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("type")
        private final String type = "report_administrative";
        @SerializedName("attributes")
        private Attr attr;

        public void setAttr(Attr attr) {
            this.attr = attr;
        }

        public String getType() {
            return type;
        }

        public Attr getAttr() {
            return attr;
        }

        public static class Attr {
            @SerializedName("readInfo")
            private String readInfo;
            @SerializedName("readStartReportDate")
            private String readStartDate;
            @SerializedName("readEndReportDate")
            private String readEndDate;

            public void setReadInfo(String readInfo) {
                this.readInfo = readInfo;
            }

            public void setReadStartDate(String readStartDate) {
                this.readStartDate = readStartDate;
            }

            public void setReadEndDate(String readEndDate) {
                this.readEndDate = readEndDate;
            }

            public String getReadInfo() {
                return readInfo;
            }

            public String getReadStartDate() {
                return readStartDate;
            }

            public String getReadEndDate() {
                return readEndDate;
            }
        }
    }
}
