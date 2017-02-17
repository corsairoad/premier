package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Toast;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import valet.digikom.com.valetparking.CheckoutActivity;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.util.MakeCurrencyString;
import valet.digikom.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class PrintCheckout implements ReceiveListener {

    private Context context;
    String noTrans;
    String noPol;
    String jenis;
    String totalBayar;
    String valetType;
    String feex;
    String warna;
    String site;
    String checkinTime;
    Printer mPrinter;
    PrefManager prefManager;
    FinishCheckOut finishCheckOut;
    int overNight;
    int lostTicket;
    String noVoucher;
    String idMembership;
    MembershipResponse.Data dataMembership;
    private CheckoutActivity checkoutActivity;
    String checkoutTime;

    public PrintCheckout(Context context, String totalBayar, EntryCheckinResponse entryCheckinResponse, FinishCheckOut finishCheckOut,
                         int overNightFine, int lostTicketFine,String noVoucher, MembershipResponse.Data dataMembership, String idMembersip, String checkoutTime) {
        this.context = context;
        checkoutActivity = (CheckoutActivity) context;
        this.totalBayar = totalBayar;
        this.noTrans = entryCheckinResponse.getData().getAttribute().getIdTransaksi();
        this.noPol = entryCheckinResponse.getData().getAttribute().getPlatNo();
        this.jenis = entryCheckinResponse.getData().getAttribute().getCar();
        this.valetType = entryCheckinResponse.getData().getAttribute().getValetType();
        this.feex = MakeCurrencyString.fromInt(entryCheckinResponse.getData().getAttribute().getFee());
        this.warna = entryCheckinResponse.getData().getAttribute().getColor();
        this.site = entryCheckinResponse.getData().getAttribute().getSiteName();
        this.checkinTime = entryCheckinResponse.getData().getAttribute().getCheckinTime();
        this.overNight = overNightFine;
        this.lostTicket = lostTicketFine;
        this.noVoucher = noVoucher;
        this.dataMembership = dataMembership;
        this.finishCheckOut = finishCheckOut;
        this.idMembership = idMembersip;
        this.checkoutTime = checkoutTime;

        prefManager = PrefManager.getInstance(context);
    }

    public void print() {
        if (!initializeObject()) {
            return;
        }

        if (!createPrintCheckout()) {
            return;
        }

        if (!printData()) {
            finalizeObject();
            return;
        }

    }

    private boolean createPrintCheckout(){
        if (mPrinter == null) {
            return false;
        }

        try {

            Bitmap logoData = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_1);
            StringBuilder sb = new StringBuilder();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);
            mPrinter.addFeedLine(1);

            mPrinter.addTextSize(1,1);
            mPrinter.addText(site);
            mPrinter.addFeedLine(2);
            mPrinter.addTextSize(2,2);
            mPrinter.addText("RECEIPT");
            mPrinter.addFeedLine(2);
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextSize(1,1);
            sb.append(" No. Plat      :  " + noPol + "\n");
            sb.append(" Valet         :  " + valetType + "\n");
            sb.append(" No. Transaksi :  " + noTrans + "\n");
            sb.append(" Jenis Mobil   :  " + jenis + "\n");
            if (warna != null) {
                sb.append(" Warna         :  " + warna + "\n");
            }
            sb.append(" Checkin       :  " + checkinTime + "\n");
            sb.append(" Checkout      :  " + checkoutTime + "\n");
            sb.append(" Fee           :  " + feex + "\n");

            if (lostTicket > 0) {
                sb.append(" Tiket Hilang  :  " + MakeCurrencyString.fromInt(lostTicket) + "\n");
            }
            if (overNight > 0) {
                sb.append(" Mobil Menginap:  " + MakeCurrencyString.fromInt(overNight) + "\n");
            }

            if (!TextUtils.isEmpty(noVoucher)) {
                sb.append(" No Voucher    :  " + noVoucher + "\n");
            }

            if (dataMembership != null) {
                sb.append(" Membership    :  " + dataMembership.getAttr().getName() + "\n");
                sb.append(" Id Membership :  " + idMembership + "\n");
                int price = Integer.valueOf(dataMembership.getAttr().getPrice());
                sb.append(" Discount Mmbr :  " + MakeCurrencyString.fromInt(price) + "\n");
            }

            sb.append("----------------------------------------\n");
            mPrinter.addText(sb.toString());
            sb.delete(0, sb.length());

            mPrinter.addFeedLine(1);
            mPrinter.addTextSize(2,2);
            sb.append("Total:" + totalBayar);
            mPrinter.addText(sb.toString());
            mPrinter.addFeedLine(1);
            mPrinter.addCut(Printer.CUT_FEED);

        } catch (Epos2Exception e) {
            e.printStackTrace();
        }

        return true;
    }



    private String getCurrentTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    private boolean printData() {
        if (mPrinter == null) {
            return false;
        }

        if (!connectPrinter()) {
            return false;
        }

        PrinterStatusInfo status = mPrinter.getStatus();
        if (isPrintable(status)) {
            try {
                mPrinter.sendData(Printer.PARAM_DEFAULT);
            } catch (Epos2Exception e) {
                try {
                    mPrinter.disconnect();
                }
                catch (Exception ex) {
                    // Do nothing
                }
                return false;
            }
        } else {
            try {
                mPrinter.disconnect();
            }
            catch (Exception ex) {
                // Do nothing
            }
            return false;
        }
        return true;
    }

    private boolean isPrintable(PrinterStatusInfo status) {
        if (status == null) {
            return false;
        }

        if (status.getConnection() == Printer.FALSE) {
            return false;
        }
        else if (status.getOnline() == Printer.FALSE) {
            return false;
        }
        else {
            ;//print available
        }

        return true;
    }

    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_T88,Printer.LANG_EN,context);
        }
        catch (Exception e) {
            //ShowMsg.showException(e, "Printer", context);
            return false;
        }

        mPrinter.setReceiveEventListener(this);

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
            //ShowMsg.showException(e, "connect", context);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
        } catch (Exception e) {
            //ShowMsg.showException(e, "beginTransaction", context);
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

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        checkoutActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (code == Epos2CallbackCode.CODE_SUCCESS) {
                    Toast.makeText(context, "Print success", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Print failed", Toast.LENGTH_SHORT).show();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        disconnectPrinter();
                    }
                }).start();
            }
        });
    }
}
