package valet.digikom.com.valetparking.domain;

import android.content.Context;

import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;

import valet.digikom.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX-99 on 2/26/2017.
 */

public abstract class PrintReceipt implements StatusChangeEventListener {

    private static final String PRINTER_NAME = "TM-T88V";
    private static final int PRINTER_LANGUAGE = com.epson.eposprint.Builder.LANG_EN;
    private static final int SEND_TIMEOUT = 10 * 1000;

    private Print printer;
    private Context context;
    private PrefManager prefManager;
    private static Builder mBuilder;

    public PrintReceipt(Context context) {
        this.context = context;
        prefManager = prefManager.getInstance(context);
    }

    private boolean openPrinter() {
        String printerTarget = prefManager.getPrinterMacAddress();
        printer = new Print(context);
        printer.setStatusChangeEventCallback(this);
        try {
            printer.openPrinter(Print.DEVTYPE_TCP, printerTarget,Print.TRUE,Print.PARAM_DEFAULT);
        }catch (Exception e) {
            ShowMsg.showException(e,"OPEN_PRINTER", context);
            return false;
        }
        return true;
    }

    @Override
    public void onStatusChangeEvent(String s, int i) {

    }

    public Context getContext() {
        return context;
    }

    public Builder getBuilder() throws EposException {
        if (mBuilder == null) {
            mBuilder = new Builder(PRINTER_NAME, PRINTER_LANGUAGE, context);
        }
        return mBuilder;
    }

    public void print() {

        if (!openPrinter()) {
            return;
        }

        if (mBuilder == null) {
            return;
        }

        int[] status = new int[1];
        int[] battery = new int[1];

        try {
            printer.sendData(mBuilder, SEND_TIMEOUT, status, battery);
            ShowMsg.showStatus(EposException.SUCCESS, status[0], battery[0], context);
        } catch (EposException e) {
            e.printStackTrace();
            ShowMsg.showStatus(e.getErrorStatus(), e.getPrinterStatus(), e.getBatteryStatus(), context);
        }

        //remove builder
        if(mBuilder != null){
            try{
                mBuilder.clearCommandBuffer();
                mBuilder = null;
            }catch(Exception e){
                mBuilder = null;
            }
        }

        closePrinter();
    }

    private void closePrinter() {
        if(printer != null){
            try{
                printer.closePrinter();
                printer = null;
            }catch(Exception e){
                printer = null;
            }
        }
    }

    public abstract void buildPrintData();
}
