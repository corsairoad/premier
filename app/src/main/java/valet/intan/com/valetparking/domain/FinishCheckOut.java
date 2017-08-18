package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class FinishCheckOut {

    @SerializedName("data")
    private Data data;

    public FinishCheckOut(FinishCheckOut.Builder builder) {
        data = builder.data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("type")
        private final String type = "ad_checkout_finish_fine";
        @SerializedName("attributes")
        private Attribute attribute;
        @SerializedName("relationships")
        private RelationShip relationShip;

        public Data() {
        }

        public RelationShip getRelationShip() {
            return relationShip;
        }

        public void setRelationShip(RelationShip relationShip) {
            this.relationShip = relationShip;
        }

        public String getType() {
            return type;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public void setAttribute(Attribute attribute) {
            this.attribute = attribute;
        }

        public static class Attribute {
            @SerializedName("vthdPytoId")
            private String paymentId;
            @SerializedName("vthdAppsTimeCO")
            private String checkoutTime;
            @SerializedName("vthdAppsIdCO")
            private String appId;
            @SerializedName("vthdImeiCO")
            private String deviceId;

            public void setCheckoutTime(String checkoutTime) {
                this.checkoutTime = checkoutTime;
            }

            public void setAppId(String appId) {
                this.appId = appId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public void setPaymentId(String paymentId) {
                this.paymentId = paymentId;
            }

            public String getCheckoutTime() {
                return checkoutTime;
            }
        }

        public static class RelationShip {
            @SerializedName("valet_finefee_detail_transaction")
            private FineFeeDetail fineFeeDetail;
            @SerializedName("valet_discount_detail_transaction")
            private DiscountFeeDetail discountFeeDetail;
            @SerializedName("valet_debit_bank_detail")
            private DebitBankDetail debitBankDetail;

            public RelationShip() {
            }

            public FineFeeDetail getFineFeeDetail() {
                return fineFeeDetail;
            }

            public void setFineFeeDetail(FineFeeDetail fineFeeDetail) {
                this.fineFeeDetail = fineFeeDetail;
            }

            public DiscountFeeDetail getDiscountFeeDetail() {
                return discountFeeDetail;
            }

            public void setDiscountFeeDetail(DiscountFeeDetail discountFeeDetail) {
                this.discountFeeDetail = discountFeeDetail;
            }

            public void setDebitBankDetail(DebitBankDetail debitBankDetail) {
                this.debitBankDetail = debitBankDetail;
            }

            public DebitBankDetail getDebitBankDetail() {
                return debitBankDetail;
            }
        }

        public static class FineFeeDetail {
            @SerializedName("data")
            List<DataRelationship> data;


            public FineFeeDetail() {
            }

            public List<DataRelationship> getData() {
                return data;
            }

            public void setData(List<DataRelationship> data) {
                this.data = data;
            }
        }

        public static class DiscountFeeDetail {
            @SerializedName("data")
            List<DataRelationshipDiscount> data;

            public List<DataRelationshipDiscount> getData() {
                return data;
            }

            public void setData(List<DataRelationshipDiscount> data) {
                this.data = data;
            }
        }

        public static class DebitBankDetail {
            @SerializedName("data")
            private DataRelationshipBank data;

            public void setData(DataRelationshipBank data) {
                this.data = data;
            }
        }

        public static class DataRelationship {
            @SerializedName("type")
            private final String type = "valet_finefee_detail_transaction";
            @SerializedName("attributes")
            private Attr attr;

            public DataRelationship() {
            }

            public String getType() {
                return type;
            }

            public Attr getAttr() {
                return attr;
            }

            public void setAttr(Attr attr) {
                this.attr = attr;
            }

            public static class Attr {
                @SerializedName("vfdtFfsdId")
                String idFineFee;

                public Attr() {
                }

                public String getIdFineFee() {
                    return idFineFee;
                }

                public void setIdFineFee(String idFineFee) {
                    this.idFineFee = idFineFee;
                }
            }
        }

        public static class DataRelationshipDiscount {
            @SerializedName("type")
            private final String type = "valet_discount_detail_transaction";
            @SerializedName("attributes")
            private Attr attrributes;

            public String getType() {
                return type;
            }

            public Attr getAttrributes() {
                return attrributes;
            }

            public void setAttrributes(Attr attrributes) {
                this.attrributes = attrributes;
            }

            public static class Attr {
                @SerializedName("vdddDcdtToken")
                private String idDiscount;

                public String getIdDiscount() {
                    return idDiscount;
                }

                public void setIdDiscount(String idDiscount) {
                    this.idDiscount = idDiscount;
                }
            }
        }

        public static class DataRelationshipBank {
            @SerializedName("type")
            private final String type = "valet_debit_bank_detail";
            @SerializedName("attributes")
            private Attr attr;

            public void setAttr(Attr attr) {
                this.attr = attr;
            }

            public static class Attr {
                @SerializedName("vdbdBnmsId")
                private String bankNameMasterId;
                @SerializedName("vdbdValue")
                private String noDebit;

                public void setBankNameMasterId(String bankNameMasterId) {
                    this.bankNameMasterId = bankNameMasterId;
                }

                public void setNoDebit(String noDebit) {
                    this.noDebit = noDebit;
                }
            }
        }
    }

    public static class Builder {
        private FineFee.Fine lostTicketFee;
        private FineFee.Fine overnightFee;
        private MembershipResponse.Data membership;
        private PaymentMethod.Data paymentData;
        private Bank.Data bankData;
        private String voucher;
        private String cardNo;
        private String checkoutTime;
        private String appId;
        private String deviceId;

        private Data data;

        public Builder() {
        }

        public String getCheckoutTime() {
            return checkoutTime;
        }

        public void setCheckoutTime(String checkoutTime) {
            this.checkoutTime = checkoutTime;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getVoucher() {
            return voucher;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public void setBankData(Bank.Data bankData) {
            this.bankData = bankData;
        }

        public void setPaymentData(PaymentMethod.Data paymentData) {
            this.paymentData = paymentData;
        }

        public void setVoucher(String voucher) {
            this.voucher = voucher;
        }

        public FineFee.Fine getLostTicketFee() {
            return lostTicketFee;
        }

        public void setLostTicketFee(FineFee.Fine lostTicketFee) {
            this.lostTicketFee = lostTicketFee;
        }

        public FineFee.Fine getOvernightFee() {
            return overnightFee;
        }

        public void setOvernightFee(FineFee.Fine overnightFee) {
            this.overnightFee = overnightFee;
        }

        public MembershipResponse.Data getMembership() {
            return membership;
        }

        public void setMembership(MembershipResponse.Data membership) {
            this.membership = membership;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public FinishCheckOut build() {
            data = new Data();
            Data.Attribute attribute = new Data.Attribute();
            Data.RelationShip relationShip = new Data.RelationShip();


            attribute.setDeviceId(getDeviceId());
            attribute.setAppId(getAppId());
            attribute.setCheckoutTime(getCheckoutTime());
            // setup fine fee json object (lost ticket)
            Data.FineFeeDetail fineFeeDetail = new Data.FineFeeDetail();
            List<Data.DataRelationship> relationshipDataList = new ArrayList<>();

            if (lostTicketFee != null) {
                Data.DataRelationship dataRelationship = new Data.DataRelationship();
                Data.DataRelationship.Attr attr = new Data.DataRelationship.Attr();

                attr.setIdFineFee(lostTicketFee.getId());
                dataRelationship.setAttr(attr);

                relationshipDataList.add(dataRelationship);
            }

            if (overnightFee != null) {
                Data.DataRelationship dataRelationship = new Data.DataRelationship();
                Data.DataRelationship.Attr attr = new Data.DataRelationship.Attr();

                attr.setIdFineFee(overnightFee.getId());
                dataRelationship.setAttr(attr);

                relationshipDataList.add(dataRelationship);
            }

            if (!relationshipDataList.isEmpty()) {
                fineFeeDetail.setData(relationshipDataList);
                relationShip.setFineFeeDetail(fineFeeDetail);
            }

            // setup discount fee json object
            Data.DiscountFeeDetail discountFeeDetail = new Data.DiscountFeeDetail();
            List<Data.DataRelationshipDiscount> relationshipDiscountList = new ArrayList<>();

            Data.DebitBankDetail debitBankDetail = new Data.DebitBankDetail();
            Data.DataRelationshipBank dataRelationshipBank = new Data.DataRelationshipBank();
            if (paymentData != null) {
                attribute.setPaymentId(String.valueOf(paymentData.getAttr().getPaymentId()));
                data.setAttribute(attribute);
                if (paymentData.getAttr().getPaymentId() != 1){ // not cash
                    if (paymentData.getAttr().getPaymentId() != 4) { // not debit
                        Data.DataRelationshipDiscount dataRelationshipDiscount = new Data.DataRelationshipDiscount();
                        Data.DataRelationshipDiscount.Attr attr = new Data.DataRelationshipDiscount.Attr();

                        attr.setIdDiscount(cardNo);
                        dataRelationshipDiscount.setAttrributes(attr);

                        relationshipDiscountList.add(dataRelationshipDiscount);
                    } else { // debit
                        Data.DataRelationshipBank.Attr attr = new Data.DataRelationshipBank.Attr();
                        attr.setBankNameMasterId(String.valueOf(bankData.getAttr().getBankId()));
                        attr.setNoDebit(cardNo);
                        dataRelationshipBank.setAttr(attr);
                        debitBankDetail.setData(dataRelationshipBank);
                        relationShip.setDebitBankDetail(debitBankDetail);
                    }
                }

            }

            if (voucher != null) {
                Data.DataRelationshipDiscount dataRelationshipDiscount = new Data.DataRelationshipDiscount();
                Data.DataRelationshipDiscount.Attr attr = new Data.DataRelationshipDiscount.Attr();

                attr.setIdDiscount(voucher);
                dataRelationshipDiscount.setAttrributes(attr);

                relationshipDiscountList.add(dataRelationshipDiscount);
            }

            if (!relationshipDiscountList.isEmpty()) {
                discountFeeDetail.setData(relationshipDiscountList);
                relationShip.setDiscountFeeDetail(discountFeeDetail);
                data.setRelationShip(relationShip);
            }

            if (relationShip.getDebitBankDetail() != null) {
                data.setRelationShip(relationShip);
            }

            return new FinishCheckOut(this);
        }


    }

    public static class Table {
        public static final String TABLE_NAME = "checkout_data";

        public static final String COL_ID = "_id";
        public static final String COL_JSON_DATA = "json_data";
        public static final String COL_DATA_ID = "data_id";
        public static final String COL_NO_TIKET = "no_tiket";
        public static final String COL_STATUS = "status";
        public static final String COL_ID_STILL_FAKE = "is_still_fake";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_JSON_DATA + " TEXT, " +
                COL_DATA_ID + " INTEGER, " +
                COL_NO_TIKET + " TEXT, " +
                COL_STATUS + " INTEGER DEFAULT 0, " +
                COL_ID_STILL_FAKE + " INTEGER DEFAULT 0);";
    }
}
