package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.text.TextUtils;

import com.epson.epos2.printer.Printer;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import valet.digikom.com.valetparking.util.MakeCurrencyString;

/**
 * Created by DIGIKOM-EX4 on 2/27/2017.
 */

public class PrintReceiptClosing extends PrintReceipt {

    private PrintClosingParam closingParam;

    public PrintReceiptClosing(Context context,PrintClosingParam closingParam) {
        super(context);
        this.closingParam = closingParam;
    }

    @Override
    public void buildPrintData() {
        try {
            Builder builder = getBuilder();
            StringBuilder sb = new StringBuilder();

            builder.addTextAlign(Printer.ALIGN_CENTER);
            builder.addTextSize(2,2);
            builder.addText("REPORT");
            builder.addFeedLine(2);

            builder.addTextAlign(Printer.ALIGN_LEFT);
            builder.addTextSize(1,1);

            sb.append("LOKASI      : " + closingParam.getSiteName() + "\n");
            sb.append("LOBBY       : " + closingParam.getLobbyName() + "\n");
            sb.append("WAKTU REPORT: " + getCurrentDate() + "\n");
            sb.append("------------------------------------------\n");
            sb.append("Tiket  No. Pol   In     Out     Status\n");
            sb.append("------------------------------------------\n");
            builder.addText(sb.toString());
            sb.delete(0, sb.length());

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
                String transactNo = data.getAttributes().getTransactionId();
                String noPol = data.getAttributes().getPlatNo();
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
                    status = "Checkedout";
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

                sb.append(transactNo + " " + noPol + "\t"  + checkinTime + "\t" + checkoutTime + "\t" + status + "\n");
                i++;
            }

            sb.append("------------------------------------------\n");
            builder.addText(sb.toString());
            sb.delete(0, sb.length());

            sb.append("Total Qty In       : " + totalCheckin + "\n");
            sb.append("Total Qty Out      : " + totalCheckout + "\n");
            sb.append("Total Qty Overnight: " + totalOverNight + "\n");
            sb.append("Total Tiket Batal  : " + totalVoid + "\n");
            sb.append("Total Tiket Hilang : " + lostTicket + "\n");
            sb.append("Cash               : " + MakeCurrencyString.fromInt(sumCash.intValue()) + "\n");
            sb.append("Income             : " + MakeCurrencyString.fromInt(sumIncome.intValue()) + "\n");
            builder.addText(sb.toString());
            builder.addFeedLine(1);
            builder.addCut(Printer.CUT_FEED);

            print();
        } catch (EposException e) {
            e.printStackTrace();
        }
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
