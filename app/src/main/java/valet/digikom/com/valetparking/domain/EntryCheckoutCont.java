package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/23/2017.
 */

public class EntryCheckoutCont {

    @SerializedName("data")
    List<EntryChekout> chekoutList;

    public EntryCheckoutCont() {
    }

    public List<EntryChekout> getChekoutList() {
        return chekoutList;
    }

    public void setChekoutList(List<EntryChekout> chekoutList) {
        this.chekoutList = chekoutList;
    }

    public static class EntryChekout{
        @SerializedName("type")
        private static final String type = "kg_entry_checkout";
        @SerializedName("id")
        private String id;
        @SerializedName("attributes")
        private Attrib attrib;

        public EntryChekout() {
        }

        public static String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Attrib getAttrib() {
            return attrib;
        }

        public void setAttrib(Attrib attrib) {
            this.attrib = attrib;
        }

        public static class Attrib{
            @SerializedName("vthd_id")
            private int id;
            @SerializedName("vthd_date_car_driving")
            private String dateCarDriving;
            @SerializedName("vthd_transact_id")
            private String idTransaction;
            @SerializedName("vthd_license_plat")
            private String platNo;
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
            @SerializedName("vthd_usms_name_rnnr_kgr_co")
            private String runnerCheckout;
            @SerializedName("vthd_kems_name")
            private String kgSite;

            public Attrib() {
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getDateCarDriving() {
                return dateCarDriving;
            }

            public void setDateCarDriving(String dateCarDriving) {
                this.dateCarDriving = dateCarDriving;
            }

            public String getIdTransaction() {
                return idTransaction;
            }

            public void setIdTransaction(String idTransaction) {
                this.idTransaction = idTransaction;
            }

            public String getPlatNo() {
                return platNo;
            }

            public void setPlatNo(String platNo) {
                this.platNo = platNo;
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

            public String getRunnerCheckout() {
                return runnerCheckout;
            }

            public void setRunnerCheckout(String runnerCheckout) {
                this.runnerCheckout = runnerCheckout;
            }

            public String getKgSite() {
                return kgSite;
            }

            public void setKgSite(String kgSite) {
                this.kgSite = kgSite;
            }
        }
    }

    public static class Table {
        public static final String TABLE_NAME = "entry_checkout";

        public static final String COL_ID = "_id";
        public static final String COL_RESPONSE_ID = "id_response";
        public static final String COL_JSON_ENTRY_CHECKOUT = "json";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RESPONSE_ID + " TEXT, " +
                COL_JSON_ENTRY_CHECKOUT + " TEXT);";
    }
}
