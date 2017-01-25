package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;

import java.text.SimpleDateFormat;
import java.util.Date;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class PrintCheckout {

    private Context context;
    private String noTransaksi;
    private String noPolisi;
    private String jenis;
    private String fee;
    private String lostTicket;
    private String overnight;
    private String totalBayar;

    private String warna;
    private Printer mPrinter;
    PrefManager prefManager;

    public PrintCheckout(Context context, String noTransaksi, String noPolisi, String jenis, String totalBayar, String warna, String fee,String lostTicket, String overnight) {
        this.noTransaksi = noTransaksi;
        this.noPolisi = noPolisi;
        this.jenis = jenis;
        this.totalBayar = totalBayar;
        this.warna = warna;
        this.context = context;
        this.fee = fee;
        this.lostTicket = lostTicket;
        this.overnight = overnight;

        prefManager = PrefManager.getInstance(context);
        initializeObject();
    }

    public void print() {
        try {

            Bitmap logoData = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_1);
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            StringBuilder sb = new StringBuilder();

            //---------------------------------------
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(1,1);
            mPrinter.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);

            mPrinter.addFeedLine(2);

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addText(date + "           ");


            mPrinter.addFeedLine(2);

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(2, 2);
            mPrinter.addText("CHECKOUT RECEIPT");

            mPrinter.addFeedLine(3);

            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            sb.append(" No. Transaksi :    " + noTransaksi + "\n");
            sb.append(" No. Plat      :    " + noPolisi + "\n");
            sb.append(" Jenis Mobil   :    " + jenis + "\n");
            sb.append(" Warna         :    " + warna + "\n");
            sb.append(" Fee           :    " + fee + "\n");
            sb.append(" Tiket Hilang  :    " + lostTicket + "\n");
            sb.append(" Mobil Menginap:    " + overnight + "\n");
            sb.append("--------------------------------\n");

            mPrinter.addTextSize(1,1);
            mPrinter.addText(sb.toString());

            mPrinter.addFeedLine(1);

            sb.delete(0, sb.length());

            mPrinter.addText(sb.toString());

            sb.append("Total: " + totalBayar);

            mPrinter.addTextSize(2,2);

            mPrinter.addText(sb.toString());
            mPrinter.addFeedLine(3);
            mPrinter.addCut(Printer.CUT_FEED);

            //----------------------------
            connectPrinter();

        } catch (Epos2Exception e) {
            e.printStackTrace();
        }
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_T88,Printer.LANG_EN,context);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", context);
            return false;
        }
        return true;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;
        String target = prefManager.getPrinterMacAddress();
        if (mPrinter == null) {
            return false;
        }

        if (target == null) {
            return false;
        }
        try {
            mPrinter.connect(target,Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", context);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
            mPrinter.sendData(Printer.PARAM_DEFAULT);
            disconnectPrinter();
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", context);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {

        }

        try {
            mPrinter.disconnect();
        }
        catch (final Exception e) {

        }

        finalizeObject();
    }

}
