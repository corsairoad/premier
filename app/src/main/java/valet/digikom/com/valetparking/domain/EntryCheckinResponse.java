package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

/**
 * Created by dev on 1/13/17.
 */

public class EntryCheckinResponse {

    public static final int FLAG_CALL = 1;
    public static final int FLAG_CANCEL_CALL = 0;
    public static final int FLAG_UPLOAD_SUCCESS = 1;
    public static final int FLAG_UPLOAD_FAILED = 0;
    public static final int FLAG_UPLOAD_PENDING = 2;
    public static final String ID_ENTRY_CHECKIN = "entry_id";

    @SerializedName("data")
    private Data data;

    private boolean isReadyToCheckout = false;

    public EntryCheckinResponse() {
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isReadyToCheckout() {
        return isReadyToCheckout;
    }

    public void setReadyToCheckout(boolean readyToCheckout) {
        isReadyToCheckout = readyToCheckout;
    }

    public static class Data {
        @SerializedName("id")
        private String id;
        @SerializedName("type")
        private String type;
        @SerializedName("attributes")
        Attribute attribute;

        public Data() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Attribute getAttribute() {
            return attribute;
        }

        public void setAttribute(Attribute attribute) {
            this.attribute = attribute;
        }
    }

    public static class Attribute {
        @SerializedName("vthd_id")
        private int id;
        @SerializedName("vthd_clms_name")
        private String color;
        @SerializedName("vthd_coms_name")
        private String companyName;
        @SerializedName("vthd_crms_name")
        private String car;
        @SerializedName("vthd_csms_name")
        private String siteName;
        @SerializedName("vthd_drms_name_ci")
        private String dropPoint;
        @SerializedName("vthd_flms_floor_name_ci")
        private String floor;
        @SerializedName("vthd_license_plat")
        private String platNo;
        @SerializedName("vthd_man_citime")
        private String checkinTime;
        @SerializedName("vthd_padt_name")
        private String blokParkir;
        @SerializedName("vthd_padt_status")
        private String blokParkirStatus;
        @SerializedName("vthd_pafm_name")
        private String areaParkir;
        @SerializedName("vthd_pafm_status")
        private String areaParkirStatus;
        @SerializedName("vthd_pasm_name")
        private String sektorParkir;
        @SerializedName("vthd_transact_id")
        private String idTransaksi;
        @SerializedName("vthd_usms_id_admv_ci")
        private String idAdminCheckin;
        @SerializedName("vthd_usms_name_admv_ci")
        private String namaAdminCheckin;
        @SerializedName("vthd_usms_name_rnnr_tkr_ci")
        private String namaRunner;
        @SerializedName("vthd_valetfee")
        private int fee;
        @SerializedName("vthd_vfsd_name")
        private String valetType;
        @SerializedName("vthd_cbms_logo")
        private String logoMobil;
        @SerializedName("vthd_iddt_counter")
        private int lastTicketCounter;
        @SerializedName("vthd_tix_id")
        private String noTiket;

        public Attribute() {
        }

        public void setValetType(String valetType) {
            this.valetType = valetType;
        }

        public String getLogoMobil() {
            return logoMobil;
        }

        public void setLogoMobil(String logoMobil) {
            this.logoMobil = logoMobil;
        }

        public String getNoTiket() {
            return noTiket;
        }

        public void setNoTiket(String noTiket) {
            this.noTiket = noTiket;
        }

        public String getValetType() {
            return valetType;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setLastTicketCounter(int lastTicketCounter) {
            this.lastTicketCounter = lastTicketCounter;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCar() {
            return car;
        }

        public void setCar(String car) {
            this.car = car;
        }

        public String getSiteName() {
            return siteName;
        }

        public void setSiteName(String siteName) {
            this.siteName = siteName;
        }

        public String getDropPoint() {
            return dropPoint;
        }

        public void setDropPoint(String dropPoint) {
            this.dropPoint = dropPoint;
        }

        public int getLastTicketCounter() {
            return lastTicketCounter;
        }



        public String getFloor() {
            return floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }

        public String getPlatNo() {
            return platNo;
        }

        public void setPlatNo(String platNo) {
            this.platNo = platNo;
        }

        public String getCheckinTime() {
            return checkinTime;
        }

        public void setCheckinTime(String checkinTime) {
            this.checkinTime = checkinTime;
        }

        public String getBlokParkir() {
            return blokParkir;
        }

        public void setBlokParkir(String blokParkir) {
            this.blokParkir = blokParkir;
        }

        public String getBlokParkirStatus() {
            return blokParkirStatus;
        }

        public void setBlokParkirStatus(String blokParkirStatus) {
            this.blokParkirStatus = blokParkirStatus;
        }

        public String getAreaParkir() {
            return areaParkir;
        }

        public void setAreaParkir(String areaParkir) {
            this.areaParkir = areaParkir;
        }

        public String getAreaParkirStatus() {
            return areaParkirStatus;
        }

        public void setAreaParkirStatus(String areaParkirStatus) {
            this.areaParkirStatus = areaParkirStatus;
        }

        public String getSektorParkir() {
            return sektorParkir;
        }

        public void setSektorParkir(String sektorParkir) {
            this.sektorParkir = sektorParkir;
        }

        public String getIdTransaksi() {
            return idTransaksi;
        }

        public void setIdTransaksi(String idTransaksi) {
            this.idTransaksi = idTransaksi;
        }

        public String getIdAdminCheckin() {
            return idAdminCheckin;
        }

        public void setIdAdminCheckin(String idAdminCheckin) {
            this.idAdminCheckin = idAdminCheckin;
        }

        public String getNamaAdminCheckin() {
            return namaAdminCheckin;
        }

        public void setNamaAdminCheckin(String namaAdminCheckin) {
            this.namaAdminCheckin = namaAdminCheckin;
        }

        public String getNamaRunner() {
            return namaRunner;
        }

        public void setNamaRunner(String namaRunner) {
            this.namaRunner = namaRunner;
        }

        public int getFee() {
            return fee;
        }

        public void setFee(int fee) {
            this.fee = fee;
        }
    }

    public static class Table {
        public static final String TABLE_NAME = "entry_response";

        public static final String COL_ID = "_id";
        public static final String COL_RESPONSE_ID = "response_id";
        public static final String COL_JSON_RESPONSE = "json_response";
        public static final String COL_IS_UPLOADED = "is_uploaded";
        public static final String COL_IS_CHECKOUT = "is_checkout";
        public static final String COL_IS_CALLED = "is_called";
        public static final String COL_IS_READY_CHECKOUT = "is_ready";
        public static final String COL_PLAT_NO = "plat_no";
        public static final String COL_NO_TRANS = "no_transaction";
        public static final String COL_REMOTE_VTHD_ID = "remote_vthd_id";
        public static final String COL_TICKET_SEQUENCE = "ticket_sec";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RESPONSE_ID + " INTEGER, " +
                COL_PLAT_NO + " TEXT, " +
                COL_NO_TRANS + " TEXT, " +
                COL_JSON_RESPONSE + " TEXT, " +
                COL_IS_UPLOADED + " INTEGER, " +
                COL_IS_CHECKOUT + " INTEGER DEFAULT 0, " +
                COL_IS_CALLED + " INTEGER DEFAULT 0, " +
                COL_IS_READY_CHECKOUT + " INTEGER DEFAULT 0, " +
                COL_REMOTE_VTHD_ID + " INTEGER DEFAULT 0, " +
                COL_TICKET_SEQUENCE + " TEXT);";
    }


}


