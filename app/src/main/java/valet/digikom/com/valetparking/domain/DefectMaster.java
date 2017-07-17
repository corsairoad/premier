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
        public static final String COL_X_AXIS = "x_axis";
        public static final String COL_Y_AXIS = "y_axis";
        public static final String COL_IMAGE_WIDTH = "img_width";
        public static final String COL_IMAGE_HEIGHT = "img_height";
        public static final String COL_IMAGE_NAME = "img_name";
        public static final String COL_IMAGE_PATH = "img_path";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY, " +
                COL_NAME + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_X_AXIS + " REAL, " +
                COL_Y_AXIS + " REAL, " +
                COL_IMAGE_WIDTH + " REAL, " +
                COL_IMAGE_HEIGHT + " REAL, " +
                COL_IMAGE_NAME + " TEXT, " +
                COL_IMAGE_PATH + " TEXT)";
    }

    @SerializedName("attributes")
    private DefectAttributes attributes;

    public DefectMaster() {
    }

    public DefectAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(DefectAttributes attributes) {
        this.attributes = attributes;
    }

    public static class DefectAttributes {

        @SerializedName("defect_master")
        private Defect defect;
        @SerializedName("dfdt_x")
        private float xAxis;
        @SerializedName("dfdt_y")
        private float yAxis;
        @SerializedName("dfdt_width")
        private float imgWidth;
        @SerializedName("dfdt_height")
        private float imgHeight;
        @SerializedName("dfdt_id")
        private int defectId;

        public DefectAttributes() {
        }
        public Defect getDefect() {
            return defect;
        }

        public float getxAxis() {
            return xAxis;
        }

        public void setxAxis(float xAxis) {
            this.xAxis = xAxis;
        }

        public float getyAxis() {
            return yAxis;
        }

        public void setyAxis(float yAxis) {
            this.yAxis = yAxis;
        }

        public float getImgWidth() {
            return imgWidth;
        }

        public void setImgWidth(float imgWidth) {
            this.imgWidth = imgWidth;
        }

        public float getImgHeight() {
            return imgHeight;
        }

        public void setImgHeight(float imgHeight) {
            this.imgHeight = imgHeight;
        }

        public int getDefectId() {
            return defectId;
        }

        public void setDefectId(int defectId) {
            this.defectId = defectId;
        }

        public void setDefect(Defect defect) {
            this.defect = defect;
        }


    }

    public static class Defect{
        @SerializedName("dfms_id")
        private int id;
        @SerializedName("dfms_name")
        private String defectName;
        @SerializedName("dfms_desc")
        private String defectDesc;
        @SerializedName("dfms_file_name")
        private String fileName;
        @SerializedName("dfms_path_name")
        private String filePath;


        public Defect() {
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
