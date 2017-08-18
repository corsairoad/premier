package valet.intan.com.valetparking.util;

import android.content.Context;

import valet.intan.com.valetparking.dao.EntryCheckinContainerDao;
import valet.intan.com.valetparking.dao.EntryDao;
import valet.intan.com.valetparking.dao.FinishCheckoutDao;

/**
 * Created by DIGIKOM-EX4 on 4/5/2017.
 */

public class GarbageManagement {
    private Context context;
    private GarbageManagement garbageManagement;
    EntryDao entryDao;
    EntryCheckinContainerDao checkinContainerDao;
    FinishCheckoutDao finishCheckoutDao;

    private GarbageManagement(Context context) {
        this.context = context;
        entryDao = EntryDao.getInstance(context);
        checkinContainerDao = EntryCheckinContainerDao.getInstance(context);
        finishCheckoutDao = FinishCheckoutDao.getInstance(context);
    }

    public GarbageManagement getInstance(Context context) {
        if (garbageManagement == null) {
            garbageManagement = new GarbageManagement(context);
        }
        return garbageManagement;
    }

    public void clearCheckinCheckoutData() {

    }
}
