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

            public RelationShip() {
            }

            public FineFeeDetail getFineFeeDetail() {
                return fineFeeDetail;
            }

            public void setFineFeeDetail(FineFeeDetail fineFeeDetail) {
                this.fineFeeDetail = fineFeeDetail;
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
    }

    public static class Builder {
        private FineFee.Fine lostTicketFee;
        private FineFee.Fine overnightFee;
        private Data data;

        public Builder() {
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
                data.setRelationShip(relationShip);
            }
            return new FinishCheckOut(this);
        }
    }
}
