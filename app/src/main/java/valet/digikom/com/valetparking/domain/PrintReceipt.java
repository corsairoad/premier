package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

import com.epson.eposdevice.printer.Printer;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;
import com.epson.eposprint.StatusChangeEventListener;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX-99 on 2/26/2017.
 */

public abstract class PrintReceipt implements StatusChangeEventListener {

    private static final String PRINTER_NAME = "TM-T88V";
    private static final int PRINTER_LANGUAGE = com.epson.eposprint.Builder.LANG_EN;
    private static final int SEND_TIMEOUT = 10 * 1000;

    private static Print printer;
    private Context context;
    private PrefManager prefManager;
    private static Builder mBuilder;
    private Bitmap logoData;
    private Bitmap logoExclusive;

    public PrintReceipt(Context context) {
        this.context = context;
        prefManager = prefManager.getInstance(context);
        logoData = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_1);
        logoExclusive = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_exclusive);
        printer = new Print(context.getApplicationContext());
    }

    private boolean openPrinter() {
        try {
            String printerTarget = prefManager.getPrinterMacAddress();

            printer = new Print(context.getApplicationContext());
            printer.setStatusChangeEventCallback(this);
            printer.openPrinter(Print.DEVTYPE_TCP, printerTarget,Print.TRUE, Print.PARAM_DEFAULT);

        }catch (EposException e) {
            //ShowMsg.showException(e,"OPEN_PRINTER", context.getApplicationContext());
            try {
                printer.closePrinter();
            } catch (EposException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            Log.e("open printer", "error status " + ShowMsg.getEposExceptionText(e.getErrorStatus()));
            Log.e("open printer", "printer status " + e.getPrinterStatus());
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

    public Bitmap getLogoExclusive() {
        return logoExclusive;
    }

    public Bitmap getLogoData() {
        return logoData;
    }

    public void setLogoData(Bitmap logoData) {
        this.logoData = logoData;
    }

    public Builder getBuilder() throws EposException {
        if (mBuilder == null) {
            mBuilder = new Builder(PRINTER_NAME, PRINTER_LANGUAGE, context.getApplicationContext());
        }
        mBuilder.addTextFont(Builder.FONT_B);
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

            // two copies receipt if it checkout
            if (this instanceof PrintReceiptCheckout){
                printer.sendData(mBuilder, SEND_TIMEOUT, status, battery);
            }
            //ShowMsg.showStatus(EposException.SUCCESS, status[0], battery[0], context);
        } catch (EposException e) {
            e.printStackTrace();
            //ShowMsg.showStatus(e.getErrorStatus(), e.getPrinterStatus(), e.getBatteryStatus(), context);
        }

        closePrinter();
    }

    public void closePrinter() {
        if(printer != null){
            try{
                //remove builder
                if(mBuilder != null){
                    try{
                        mBuilder.clearCommandBuffer();
                        mBuilder = null;
                    }catch(Exception e){
                        mBuilder = null;
                    }
                }
                printer.closePrinter();
                printer = null;
            }catch(Exception e){
                printer = null;
            }
        }
    }

    //abstract method
    public abstract void buildPrintData();


    public Bitmap scaleBitmap(Bitmap b, int x, int y) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, x, y), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
    }


    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
    /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/

        return cs;
    }

    public Bitmap createBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }

    public void addLogo(Builder builder, Bitmap logoData) throws EposException {
        builder.addTextAlign(Builder.ALIGN_CENTER);
        builder.addImage(logoData, 0, 0,
                logoData.getWidth(),
                logoData.getHeight(),
                Builder.COLOR_1,
                Builder.MODE_MONO,
                Builder.HALFTONE_DITHER,
                Builder.PARAM_DEFAULT);
        builder.addFeedLine(1);
    }
}
