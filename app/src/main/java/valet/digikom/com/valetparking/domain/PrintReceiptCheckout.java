package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.text.TextUtils;

import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;

import valet.digikom.com.valetparking.dao.DisclaimerDao;
import valet.digikom.com.valetparking.util.MakeCurrencyString;

/**
 * Created by DIGIKOM-EX4 on 2/27/2017.
 */

public class PrintReceiptCheckout extends PrintReceipt {

    private PrintCheckoutParam printCheckoutParam;

    public PrintReceiptCheckout(Context context, PrintCheckoutParam printCheckoutParam) {
        super(context);
        this.printCheckoutParam = printCheckoutParam;
    }

    private boolean isVIP(String valetType){
        if ("exclusive".equals(valetType.toLowerCase())) {
            //setLogoData(combineImages(getLogoExclusive(), getLogoData()));
            return true;
        }
        return false;
    }

    @Override
    public void buildPrintData() {
        try {
            Builder builder = getBuilder();
            StringBuilder sb = new StringBuilder();

            String valetType = printCheckoutParam.getEntryCheckinResponse().getData().getAttribute().getValetType();
            String site = printCheckoutParam.getEntryCheckinResponse().getData().getAttribute().getSiteName();
            String noTrans = printCheckoutParam.getEntryCheckinResponse().getData().getAttribute().getNoTiket();
            String noPol = printCheckoutParam.getEntryCheckinResponse().getData().getAttribute().getPlatNo();
            String jenis = printCheckoutParam.getEntryCheckinResponse().getData().getAttribute().getCar();
            String warna = printCheckoutParam.getEntryCheckinResponse().getData().getAttribute().getColor();
            String checkinTime = printCheckoutParam.getEntryCheckinResponse().getData().getAttribute().getCheckinTime();
            String checkoutTime = printCheckoutParam.getCheckoutTime();
            String paymentName = printCheckoutParam.getPaymentData().getAttr().getPaymentName();
            String ticketSeq = printCheckoutParam.getTotalCheckin();
            String feex = MakeCurrencyString.fromInt(printCheckoutParam.getEntryCheckinResponse().getData().getAttribute().getFee());
            int lostTicket = printCheckoutParam.getLostTicketFine();
            int overNight = printCheckoutParam.getOverNightFine();
            String noVoucher = printCheckoutParam.getNoVoucher();
            MembershipResponse.Data dataMembership = printCheckoutParam.getDataMembership();
            String idMembership = printCheckoutParam.getIdMembersip();
            PaymentMethod.Data paymentData =  printCheckoutParam.getPaymentData();
            Bank.Data bankData= printCheckoutParam.getBankData();
            String totalBayar = printCheckoutParam.getTotalBayar();
            Disclaimer.Data disclaimerObj = DisclaimerDao.getInstance(getContext()).getDisclaimer(DisclaimerDao.FLAG_CHECKOUT_DISCLAIMER);
            String disclaimer = "";

            if (disclaimerObj != null) {
                disclaimer = disclaimerObj.getAttrib().getDscDesc();
            }

            /*
            if ("exclusive".equals(valetType.toLowerCase())) {
                setLogoData(combineImages(getLogoExclusive(), getLogoData()));
            }
            */

            addLogo(builder,getLogoData());

            if (isVIP(valetType)) {
                builder.addTextAlign(Builder.ALIGN_CENTER);
                builder.addTextSize(2,2);
                builder.addText("VIP");
                builder.addFeedLine(2);
            }

            builder.addTextSize(1,1);
            builder.addText(site);
            builder.addFeedLine(2);
            builder.addTextSize(2,2);
            builder.addText("RECEIPT");
            builder.addFeedLine(2);
            builder.addTextAlign(Builder.ALIGN_LEFT);
            builder.addTextSize(1,1);

            sb.append(" No. Tiket     : " + noTrans + "\n");
            if (!TextUtils.isEmpty(ticketSeq)) {
                sb.append(" Id. Transaksi : " + ticketSeq + "\n");
            }
            sb.append(" No. Plat      : " + noPol + "\n");
            sb.append(" Valet         : " + valetType + "\n");
            sb.append(" Jenis Mobil   : " + jenis + "\n");
            if (warna != null) {
                sb.append(" Warna         : " + warna + "\n");
            }
            sb.append(" Checkin       : " + checkinTime + "\n");
            sb.append(" Checkout      : " + checkoutTime + "\n");
            sb.append(" Payment       : " + paymentName + "\n");
            sb.append(" Fee           : " + feex + "\n");

            if (lostTicket > 0) {
                sb.append(" Tiket Hilang  : " + MakeCurrencyString.fromInt(lostTicket) + "\n");
            }

            if (overNight > 0) {
                sb.append(" Mobil Menginap: " + MakeCurrencyString.fromInt(overNight) + "\n");
            }

            if (!TextUtils.isEmpty(noVoucher)) {
                sb.append(" No Voucher    : " + noVoucher + "\n");
            }

            if (dataMembership != null) {
                sb.append(" Membership    : " + dataMembership.getAttr().getName() + "\n");
                sb.append(" Id Membership : " + idMembership + "\n");
                int price = Integer.valueOf(dataMembership.getAttr().getPrice());
                sb.append(" Discount Mmbr :  " + MakeCurrencyString.fromInt(price) + "\n");
            }


            if (paymentData.getAttr().getPaymentId() == 4) {
                if (bankData != null) {
                    sb.append(" Bank          : " + bankData.getAttr().getBankName() + "\n");
                }
            }

            sb.append("----------------------------------------");
            builder.addText(sb.toString());
            sb.delete(0, sb.length());

            builder.addFeedLine(1);
            builder.addTextSize(1,1);
            sb.append(" Total         : " + totalBayar);
            builder.addText(sb.toString());
            builder.addFeedLine(2);

            builder.addTextAlign(Builder.ALIGN_CENTER);
            builder.addText(disclaimer);
            builder.addFeedLine(1);
            builder.addCut(Builder.CUT_FEED);

            print();

        } catch (EposException e) {
            e.printStackTrace();
        }

    }
}
