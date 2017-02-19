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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import valet.digikom.com.valetparking.ClosingActivity;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.util.MakeCurrencyString;
import valet.digikom.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX4 on 2/15/2017.
 */

public class PrintClosing implements ReceiveListener {

    private Context context;
    private Printer mPrinter;
    private PrefManager prefManager;
    private List<ClosingData.Data> closingData;
    private ClosingActivity closingActivity;
    private String lobbyName;
    private String siteName;
    private String dateFrom;
    private String dateTo;
    private String adminName;
    private int numRegular;
    private int numExclusive;
    private int total;

    public PrintClosing(Context context, List<ClosingData.Data> closingData, String lobbyName, String siteName, String dateFrom, String dateTo, String adminName, int numRegular, int numExclusive, int total) {
        this.context = context;
        this.closingData = closingData;
        prefManager = PrefManager.getInstance(context);
        closingActivity = (ClosingActivity) context;
        this.lobbyName = lobbyName;
        this.siteName = siteName;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.adminName = adminName;
        this.numRegular = numRegular;
        this.numExclusive = numExclusive;
        this.total = total;

        initializeObject();
    }

    public boolean print() {
        if (!initializeObject()) {
            return false;
        }

        if (!buildPrintClosing()) {
            return false;
        }

        if (!prindData()) {
            finalizeObject();
            return false;
        }
        return true;
    }

    private boolean buildPrintClosing() {
        if (mPrinter == null) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        Bitmap logoData = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_1);

        try {
            /*
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
            */
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(2,2);
            mPrinter.addText("REPORT");
            mPrinter.addFeedLine(2);

            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addTextSize(1,1);

            sb.append("LOKASI      : " + lobbyName + "\n");
            //sb.append("LOKASI      : " + siteName + "\n");
            //sb.append("ADMIN       : " + adminName + "\n");
            //sb.append("WAKTU REPORT: " + dateFrom + "\n");
            sb.append("WAKTU REPORT: " + getCurrentDate() + "\n");
            //sb.append("DATE TO   : " + dateTo  + "\n");
            //sb.append("PRINT DATE: " + getCurrentDate() + "\n");
            sb.append("------------------------------------------\n");
            sb.append("#  No. Tiket\tIn\t\tOut\n");
            sb.append("------------------------------------------\n");
            mPrinter.addText(sb.toString());
            sb.delete(0, sb.length());

            int i = 1;
            int totalCheckin = 0;
            int totalCheckout = 0;
            int totalOverNight = 0;
            int totalVoid = 0;
            int lostTicket = 0;
            BigDecimal sumCash = new BigDecimal(0);
            BigDecimal sumIncome = new BigDecimal(0);

            for (ClosingData.Data data : closingData) {
                String transactNo = data.getAttributes().getTransactionId();
                //String valetType = data.getAttributes().getValetTypeName();

                int valetTotal = data.getAttributes().getValetTotalFee();
                int valetFee = data.getAttributes().getValetFee();
                int valetFineFee = data.getAttributes().getValetFineFee();

                sumCash = sumCash.add(new BigDecimal(valetTotal));
                sumIncome = sumIncome.add(new BigDecimal(valetFee + valetFineFee));

                int lost = data.getAttributes().getValetFineFee();
                if (lost != 0) {
                    lostTicket +=1;
                }

                String checkinTime = data.getAttributes().getCheckIn();
                if (checkinTime != null) {
                    // count total checkin
                    checkinTime = convertDateString(checkinTime);
                    totalCheckin+=1;
                }else {
                    // count total void
                    checkinTime = "Cancel";
                    totalVoid +=1;
                }
                String checkoutTime = data.getAttributes().getCheckout();
                if (checkoutTime != null) {
                    //count total checkout
                    checkoutTime = convertDateString(checkoutTime);
                    totalCheckout+=1;
                }else {
                    // count total overnight
                    checkoutTime = "";
                    if (!checkinTime.toLowerCase().equals("cancel")) {
                        totalOverNight += 1;
                    }
                }

                sb.append(i + " " +  transactNo + "\t"  +  checkinTime + " " + checkoutTime + "\n");
                i++;
            }
            sb.append("------------------------------------------\n");
            mPrinter.addText(sb.toString());
            sb.delete(0, sb.length());

            sb.append("Total Qty In       : " + totalCheckin + "\n");
            sb.append("Total Qty Out      : " + totalCheckout + "\n");
            sb.append("Total Qty Overnight: " + totalOverNight + "\n");
            sb.append("Total Tiket Batal  : " + totalVoid + "\n");
            sb.append("Total Tiket Hilang : " + lostTicket + "\n");
            sb.append("Cash               : " + MakeCurrencyString.fromInt(sumCash.intValue()) + "\n");
            sb.append("Income             : " + MakeCurrencyString.fromInt(sumIncome.intValue()) + "\n");
            mPrinter.addText(sb.toString());
            mPrinter.addFeedLine(1);
            mPrinter.addCut(Printer.CUT_FEED);
        } catch (Epos2Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean prindData() {
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
            mPrinter = PrintManager.getPrinterInstance(context);
        }
        catch (Exception e) {
            //ShowMsg.showException(e, "Printer", context);
            return false;
        }

        mPrinter.setReceiveEventListener(this);

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

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {
        closingActivity.runOnUiThread(new Runnable() {
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

    private String getCurrentDate() {
        SimpleDateFormat sdf  = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        return sdf.format(new Date());
    }

    private String convertDateString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse(dateString);
            sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }
}
