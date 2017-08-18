package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/9/2017.
 */

public class AdditionalItems {

    private static AdditionalItems additionalItems;

    public static class Table {
        public static final String TABLE_NAME = "additional_items";
        public static final String COL_ID = "_id";
        public static final String COL_ITEM_NAME = "item_name";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY, " +
                COL_ITEM_NAME + " TEXT)";
    }

    @SerializedName("attributes")
    AdditionalItemMaster attributes;
    //Attributes attributes;

    public AdditionalItems() {
    }


    public AdditionalItemMaster getAttributes() {
        return attributes;
    }

    public void setAttributes(AdditionalItemMaster attributes) {
        this.attributes = attributes;
    }

    /*
        public Attributes getAttributes() {
            return attributes;
        }
        */
    /*
    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }
    */
    public static class Attributes {
        @SerializedName("additional_item_master")
        AdditionalItemMaster additionalItemMaster;

        public Attributes() {
        }

        public AdditionalItemMaster getAdditionalItemMaster() {
            return additionalItemMaster;
        }

        public void setAdditionalItemMaster(AdditionalItemMaster additionalItemMaster) {
            this.additionalItemMaster = additionalItemMaster;
        }
    }

    public static class AdditionalItemMaster {
        //@SerializedName("aims_id")
        @SerializedName("aidt_id")
        private int id;
        @SerializedName("aims_name")
        private String name;

        public AdditionalItemMaster() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static AdditionalItems newInstance() {
        additionalItems  = new AdditionalItems();
        Attributes attributes = new Attributes();
        AdditionalItemMaster additionalItemMaster = new AdditionalItemMaster();
        attributes.setAdditionalItemMaster(additionalItemMaster);
        //additionalItems.setAttributes(attributes);
        return additionalItems;
    }
}


