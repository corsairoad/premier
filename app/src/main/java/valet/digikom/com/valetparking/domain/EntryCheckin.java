package valet.digikom.com.valetparking.domain;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import valet.digikom.com.valetparking.util.BitmapToString;

/**
 * Created by DIGIKOM-EX4 on 1/11/2017.
 */

public class EntryCheckin {

    @SerializedName("type")
    private String type = "ad_entry_checkin";
    @SerializedName("attributes")
    private EntryCheckin.Attrib attrib;
    @SerializedName("relationships")
    private RelationShip relationShip;

    public EntryCheckin (EntryCheckin.Builder builder) {
        attrib = builder.attrib;
        relationShip = builder.relationShip;
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

    public void setType(String type) {
        this.type = type;
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
        @SerializedName("vthdDefectsBlob")
        private String defect;

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
            this.paraf = "paraf";
        }

        public String getDefect() {
            return defect;
        }

        public void setDefect(String defect) {
            this.defect = "defect";
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
                private String type = "valet_defect_detail_transaction";
                @SerializedName("attributes")
                private AttrDefect attrDefect;

                public DefectItem() {
                }

                public  String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
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
                private String type = "valet_additional_item_detail_transaction";
                @SerializedName("attributes")
                private AttribItem attribItem;


                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public AttribItem getAttribItem() {
                    return attribItem;
                }

                public void setAttribItem(AttribItem attribItem) {
                    this.attribItem = attribItem;
                }

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

    public static class Builder{

        private Attrib attrib;
        private RelationShip relationShip;

        public Builder() {
        }

        public Builder setAttribute(DropPointMaster dropPointMaster, String platNo, CarMaster carMaster, ColorMaster colorMaster, String email, Bitmap bmpDefects, Bitmap bmpSignature) {
            attrib = new Attrib();
            attrib.setDropMasterId(String.valueOf(dropPointMaster.getAttrib().getDropId()));
            attrib.setPlatNo(platNo);
            attrib.setCarType(String.valueOf(carMaster.getAttrib().getId_attrib()));
            attrib.setColorId(String.valueOf(colorMaster.getAttrib().getId_color()));
            attrib.setEmailCustomer(email);
            attrib.setDefect(BitmapToString.create(bmpDefects));
            attrib.setParaf(BitmapToString.create(bmpSignature));

            return this;
        }

        public Builder setRelationShip(List<DefectMaster> defectMasterList, List<AdditionalItems> itemsList){
            relationShip = new RelationShip();

            RelationShip.DefectDetail defectDetail = new RelationShip.DefectDetail();
            List<RelationShip.DefectDetail.DefectItem> defectItems = new ArrayList<>();
            for (DefectMaster dm : defectMasterList) {
                RelationShip.DefectDetail.DefectItem defectItem = new RelationShip.DefectDetail.DefectItem();
                RelationShip.DefectDetail.DefectItem.AttrDefect attrDefect = new RelationShip.DefectDetail.DefectItem.AttrDefect();
                attrDefect.setIdDefect(String.valueOf(dm.getAttributes().getId()));
                defectItem.setAttrDefect(attrDefect);

                defectItems.add(defectItem);
            }
            defectDetail.setData(defectItems);


            RelationShip.ItemDetail itemDetail = new RelationShip.ItemDetail();
            List<RelationShip.ItemDetail.Item> items = new ArrayList<>();

            for (AdditionalItems i : itemsList) {
                RelationShip.ItemDetail.Item item = new RelationShip.ItemDetail.Item();
                RelationShip.ItemDetail.Item.AttribItem attribItem = new RelationShip.ItemDetail.Item.AttribItem();
                attribItem.setItemId(String.valueOf(i.getAttributes().getAdditionalItemMaster().getId()));
                item.setAttribItem(attribItem);

                items.add(item);
            }
            itemDetail.setListItem(items);

            relationShip.setDefectDetail(defectDetail);
            relationShip.setItemDetail(itemDetail);
            return this;
        }

        public EntryCheckin build() {
            return new EntryCheckin(this);
        }
    }



}
