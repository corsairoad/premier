package valet.digikom.com.valetparking.domain;

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

        public  String getType() {
            return type;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public void setAttribute(Attribute attribute) {
            this.attribute = attribute;
        }

        public static class Attribute {

        }

        public static class RelationShip {
            @SerializedName("valet_finefee_detail_transaction")
            private FineFeeDetail fineFeeDetail;
            @SerializedName("valet_discount_detail_transaction")
            private DiscountFeeDetail discountFeeDetail;

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
    }

    public static class Builder {
        private FineFee.Fine lostTicketFee;
        private FineFee.Fine overnightFee;
        MembershipResponse.Data membership;
        String voucher;

        private Data data;

        public Builder() {
        }

        public String getVoucher() {
            return voucher;
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
            data.setAttribute(attribute);

            Data.RelationShip relationShip = new Data.RelationShip();

            // setup fine fee json object
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

            if (membership != null) {
                Data.DataRelationshipDiscount dataRelationshipDiscount = new Data.DataRelationshipDiscount();
                Data.DataRelationshipDiscount.Attr attr = new Data.DataRelationshipDiscount.Attr();

                attr.setIdDiscount(membership.getAttr().getToken());
                dataRelationshipDiscount.setAttrributes(attr);

                relationshipDiscountList.add(dataRelationshipDiscount);
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
            }

            data.setRelationShip(relationShip);

            return new FinishCheckOut(this);
        }


    }
}
