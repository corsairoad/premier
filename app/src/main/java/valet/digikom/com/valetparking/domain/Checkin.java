package valet.digikom.com.valetparking.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 12/20/2016.
 */

public class Checkin implements Parcelable {

    protected Checkin(Parcel in) {
        id = in.readInt();
        transactionId = in.readString();
        platNo = in.readString();
        merkMobil = in.readString();
        warnaMobil = in.readString();
        jenisMobil = in.readString();
        emailCustomer = in.readString();
        runnerName = in.readString();
        dropPoint = in.readString();
        adminId = in.readString();
        defects = in.createStringArrayList();
        stuffs = in.createStringArrayList();
    }

    public static final Creator<Checkin> CREATOR = new Creator<Checkin>() {
        @Override
        public Checkin createFromParcel(Parcel in) {
            return new Checkin(in);
        }

        @Override
        public Checkin[] newArray(int size) {
            return new Checkin[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(transactionId);
        parcel.writeString(platNo);
        parcel.writeString(merkMobil);
        parcel.writeString(warnaMobil);
        parcel.writeString(jenisMobil);
        parcel.writeString(emailCustomer);
        parcel.writeString(runnerName);
        parcel.writeString(dropPoint);
        parcel.writeString(adminId);
        parcel.writeStringList(defects);
        parcel.writeStringList(stuffs);
    }

    public static class Table{
        public static final String TABLE_NAME = "checkin";

        public static final String COL_ID = "_id";
        public static final String COL_TRANSACTION_ID = "transaction_id";
        public static final String COL_PLAT_NO = "plat_no";
        public static final String COL_MERK = "merk";
        public static final String COL_WARNA = "warna";
        public static final String COL_CHECKIN_TIME = "checkin_time";
        public static final String COL_JENIS = "jenis";
        public static final String COL_EMAIL = "email";
        public static final String COL_RUNNER = "runner_name";
        public static final String COL_DROP_POINT = "drop_point";
        public static final String COL_LEFT_DEFECT = "left_defect";
        public static final String COL_RIGHT_DEFECT = "right_defect";
        public static final String COL_TOP_DEFECT = "top_defect";
        public static final String COL_BOTTOM_DEFECT = "bottom_defect";
        public static final String COL_BACK_DEFECT = "back_defect";
        public static final String COL_FRONT_DEFECT = "front_defect";
        public static final String COL_DEFECTS = "defects";
        public static final String COL_STUFFS = "stuffs";
        public static final String COL_ADMIN_ID = "admin_id";
        public static final String COL_IMAGE_URL = "image_url";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY, " +
                COL_TRANSACTION_ID + " TEXT, " +
                COL_PLAT_NO + " TEXT, " +
                COL_MERK + " TEXT, " +
                COL_WARNA + " TEXT, " +
                COL_CHECKIN_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COL_JENIS + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_RUNNER + " TEXT, " +
                COL_DROP_POINT + " TEXT, " +
                COL_DEFECTS + " TEXT, " +
                COL_STUFFS + " TEXT, " +
                COL_ADMIN_ID + " TEXT, " +
                COL_IMAGE_URL + " TEXT)";
    }

    private int id;
    private String transactionId;
    private String platNo;
    private String merkMobil;
    private String warnaMobil;
    private Date checkinTime = new Date();
    private String jenisMobil;
    private String emailCustomer;
    private String runnerName;
    private String dropPoint;
    private String adminId;
<<<<<<< HEAD
    List<String> defects = new ArrayList<>();
    List<String> stuffs = new ArrayList<>();
=======
    private List<String> defects = new ArrayList<>();
    private List<String> stuffs = new ArrayList<>();
>>>>>>> 31c6fece6bd34924f286228d90b5ba2529c385d7
    private String imageUrl;

    public Checkin() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPlatNo() {
        return platNo;
    }

    public void setPlatNo(String platNo) {
        this.platNo = platNo;
    }

    public String getMerkMobil() {
        return merkMobil;
    }

    public void setMerkMobil(String merkMobil) {
        this.merkMobil = merkMobil;
    }

    public String getWarnaMobil() {
        return warnaMobil;
    }

    public void setWarnaMobil(String warnaMobil) {
        this.warnaMobil = warnaMobil;
    }

    private Date getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(Date checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getJenisMobil() {
        return jenisMobil;
    }

    public void setJenisMobil(String jenisMobil) {
        this.jenisMobil = jenisMobil;
    }

    public String getEmailCustomer() {
        return emailCustomer;
    }

    public void setEmailCustomer(String emailCustomer) {
        this.emailCustomer = emailCustomer;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }

    public String getDropPoint() {
        return dropPoint;
    }

    public void setDropPoint(String dropPoint) {
        this.dropPoint = dropPoint;
    }



    public List<String> getDefects() {
        return defects;
    }

    public List<String> getStuffs() {
        return stuffs;
    }

    public String defectsToString() {
        StringBuilder sb = new StringBuilder();
        for (String s : defects) {
            sb.append(s)
                    .append("\n\n");
        }
        return sb.toString();
    }

    public String stuffsToString() {
        StringBuilder sb = new StringBuilder();
        for (String s : stuffs) {
            sb.append(s)
                    .append("\n\n");
        }
        return sb.toString();
    }

<<<<<<< HEAD
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSignatureName() {
        StringBuilder sb = new StringBuilder();
        sb.append(getJenisMobil().trim()).append("_")
                .append(getMerkMobil().trim()).append("_")
                .append(getPlatNo().trim()).append("_").
                append(getCheckinTime().getTime()).append(".jpg");
        return sb.toString();
=======
    public String getSignatureName() {
        String name = getJenisMobil().trim() + "_" +
                getMerkMobil().trim() + "_" +
                getPlatNo().trim() + "_" +
                getCheckinTime().getTime() + ".jpg";
        return name;
>>>>>>> 31c6fece6bd34924f286228d90b5ba2529c385d7
    }
}
