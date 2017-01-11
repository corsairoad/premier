package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/11/2017.
 */

public class EntryCheckin {

    @SerializedName("type")
    private static final String type = "valet_header_transaction";
    @SerializedName("attributes")
    private EntryCheckin.Attrib attrib;

    public EntryCheckin() {
    }

    public static String getType() {
        return type;
    }

    public Attrib getAttrib() {
        return attrib;
    }

    public void setAttrib(Attrib attrib) {
        this.attrib = attrib;
    }

    public static class Attrib{
        @SerializedName("vthdDrmsIdCI")
        private String dropMasterId;
        @SerializedName("vthdLicensePlat")
        private String platNo;
        @SerializedName("vthdCsdtId")
        private String carType;
        @SerializedName("vthdClmsId")
        private String colorId;
        @SerializedName("vthdUsmsEmailCtmr")
        private String emailCustomer;
        @SerializedName("vthdCumsParaf")
        private String paraf;

        public Attrib() {
        }

        public String getDropMasterId() {
            return dropMasterId;
        }

        public void setDropMasterId(String dropMasterId) {
            this.dropMasterId = dropMasterId;
        }

        public String getPlatNo() {
            return platNo;
        }

        public void setPlatNo(String platNo) {
            this.platNo = platNo;
        }

        public String getCarType() {
            return carType;
        }

        public void setCarType(String carType) {
            this.carType = carType;
        }

        public String getColorId() {
            return colorId;
        }

        public void setColorId(String colorId) {
            this.colorId = colorId;
        }

        public String getEmailCustomer() {
            return emailCustomer;
        }

        public void setEmailCustomer(String emailCustomer) {
            this.emailCustomer = emailCustomer;
        }

        public String getParaf() {
            return paraf;
        }

        public void setParaf(String paraf) {
            this.paraf = paraf;
        }
    }

    public static class RelationShip {

        @SerializedName("valet_defect_detail_transaction")
        private DefectDetail defectDetail;
        @SerializedName("valet_additional_item_detail_transaction")
        private ItemDetail itemDetail;

        public RelationShip() {
        }

        public DefectDetail getDefectDetail() {
            return defectDetail;
        }

        public void setDefectDetail(DefectDetail defectDetail) {
            this.defectDetail = defectDetail;
        }

        public ItemDetail getItemDetail() {
            return itemDetail;
        }

        public void setItemDetail(ItemDetail itemDetail) {
            this.itemDetail = itemDetail;
        }

        public static class DefectDetail {

            @SerializedName("data")
            List<DefectItem> data;

            public DefectDetail() {
            }

            public List<DefectItem> getData() {
                return data;
            }

            public void setData(List<DefectItem> data) {
                this.data = data;
            }

            public static class DefectItem {
                @SerializedName("type")
                private static final String type = "valet_defect_detail_transaction";
                @SerializedName("attributes")
                private AttrDefect attrDefect;

                public DefectItem() {
                }

                public static String getType() {
                    return type;
                }

                public AttrDefect getAttrDefect() {
                    return attrDefect;
                }

                public void setAttrDefect(AttrDefect attrDefect) {
                    this.attrDefect = attrDefect;
                }

                public static class AttrDefect {
                    @SerializedName("vddtDsdtId")
                    String idDefect;

                    public AttrDefect() {
                    }

                    public String getIdDefect() {
                        return idDefect;
                    }

                    public void setIdDefect(String idDefect) {
                        this.idDefect = idDefect;
                    }
                }
            }
        }

        public static class ItemDetail {

            @SerializedName("data")
            private List<Item> listItem;

            public ItemDetail() {
            }

            public List<Item> getListItem() {
                return listItem;
            }

            public void setListItem(List<Item> listItem) {
                this.listItem = listItem;
            }

            public static class Item {

                @SerializedName("type")
                private static final String type = "valet_additional_item_detail_transaction";
                @SerializedName("attributes")
                private AttribItem attribItem;

                public static class AttribItem {
                    @SerializedName("vidtAidtId")
                    String itemId;

                    public AttribItem() {
                    }

                    public String getItemId() {
                        return itemId;
                    }

                    public void setItemId(String itemId) {
                        this.itemId = itemId;
                    }
                }
            }

        }

    }



}
