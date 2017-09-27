package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 2/13/2017.
 */

public class ClosingData {

    @SerializedName("data")
    private List<Data> dataList;
    @SerializedName("links")
    private Links links;
    @SerializedName("meta")
    private Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public List<Data> getDataList() {
        return dataList;
    }

    public Links getLinks() {
        return links;
    }

    public static class Data {
        @SerializedName("type")
        private final String type = "ad_checkout_finish_fine";
        @SerializedName("id")
        private String id;
        @SerializedName("attributes")
        private Attr attributes;
        @SerializedName("links")
        private Links links;

        public Links getLinks() {
            return links;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public Attr getAttributes() {
            return attributes;
        }

        public static class Attr {
            @SerializedName("created_at")
            private String createdAt;
            @SerializedName("deleted_at")
            private String deletedAt;
            @SerializedName("updated_at")
            private String updatedAt;
            @SerializedName("vthd_cbms_logo")
            private String logoMobil;
            @SerializedName("vthd_id")
            private String valetHeaderId;
            @SerializedName("vthd_vfsd_key")
            private String valetTpyeKey;
            @SerializedName("vthd_vfsd_name")
            private String valetTypeName;
            @SerializedName("vthd_vfsd_type")
            private String valetType;
            @SerializedName("vthd_transact_id")
            private String transactionId;
            @SerializedName("vthd_valetfee")
            private int valetFee;
            @SerializedName("vthd_finefee")
            private int valetFineFee;
            @SerializedName("vthd_total")
            private int valetTotalFee;
            @SerializedName("vthd_license_plat_trim")
            private String platNo;
            @SerializedName("vthd_man_citime")
            private String checkIn;
            @SerializedName("vthd_man_cotime")
            private String checkout;
            @SerializedName("vthd_tix_id")
            private String noTiket;
            @SerializedName("vthd_apps_time_ci")
            private String checkinTimeApp;
            @SerializedName("vthd_apps_time_co")
            private String checkoutTimeApp;

            public String getCheckinTimeApp() {
                return checkinTimeApp;
            }

            public void setCheckinTimeApp(String checkinTimeApp) {
                this.checkinTimeApp = checkinTimeApp;
            }

            public String getCheckoutTimeApp() {
                return checkoutTimeApp;
            }

            public void setCheckoutTimeApp(String checkoutTimeApp) {
                this.checkoutTimeApp = checkoutTimeApp;
            }

            public String getCreatedAt() {
                return createdAt;
            }

            public String getDeletedAt() {
                return deletedAt;
            }

            public String getUpdatedAt() {
                return updatedAt;
            }

            public String getLogoMobil() {
                return logoMobil;
            }

            public String getNoTiket() {
                return noTiket;
            }

            public String getValetHeaderId() {
                return valetHeaderId;
            }

            public String getValetTypeName() {
                return valetTypeName;
            }

            public String getValetType() {
                return valetType;
            }

            public String getTransactionId() {
                return transactionId;
            }

            public int getValetFee() {
                return valetFee;
            }

            public int getValetFineFee() {
                return valetFineFee;
            }

            public int getValetTotalFee() {
                return valetTotalFee;
            }

            public String getPlatNo() {
                return platNo;
            }

            public String getCheckIn() {
                return checkIn;
            }

            public String getCheckout() {
                return checkout;
            }

            public String getValetTpyeKey() {
                return valetTpyeKey;
            }


        }

        public static class Links {
            @SerializedName("self")
            private Self self;

            public Self getSelf() {
                return self;
            }

            public static class Self {
                @SerializedName("href")
                private String href;

                public String getHref() {
                    return href;
                }
            }
        }
    }

    public static class Links{
        @SerializedName("self")
        private Data.Links.Self self;
        @SerializedName("first")
        private First first;
        @SerializedName("next")
        private Next next;
        @SerializedName("last")
        private Last last;

        public Data.Links.Self getSelf() {
            return self;
        }

        public First getFirst() {
            return first;
        }

        public Next getNext() {
            return next;
        }

        public Last getLast() {
            return last;
        }

        public static class Self{
            @SerializedName("href")
            private String href;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }
        }

        public static class First{
            @SerializedName("href")
            private String href;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }
        }

        public static class Next{
            @SerializedName("href")
            private String href;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }
        }

        public static class Last{
            @SerializedName("href")
            private String href;

            public String getHref() {
                return href;
            }

            public void setHref(String href) {
                this.href = href;
            }
        }
    }

    public static class Meta {

        @SerializedName("page")
        private Page page;

        public Page getPage() {
            return page;
        }

        public static class Page {
            @SerializedName("total")
            private int total;
            @SerializedName("last")
            private int last;
            @SerializedName("number")
            private int number;
            @SerializedName("size")
            private int size;

            public int getTotal() {
                return total;
            }

            public int getLast() {
                return last;
            }

            public int getNumber() {
                return number;
            }

            public int getSize() {
                return size;
            }
        }
    }
}
