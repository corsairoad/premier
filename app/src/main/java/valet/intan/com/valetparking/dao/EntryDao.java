package valet.intan.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import valet.intan.com.valetparking.domain.ClosingData;
import valet.intan.com.valetparking.domain.EntryCheckinResponse;
import valet.intan.com.valetparking.service.LoggingUtils;
import valet.intan.com.valetparking.util.CheckinComparator;
import valet.intan.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/18/2017.
 */

public class EntryDao {

    private ValetDbHelper dbHelper;
    private static EntryDao entryDao;
    private Gson gson;

    private EntryDao(ValetDbHelper dbHelper) {
        this.dbHelper = dbHelper;
        gson = new Gson();
    }

    public static EntryDao getInstance(Context context) {
        if (entryDao == null) {
            entryDao = new EntryDao(new ValetDbHelper(context));
        }
        return entryDao;
    }


    public void insertEntry(int id, String jsonEntry) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Table.COL_ID_CHECKIN, id);
        cv.put(Table.COL_JSON_ENTRY, jsonEntry);

        db.insert(Table.TABLE_NAME,null,cv);

        db.close();
    }

    public void insertEntryResponse(EntryCheckinResponse response, int flagUpload) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(response);
        String platNo = response.getData().getAttribute().getPlatNo();
        String noTrans = response.getData().getAttribute().getIdTransaksi();
        int id = response.getData().getAttribute().getId();
        //int fakeVthdId = response.getData().getAttribute().getLastTicketCounter(); // fakeVthdid is lastticketcounter

        ContentValues cv = new ContentValues();
        cv.put(EntryCheckinResponse.Table.COL_RESPONSE_ID, id); // fakevthdid
        cv.put(EntryCheckinResponse.Table.COL_JSON_RESPONSE, jsonResponse);
        cv.put(EntryCheckinResponse.Table.COL_PLAT_NO, platNo);
        cv.put(EntryCheckinResponse.Table.COL_NO_TRANS, noTrans); // no tiket
        cv.put(EntryCheckinResponse.Table.COL_REMOTE_VTHD_ID, id); // fakevthdid
        cv.put(EntryCheckinResponse.Table.COL_TICKET_SEQUENCE, noTrans); // no tiket
        cv.put(EntryCheckinResponse.Table.COL_IS_CHECKOUT, 0);
        cv.put(EntryCheckinResponse.Table.COL_IS_UPLOADED, flagUpload);

        db.insert(EntryCheckinResponse.Table.TABLE_NAME,null,cv);

        //db.close();
    }

    public void updateUploadFlag(int id, int flag) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String[] args = new String[] {String.valueOf(flag)};
        cv.put(EntryCheckinResponse.Table.COL_IS_UPLOADED, flag);
        db.update(EntryCheckinResponse.Table.TABLE_NAME, cv, EntryCheckinResponse.Table.COL_RESPONSE_ID + "=?", args);
    }

    public int updateRemoteAndTicketSequenceId(String fakeVthdId, int remoteVthdId, String ticketSeq) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {fakeVthdId};

        ContentValues cv = new ContentValues();
        cv.put(EntryCheckinResponse.Table.COL_REMOTE_VTHD_ID, remoteVthdId);
        cv.put(EntryCheckinResponse.Table.COL_TICKET_SEQUENCE, ticketSeq);
        cv.put(EntryCheckinResponse.Table.COL_IS_UPLOADED, EntryCheckinResponse.FLAG_UPLOAD_SUCCESS);

        return db.update(EntryCheckinResponse.Table.TABLE_NAME,cv, EntryCheckinResponse.Table.COL_RESPONSE_ID + " = ? ", args);
    }

    public int updateRemoteAndTicketSecByTicketNo(String ticketNo, int remoteVthdId, String ticketSeq) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {ticketNo};

        ContentValues cv = new ContentValues();
        cv.put(EntryCheckinResponse.Table.COL_REMOTE_VTHD_ID, remoteVthdId);
        cv.put(EntryCheckinResponse.Table.COL_TICKET_SEQUENCE, ticketSeq);
        cv.put(EntryCheckinResponse.Table.COL_IS_UPLOADED, EntryCheckinResponse.FLAG_UPLOAD_SUCCESS);

        return db.update(EntryCheckinResponse.Table.TABLE_NAME,cv, EntryCheckinResponse.Table.COL_TICKET_SEQUENCE + " = ? ", args);
    }

    public void insertListCheckin(List<EntryCheckinResponse.Data> checkinList) {

        //int removedRows = removeUploadSuccess();
        //removeUploadSuccess(checkinList);
        filterUnsyncedData(checkinList); // remove unsynced data with synced one

        checkinList = getNewListCheckin(checkinList);

        if (checkinList == null) {
            return;
        }

        FinishCheckoutDao finishCheckoutDao = FinishCheckoutDao.getInstance(dbHelper.getContext());

        if (!checkinList.isEmpty()) {
            for (EntryCheckinResponse.Data e : checkinList) {
                if (e != null) {
                    //removeEntryById(e.getAttribute().getId());
                    //int remoteVthdId = e.getAttribute().getId();
                    String noTiket = e.getAttribute().getNoTiket().trim().replace(" ","");
                    if (!finishCheckoutDao.isAlreadyCheckout(noTiket)) {
                        EntryCheckinResponse entryCheckinResponse = new EntryCheckinResponse();
                        entryCheckinResponse.setData(e);
                        insertEntryResponse(entryCheckinResponse,EntryCheckinResponse.FLAG_UPLOAD_SUCCESS);

                        LoggingUtils.getInstance(dbHelper.getContext()).logAddParkedCarFromOtherDevices(entryCheckinResponse);
                    }
                }
            }
        }

    }

    private void filterUnsyncedData(List<EntryCheckinResponse.Data> checkinList) {
        List<EntryCheckinResponse> unSyncedResponses = getUnsyncedResponse();

        for (EntryCheckinResponse.Data dataDownload : checkinList) {
            String noTiketDownload = dataDownload.getAttribute().getNoTiket().trim().toLowerCase();
            String platNoDownload = dataDownload.getAttribute().getPlatNo().trim().toLowerCase();
            for (EntryCheckinResponse dataLocal : unSyncedResponses) {
                String noTiketLokal = dataLocal.getData().getAttribute().getNoTiket().trim().toLowerCase();
                String platNoLokal = dataLocal.getData().getAttribute().getPlatNo().trim().toLowerCase();
                if (noTiketDownload.equalsIgnoreCase(noTiketLokal) && platNoDownload.equalsIgnoreCase(platNoLokal)) {
                    int removeStatus = removeByPlatNo(dataLocal.getData().getAttribute().getPlatNo());
                    Log.d("remove status", "" + removeStatus);
                }
            }
        }
    }

    private int removeByPlatNo(String platNo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = EntryCheckinResponse.Table.COL_PLAT_NO + " = ? ";
        String[] args = new String[]{platNo};

        return db.delete(EntryCheckinResponse.Table.TABLE_NAME,where, args);
    }

    private void removeUploadSuccess(List<EntryCheckinResponse.Data> checkinList) {
        String sql = EntryCheckinResponse.Table.COL_IS_UPLOADED + " = ?";
        String argsValue = String.valueOf(EntryCheckinResponse.FLAG_UPLOAD_SUCCESS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null,sql,new String[]{argsValue},null,null,null);

        if (c.moveToFirst()) {
            do {
                String noTicket = c.getString(c.getColumnIndex(EntryCheckinResponse.Table.COL_NO_TRANS));
                for (EntryCheckinResponse.Data data : checkinList) {
                    if (noTicket != null && data.getAttribute().getNoTiket() != null) {
                        if (noTicket.equalsIgnoreCase(data.getAttribute().getNoTiket()));
                        removeUploadSuccessByTicketNo(noTicket);
                    }
                }

            }while (c.moveToNext());
        }
    }


    public int removeUploadSuccess(){
        String sql = EntryCheckinResponse.Table.COL_IS_UPLOADED + " = " + EntryCheckinResponse.FLAG_UPLOAD_SUCCESS;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(EntryCheckinResponse.Table.TABLE_NAME, sql, new String[] {});
    }

    private int removeUploadSuccessByTicketNo(String noTicket) {
        String sql = EntryCheckinResponse.Table.COL_IS_UPLOADED + " = ? AND " +  EntryCheckinResponse.Table.COL_NO_TRANS + " = ?";
        String flagUploadSuccess = String.valueOf(EntryCheckinResponse.FLAG_UPLOAD_SUCCESS);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(EntryCheckinResponse.Table.TABLE_NAME, sql, new String[]{flagUploadSuccess, noTicket});
    }

    public int getRemoteVthdIdByFakeId(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};
        int remoteId = 0;
        Cursor c = db.rawQuery("SELECT " + EntryCheckinResponse.Table.COL_REMOTE_VTHD_ID + " FROM " + EntryCheckinResponse.Table.TABLE_NAME
        + " WHERE " + EntryCheckinResponse.Table.COL_RESPONSE_ID + " = ? ", args);

        if (c.moveToFirst()) {
            remoteId = c.getInt(0);
        }

        return remoteId;
    }


    public String getTicketSequence(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};
        String ticketSeq = null;
        Cursor c = db.rawQuery("SELECT " + EntryCheckinResponse.Table.COL_TICKET_SEQUENCE + " FROM " + EntryCheckinResponse.Table.TABLE_NAME
                + " WHERE " + EntryCheckinResponse.Table.COL_REMOTE_VTHD_ID + " = ?", args);

        if (c.moveToFirst()) {
            ticketSeq = c.getString(0);
        }

        return ticketSeq;
    }

    public List<EntryCheckinResponse> fetchAllCheckinResponse() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_IS_CHECKOUT + "=? AND " + EntryCheckinResponse.Table.COL_IS_CALLED + "=0",new String[]{"0"},null,null,EntryCheckinResponse.Table.COL_RESPONSE_ID + " DESC");
        //Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_IS_CHECKOUT + " = ? AND " + EntryCheckinResponse.Table.COL_IS_READY_CHECKOUT + " = ?",new String[]{"0","0"},null,null,EntryCheckinResponse.Table.COL_ID + " DESC");
        Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_IS_CHECKOUT + " = ?",new String[]{"0"},null,null,EntryCheckinResponse.Table.COL_ID + " DESC");

        List<EntryCheckinResponse> responseList = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                String jsonResponse = c.getString(c.getColumnIndex(EntryCheckinResponse.Table.COL_JSON_RESPONSE));
                EntryCheckinResponse checkinResponse = gson.fromJson(jsonResponse, EntryCheckinResponse.class);
                responseList.add(checkinResponse);
            }while (c.moveToNext());
        }

        Collections.sort(responseList, new CheckinComparator()); // sorting based on checkintime descending
        return responseList;
    }

    private List<EntryCheckinResponse> getUnsyncedResponse() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(EntryCheckinResponse.FLAG_UPLOAD_SUCCESS)};
        Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_IS_UPLOADED + " != ?",args,null,null,EntryCheckinResponse.Table.COL_ID + " DESC");
        List<EntryCheckinResponse> responseList = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                String jsonResponse = c.getString(c.getColumnIndex(EntryCheckinResponse.Table.COL_JSON_RESPONSE));
                EntryCheckinResponse checkinResponse = gson.fromJson(jsonResponse, EntryCheckinResponse.class);
                responseList.add(checkinResponse);
            }while (c.moveToNext());
        }



        return responseList;
    }

    public Cursor getAllParkedCars() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_IS_CHECKOUT + "= ? AND " + EntryCheckinResponse.Table.COL_IS_READY_CHECKOUT + " = ?",new String[]{"0", "0"},null,null,EntryCheckinResponse.Table.COL_RESPONSE_ID + " DESC");
    }

    public Cursor getParkedCarsByPlatNo(String platNo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {platNo};
        return db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_PLAT_NO + " LIKE '?' AND "
                 + EntryCheckinResponse.Table.COL_IS_CHECKOUT + "= 0 AND "
                + EntryCheckinResponse.Table.COL_IS_READY_CHECKOUT + "=0", args,null,null,EntryCheckinResponse.Table.COL_RESPONSE_ID + " DESC");
    }

    public boolean isAlreadyCheckedIn(String platNo) {

        Cursor c = getAllParkedCars();

        if (Character.isDigit(platNo.trim().charAt(0))) {
            platNo = "B" + platNo;
        }

        String plat = platNo.replace(" ", "");

        if (c.moveToFirst()) {
            do {
                String existingPlatNo = c.getString(c.getColumnIndex(EntryCheckinResponse.Table.COL_PLAT_NO));
                String trimmedPlat = existingPlatNo.replace(" ","");
                if (plat.equalsIgnoreCase(trimmedPlat)) {
                    c.close();
                    return true;
                }
            }while (c.moveToNext());
        }
        c.close();

        return false;
    }

    public int removeAllCheckinList() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(EntryCheckinResponse.Table.TABLE_NAME, null, new String[]{});

    }

    public EntryCheckinResponse getEntryByIdResponse(int id) {
        EntryCheckinResponse checkinResponse = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};
        // Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_RESPONSE_ID + "=? AND " + EntryCheckinResponse.Table.COL_IS_CHECKOUT + " = 0",args,null,null,null);
        Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_RESPONSE_ID + "=?",args,null,null,null);

        if (c.moveToFirst()) {
            do {
                String jsonResponse = c.getString(c.getColumnIndex(EntryCheckinResponse.Table.COL_JSON_RESPONSE));
                checkinResponse = gson.fromJson(jsonResponse, EntryCheckinResponse.class);

            }while (c.moveToNext());
        }

        return checkinResponse;
    }

    public EntryCheckinResponse getEntryByRemoteId(int id) {
        EntryCheckinResponse checkinResponse = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};
        // Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_RESPONSE_ID + "=? AND " + EntryCheckinResponse.Table.COL_IS_CHECKOUT + " = 0",args,null,null,null);
        Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_REMOTE_VTHD_ID + "=?",args,null,null,null);


        if (c.moveToFirst()) {
            do {
                String jsonResponse = c.getString(c.getColumnIndex(EntryCheckinResponse.Table.COL_JSON_RESPONSE));
                checkinResponse = gson.fromJson(jsonResponse, EntryCheckinResponse.class);

            }while (c.moveToNext());
        }

        return checkinResponse;
    }

    public boolean isSynced(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id), String.valueOf(EntryCheckinResponse.FLAG_UPLOAD_SUCCESS)};
        Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null,EntryCheckinResponse.Table.COL_RESPONSE_ID +" = ? AND " + EntryCheckinResponse.Table.COL_IS_UPLOADED + " = ?",args,null,null,null);

        if (c.moveToFirst()) {
            return true;
        }
        return false;
    }

    public int removeEntryById(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};
        return db.delete(EntryCheckinResponse.Table.TABLE_NAME, EntryCheckinResponse.Table.COL_RESPONSE_ID + "=?", args);
    }

    public static class Table {
        public static final String TABLE_NAME = "entry_json";

        public static final String COL_ID = "id";
        public static final String COL_ID_CHECKIN = "id_checkin";
        public static final String COL_JSON_ENTRY = "json_entry";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY, " +
                COL_ID_CHECKIN + " INTEGER, " +
                COL_JSON_ENTRY + " TEXT)";
    }

    public void deleteUncheckedOutEntry() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + EntryCheckinResponse.Table.TABLE_NAME + " WHERE " + EntryCheckinResponse.Table.COL_IS_CHECKOUT + " != 0");
    }

    public String getPlatNoByTicketNo(String ticketNo) {
        List<EntryCheckinResponse> checkedOutCars = fetchAllCheckedoutCars();
        Iterator<EntryCheckinResponse> i = checkedOutCars.iterator();
        String platNo = "";

        while (i.hasNext()) {
            EntryCheckinResponse e = i.next();
            String checkinTiket = e.getData().getAttribute().getNoTiket();
            if (TextUtils.equals(checkinTiket, ticketNo)) {
                platNo = e.getData().getAttribute().getPlatNo();
                break;
            }
        }
        return platNo;
    }

    public List<EntryCheckinResponse> fetchAllCheckedoutCars() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_IS_CHECKOUT + "=? AND " + EntryCheckinResponse.Table.COL_IS_CALLED + "=0",new String[]{"0"},null,null,EntryCheckinResponse.Table.COL_RESPONSE_ID + " DESC");
        Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_IS_CHECKOUT + " != ? ",new String[]{"0"},null,null,EntryCheckinResponse.Table.COL_ID + " DESC");
        List<EntryCheckinResponse> responseList = new ArrayList<>();

        if (c.moveToFirst()) {
            do {
                String jsonResponse = c.getString(c.getColumnIndex(EntryCheckinResponse.Table.COL_JSON_RESPONSE));
                EntryCheckinResponse checkinResponse = gson.fromJson(jsonResponse, EntryCheckinResponse.class);
                responseList.add(checkinResponse);
            }while (c.moveToNext());
        }

        Collections.sort(responseList, new CheckinComparator()); // sorting based on checkintime descending
        return responseList;
    }

    public void setCheckoutCar(List<ClosingData.Data> dataCheckouts){
        List<EntryCheckinResponse> checkins = fetchAllCheckinResponse();

        if (!checkins.isEmpty()) {
            Iterator<ClosingData.Data> iCheckouts = dataCheckouts.iterator();
            Iterator<EntryCheckinResponse> iCheckins = checkins.iterator();

            ClosingData.Data checkout;
            String checkoutTicket;
            EntryCheckinResponse checkin;
            String checkinTicket;

            while (iCheckouts.hasNext()) {
                //ClosingData.Data checkout = iCheckouts.next();
                checkout = iCheckouts.next();
                //String checkoutTicket = checkout.getAttributes().getNoTiket().replace(" ","");
                checkoutTicket = checkout.getAttributes().getNoTiket().replace(" ","");
                while (iCheckins.hasNext()) {
                    //EntryCheckinResponse checkin = iCheckins.next();
                    checkin = iCheckins.next();
                    //String checkinTicket = checkin.getData().getAttribute().getNoTiket().replace(" ","");
                    checkinTicket = checkin.getData().getAttribute().getNoTiket().replace(" ","");
                    if (checkinTicket.equalsIgnoreCase(checkoutTicket)) {
                        //String platNo = checkin.getData().getAttribute().getPlatNo();
                        String platNo = checkin.getData().getAttribute().getPlatNo();
                        int vthdId = checkin.getData().getAttribute().getId();
                        setCheckout(vthdId);
                        LoggingUtils.getInstance(dbHelper.getContext()).logCheckoutFromAnotherLobby(checkinTicket, platNo);
                    }
                }
                iCheckins = checkins.iterator();
            }
        }
    }

    private void setCheckout(int vthdId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(vthdId)};
        ContentValues cv = new ContentValues();
        cv.put(EntryCheckinResponse.Table.COL_IS_CHECKOUT, 1);
        int udpatSukes = db.update(EntryCheckinResponse.Table.TABLE_NAME,cv, EntryCheckinResponse.Table.COL_RESPONSE_ID + " = ?", args);
        Log.d("Update to checkout", "update to checkout from download checkout service: " + udpatSukes);
    }

    public List<EntryCheckinResponse.Data> getNewListCheckin(List<EntryCheckinResponse.Data> checkinList) {
        List<EntryCheckinResponse.Data> newCheckinList = checkinList;
        List<EntryCheckinResponse> checkins = fetchAllCheckinResponse();

        if (checkins.size() >= newCheckinList.size()){
            return null;
        }

        Iterator<EntryCheckinResponse.Data> iDownloadedCheckinList = newCheckinList.iterator();
        Iterator<EntryCheckinResponse> iCurrentCheckinList = checkins.iterator();

        while (iDownloadedCheckinList.hasNext()) {
            EntryCheckinResponse.Data downloadedCheckinData = iDownloadedCheckinList.next();
            String downloadedTicket = downloadedCheckinData.getAttribute().getNoTiket().trim();
            while (iCurrentCheckinList.hasNext()) {
                EntryCheckinResponse.Data currentCheckinData = iCurrentCheckinList.next().getData();
                String currentTicket = currentCheckinData.getAttribute().getNoTiket().trim();
                if (downloadedTicket.equalsIgnoreCase(currentTicket)) {
                    iDownloadedCheckinList.remove();
                }
            }

            iCurrentCheckinList = checkins.iterator();

        }

        return newCheckinList;
    }

}
