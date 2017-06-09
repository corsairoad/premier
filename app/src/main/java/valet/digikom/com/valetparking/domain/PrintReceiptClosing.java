package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.widget.Toast;

import com.epson.epos2.printer.Printer;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import valet.digikom.com.valetparking.ClosingActivity;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.util.MakeCurrencyString;

/**
 * Created by DIGIKOM-EX4 on 2/27/2017.
 */

public class PrintReceiptClosing extends PrintReceipt {

    private PrintClosingParam closingParam;
    private String flagHeader;
    private String printType;
    private int flagPrint;

    public PrintReceiptClosing(Context context,PrintClosingParam closingParam, String flagHeader, int flagPrint) {
        super(context);
        this.closingParam = closingParam;
        setFlagHeader(flagHeader);
        setFlagPrint(flagPrint);
    }

    public void setFlagHeader(String flagHeader) {
        if (flagHeader == null) {
            this.flagHeader = "CLOSING";
        }else {
            this.flagHeader = flagHeader.toUpperCase();
        }
    }

    public void setFlagPrint(int flagPrint) {
        this.flagPrint = flagPrint;
        if (flagPrint == ClosingActivity.PRINT_SUMMARY) {
            this.printType = "SUMMARY";
        }else {
            this.printType = "DETAILS";
        }
    }

    @Override
    public void buildPrintData() throws EposException {

            Toast.makeText(getContext(), "Printing", Toast.LENGTH_SHORT).show();

            Builder builder = getBuilder();

            StringBuilder sb = new StringBuilder();
            StringBuilder sbSample = new StringBuilder();

            builder.addTextAlign(Printer.ALIGN_CENTER);
            builder.addTextSize(2,2);
            builder.addText("REPORT " + this.flagHeader);
            builder.addFeedLine(1);
            builder.addTextSize(1,1);
            builder.addText(printType);
            builder.addFeedLine(2);

            builder.addTextAlign(Printer.ALIGN_LEFT);
            builder.addTextSize(1,1);

            sb.append("LOKASI      : " + closingParam.getSiteName() + "\n");
            if (!flagHeader.contains("SITE")) {
                sb.append("LOBBY       : " + closingParam.getLobbyName() + "\n");
            }
            sb.append("WAKTU REPORT: " + getCurrentDate() + "\n");
            sb.append("--------------------------------------------------------\n");

            sbSample.append(sb.toString());
            builder.addText(sb.toString());

            sb.delete(0, sb.length());


            if (flagPrint == ClosingActivity.PRINT_DETAILS || flagPrint == ClosingActivity.PRINT_CLOSING) {
                sb.append("Tiket        ID.Trans. No.Plat    In    Out    Status\n");
                sb.append("--------------------------------------------------------\n");

                sbSample.append(sb.toString());
                builder.addText(sb.toString());
                sb.delete(0, sb.length());
            }

            int i = 1;
            int totalCheckin = 0;
            int totalCheckout = 0;
            int totalOverNight = 0;
            int totalVoid = 0;
            int lostTicket = 0;
            BigDecimal sumCash = new BigDecimal(0);
            BigDecimal sumIncome = new BigDecimal(0);
            String status = "";

            for (ClosingData.Data data : closingParam.getClosingData()) {
                String transactNo = data.getAttributes().getNoTiket();
                String noPol = data.getAttributes().getPlatNo();
                String remoteId = data.getAttributes().getTransactionId();
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
                String checkoutTime = data.getAttributes().getCheckout();

                if (checkinTime != null && checkoutTime != null) {
                    status = "Checkout";
                    totalCheckin +=1;
                    totalCheckout +=1;
                    checkinTime = convertDateString(checkinTime);
                    checkoutTime = convertDateString(checkoutTime);
                }else if(checkinTime != null && checkoutTime == null) {
                    status = "Parked";
                    totalCheckin += 1;
                    totalOverNight +=1;
                    checkinTime = convertDateString(checkinTime);
                    checkoutTime = "";
                }else if (checkinTime == null && checkoutTime == null) {
                    status = "Canceled";
                    totalVoid +=1;
                    checkinTime = "";
                    checkoutTime = "";
                }

                sb.append(transactNo + "\t   " + remoteId + "\t" + noPol + "\t"  + checkinTime + " " + checkoutTime + "  " + status + "\n");
                i++;
            }

            sb.append("--------------------------------------------------------\n");

            if (flagPrint == ClosingActivity.PRINT_DETAILS || flagPrint == ClosingActivity.PRINT_CLOSING) {
                sbSample.append(sb.toString());
                builder.addText(sb.toString());
            }

            sb.delete(0, sb.length());

            sb.append("Total Qty In       : " + totalCheckin + "\n");
            sb.append("Total Qty Out      : " + totalCheckout + "\n");
            sb.append("Total Qty Overnight: " + totalOverNight + "\n");
            sb.append("Total Tiket Batal  : " + totalVoid + "\n");
            sb.append("Total Tiket Hilang : " + lostTicket + "\n");
            sb.append("Total Reprint Tiket: " + closingParam.getTotalReprint() + "\n");
            sb.append("Cash               : " + MakeCurrencyString.fromInt(sumCash.intValue()) + "\n");
            sb.append("Income             : " + MakeCurrencyString.fromInt(sumIncome.intValue()) + "\n");

            sbSample.append(sb.toString());
            builder.addText(sb.toString());
            builder.addFeedLine(1);
            builder.addCut(Printer.CUT_FEED);

            builder.addTextFont(Builder.FONT_A);

            print();

    }


    public Bitmap drawTextToBitmap(Context gContext,
                                   int gResId,
                                   String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, gResId);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize((int) (14 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;

        canvas.drawText(gText, x, y, paint);

        return bitmap;
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
            sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }
}
