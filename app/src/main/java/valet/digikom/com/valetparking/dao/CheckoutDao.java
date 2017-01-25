package valet.digikom.com.valetparking.dao;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.CheckoutActivity;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by DIGIKOM-EX4 on 1/23/2017.
 */

public class CheckoutDao implements ProcessRequest {

    private Context context;
    private ValetDbHelper dbHelper;
    private static CheckoutDao checkoutDao;
    private static OnCarReadyListener listener;
    private static OnCarReadyListener listenerOnParkedCark;

    private CheckoutDao(Context context) {
        this.context = context;
        this.dbHelper = new ValetDbHelper(context);
    }


    public static CheckoutDao getInstance(Context context, Fragment fragment, Fragment fragmentParkedCar) {
        if (checkoutDao == null) {
            checkoutDao = new CheckoutDao(context);
        }

        if (fragment != null) {
            listener = (OnCarReadyListener) fragment;
        }
        if (fragmentParkedCar != null) {
            listenerOnParkedCark = (OnCarReadyListener) fragmentParkedCar;
        }

        return checkoutDao;
    }

    private void downloadCheckouts(String token) {
        ApiEndpoint endpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<EntryCheckoutCont> call = endpoint.getCheckouts(50);
        call.enqueue(new Callback<EntryCheckoutCont>() {
            @Override
            public void onResponse(Call<EntryCheckoutCont> call, Response<EntryCheckoutCont> response) {
                if (response != null && response.body() != null) {
                    EntryCheckoutCont checkoutCont = response.body();
                    new FindCheckoutCar().execute(checkoutCont);
                    insertEntryCheckout(checkoutCont);
                }
            }

            @Override
            public void onFailure(Call<EntryCheckoutCont> call, Throwable t) {

            }
        });
    }

    private void insertEntryCheckout(EntryCheckoutCont checkoutCont) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + EntryCheckoutCont.Table.TABLE_NAME);
        Gson gson = new Gson();
        List<EntryCheckoutCont.EntryChekout> entryChekoutList = checkoutCont.getChekoutList();

        for (EntryCheckoutCont.EntryChekout e : entryChekoutList) {
            String jsonCheckout = gson.toJson(e);
            ContentValues cv = new ContentValues();
            cv.put(EntryCheckoutCont.Table.COL_RESPONSE_ID, String.valueOf(e.getAttrib().getId()));
            cv.put(EntryCheckoutCont.Table.COL_JSON_ENTRY_CHECKOUT, jsonCheckout);

            db.insert(EntryCheckoutCont.Table.TABLE_NAME,null, cv);
        }
    }

    public EntryCheckoutCont.EntryChekout getEntryCheckoutById(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};
        Cursor c = db.query(EntryCheckoutCont.Table.TABLE_NAME,null, EntryCheckoutCont.Table.COL_RESPONSE_ID + "=?", args,null, null, null);
        EntryCheckoutCont.EntryChekout entryChekout = null;

        if (c.moveToFirst()) {
            Gson gson = new Gson();
            String jsonCheckout = c.getString(c.getColumnIndex(EntryCheckoutCont.Table.COL_JSON_ENTRY_CHECKOUT));
            entryChekout = gson.fromJson(jsonCheckout,EntryCheckoutCont.EntryChekout.class);
        }
        c.close();
        return entryChekout;
    }

    @Override
    public void process(String token) {
        downloadCheckouts(token);
    }

    private class FindCheckoutCar extends AsyncTask<EntryCheckoutCont, Void, List<EntryCheckinResponse>> {

        @Override
        protected List<EntryCheckinResponse> doInBackground(EntryCheckoutCont... entryCheckoutConts) {
            CallDao callDao = CallDao.getInstance(context);
            List<EntryCheckinResponse> calledCars = callDao.fetchAllCalledCarsNotReady();
            List<EntryCheckoutCont.EntryChekout> responseCheckouts = entryCheckoutConts[0].getChekoutList();
            List<EntryCheckinResponse> readyCheckouts = new ArrayList<>();
            for (EntryCheckinResponse e : calledCars) {
                for (EntryCheckoutCont.EntryChekout c : responseCheckouts) {
                    if (c.getAttrib().getId() == e.getData().getAttribute().getId()) {
                        e.setReadyToCheckout(true);
                        readyCheckouts.add(e);
                        callDao.setCheckoutReady(e.getData().getAttribute().getId(), CallDao.FLAG_READY);
                        break;
                    }
                }
            }
            return readyCheckouts;
        }

        @Override
        protected void onPostExecute(List<EntryCheckinResponse> responseList) {
            if (!responseList.isEmpty()){
                notifyApp(responseList);
            }
        }
    }

    private void notifyApp(List<EntryCheckinResponse> responseList) {

        listener.onCheckoutReady();
        listenerOnParkedCark.onCheckoutReady();

        for (EntryCheckinResponse e : responseList) {
            notify(e);
        }
    }

    public interface OnCarReadyListener {
        void onCheckoutReady();
    }

    private void notify(EntryCheckinResponse response) {
        EntryCheckinResponse.Attribute attribute = response.getData().getAttribute();
        String platNo = attribute.getPlatNo();
        String noTrans = attribute.getIdTransaksi();

        String contentTitle = "Ready to Checkout";
        String contentText = platNo+ " - " + noTrans ;

        String strtitle = "nTitle";
        // Set Notification Text
        String strtext = "nText";

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(context, CheckoutActivity.class);
        intent.putExtra(EntryCheckoutCont.KEY_ENTRY_CHECKOUT, attribute.getId());
        // Send data to NotificationView Class
        intent.putExtra("title", strtitle);
        intent.putExtra("text", strtext);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // notif sound
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(R.mipmap.logo_1)
                // Set Ticker Message
                .setTicker("sticker message")
                // Set Title
                .setContentTitle(contentTitle)
                // Set Text
                .setContentText(contentText)
                // Add an Action Button below Notification
                .addAction(R.drawable.ic_call, "Checkout", pIntent)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                .setSound(uri)
                // Dismiss Notification
                .setAutoCancel(true);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());
    }

}
