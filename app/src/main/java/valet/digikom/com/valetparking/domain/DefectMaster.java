package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dev on 1/7/17.
 */

public class DefectMaster {

    public static class Table {
        public static final String TABLE_NAME = "defect_master";
        public static final String COL_ID = "_id";
        public static final String COL_NAME = "defect_name";
        public static final String COL_DESC = "defect_desc";
        public static final String COL_HREF = "href";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY, " +
                COL_NAME + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_HREF + " TEXT)";
    }

    @SerializedName("id")
    private int id;
    @SerializedName("attributes")
    private DefectAttributes attributes;

    public DefectMaster() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DefectAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(DefectAttributes attributes) {
        this.attributes = attributes;
    }

    public static class DefectAttributes {

        @SerializedName("dfms_name")
        private String defectName;
        @SerializedName("dfms_desc")
        private String defectDesc;
        @SerializedName("href")
        private String href;

        public DefectAttributes() {
        }

        public String getDefectName() {
            return defectName;
        }

        public void setDefectName(String defectName) {
            this.defectName = defectName;
        }

        public String getDefectDesc() {
            return defectDesc;
        }

        public void setDefectDesc(String defectDesc) {
            this.defectDesc = defectDesc;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }
}
