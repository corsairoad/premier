package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/10/2017.
 */

public class ColorMaster {

    public static class Table {
        public static final String TABLE_NAME = "color_master";

        public static final String COL_ID = "_id";
        public static final String COL_COLOR_NAME = "color_name";
        public static final String COL_COLOR_ID = "color_id";
        public static final String COL_COLOR_HEX = "color_hex";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "( " + COL_ID + " INTEGER PRIMARY KEY, " +
                COL_COLOR_NAME + " TEXT, " +
                COL_COLOR_HEX + " TEXT, " +
                COL_COLOR_ID + " INTEGER)";
    }

    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private int id;
    @SerializedName("attributes")
    private Attrib attrib;

    public ColorMaster() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Attrib getAttrib() {
        return attrib;
    }

    public void setAttrib(Attrib attrib) {
        this.attrib = attrib;
    }

    public static class Attrib {
        @SerializedName("clms_id")
        private int id_color;
        @SerializedName("clms_name")
        private String colorName;
        @SerializedName("clms_hex")
        private String colorHex;

        public Attrib() {
        }

        public int getId_color() {
            return id_color;
        }

        public void setId_color(int id_color) {
            this.id_color = id_color;
        }

        public String getColorName() {
            return colorName;
        }

        public void setColorName(String colorName) {
            this.colorName = colorName;
        }

        public String getColorHex() {
            return colorHex;
        }

        public void setColorHex(String colorHex) {
            this.colorHex = colorHex;
        }
    }
}
