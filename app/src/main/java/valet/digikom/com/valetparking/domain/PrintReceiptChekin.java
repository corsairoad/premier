package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.dao.DisclaimerDao;
import valet.digikom.com.valetparking.util.MakeCurrencyString;

/**
 * Created by DIGIKOM-EX-99 on 2/26/2017.
 */

public class PrintReceiptChekin extends PrintReceipt {

    private EntryCheckinResponse response;
    private Bitmap bitmapDefect;
    private Bitmap bitmapSign;
    private List<AdditionalItems> itemsList;

    String noTransaksi;
    String date;
    String dropPoint;
    String site;
    String platNo;
    String valetType;
    String disclaimer;
    Bitmap logoData;
    Bitmap logoExlusive;

    public PrintReceiptChekin(Context context, EntryCheckinResponse response, Bitmap bmpDefect, Bitmap bmpSign, List<AdditionalItems> items) {
        super(context);
        this.response = response;
        this.itemsList = items;
        setBitmapSignature(bmpSign);
        setBitmapDefect(bmpDefect);
        initFields();
    }

    private void initFields() {
        noTransaksi = response.getData().getAttribute().getIdTransaksi();
        date = response.getData().getAttribute().getCheckinTime();
        dropPoint = response.getData().getAttribute().getDropPoint();
        Disclaimer.Data disclaimerObj = DisclaimerDao.getInstance(getContext()).getDisclaimer(DisclaimerDao.FLAG_CHECKIN_DISCLAIMER);
        if (disclaimerObj != null) {
            disclaimer = disclaimerObj.getAttrib().getDscDesc();
        }

        site = response.getData().getAttribute().getSiteName();
        platNo = response.getData().getAttribute().getPlatNo();
        valetType = response.getData().getAttribute().getValetType();
        logoData = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.logo_1);
        logoExlusive = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.logo_exclusive);

        if ("exclusive".equals(valetType.toLowerCase())) {
            logoData = combineImages(logoExlusive, logoData);
        }
    }

    private void setBitmapSignature(Bitmap bitmapSign) {
        this.bitmapSign = scaleBitmap(bitmapSign, 100,100);
        this.bitmapSign = createBorder(this.bitmapSign,1);
    }

    private void setBitmapDefect(Bitmap bitmapDefect) {
        if (bitmapDefect == null) {
            this.bitmapDefect = scaleBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.car_vector_update_72),300,300);
        }else {
            this.bitmapDefect = scaleBitmap(bitmapDefect, 300, 300);
        }
        this.bitmapDefect = combineImages(this.bitmapDefect, this.bitmapSign);
    }

    @Override
    public void buildPrintData() {

        try {
            Builder builder = getBuilder();
            StringBuilder sb = new StringBuilder();

            buildDataForCustomer(builder, sb);
            buildDataForKeyGuard(builder, sb);
            buildDataForDashboard(builder, sb);

            print();
        } catch (EposException e) {
            e.printStackTrace();
        }
    }

    private void buildDataForCustomer(Builder builder, StringBuilder sb) throws EposException {
            addLogo(builder, logoData);

            builder.addTextAlign(Builder.ALIGN_LEFT);
            builder.addTextSize(1, 1);
            sb.append(" No. Tiket     : " + noTransaksi + "\n");
            sb.append(" No. Plat      : " + platNo + "\n");
            sb.append(" Valet         : " + valetType + "\n");
            sb.append(" Checkin       : " + date + "\n");
            sb.append(" Tipe Mobil    : " + response.getData().getAttribute().getCar() + "\n");
            if (response.getData().getAttribute().getColor() != null) {
                sb.append(" Warna         : " + response.getData().getAttribute().getColor() + "\n");
            }
            sb.append(" Drop Point    : " + dropPoint + "\n");
            sb.append(" Harga         : " + MakeCurrencyString.fromInt(response.getData().getAttribute().getFee()) + "\n");

            builder.addText(sb.toString());
            sb.delete(0, sb.length());
            builder.addFeedLine(1);
            builder.addTextAlign(Builder.ALIGN_CENTER);
            builder.addTextSize(1,1);
            builder.addText(disclaimer);
            builder.addFeedLine(1);
            builder.addCut(Builder.CUT_FEED);

    }

    private void buildDataForKeyGuard(Builder builder, StringBuilder sb) throws EposException {
            if ("exclusive".equals(valetType.toLowerCase())) {
                addLogo(builder, logoData);
            }
            builder.addTextAlign(Builder.ALIGN_LEFT);
            builder.addTextSize(1, 1);
            sb.append(" No. Tiket     : " + noTransaksi + "\n");
            sb.append(" No. Plat      : " + platNo + "\n");
            sb.append(" Valet         : " + valetType + "\n");
            sb.append(" Checkin       : " + date + "\n");
            sb.append(" Tipe Mobil    : " + response.getData().getAttribute().getCar() + "\n");
            if (response.getData().getAttribute().getColor() != null) {
                sb.append(" Warna         : " + response.getData().getAttribute().getColor() + "\n");
            }
            sb.append(" Drop Point    : " + dropPoint + "\n");
            //sb.append(" Harga         :  " + MakeCurrencyString.fromInt(response.getData().getAttribute().getFee()));

            builder.addText(sb.toString());
            sb.delete(0, sb.length());
            // image defect
            if (bitmapDefect != null) {
                builder.addFeedLine(1);
                builder.addTextAlign(Builder.ALIGN_CENTER);
                builder.addText("CHECK MOBIL");
                builder.addFeedLine(1);
                builder.addImage(bitmapDefect, 0, 0,
                        bitmapDefect.getWidth(),
                        bitmapDefect.getHeight(),
                        Builder.COLOR_1,
                        Builder.MODE_MONO,
                        Builder.HALFTONE_DITHER,
                        Builder.PARAM_DEFAULT);
            }
            builder.addFeedLine(1);

            if (itemsList != null && !itemsList.isEmpty()) {
                builder.addTextAlign(Builder.ALIGN_LEFT);
                sb.append("Barang Berharga: ");
                builder.addFeedLine(1);
                int loop = 1;
                for (AdditionalItems i : itemsList) {
                    sb.append(i.getAttributes().getAdditionalItemMaster().getName());
                    if (loop < itemsList.size()){
                        sb.append(", ");
                    }
                    loop++;
                }
                builder.addText(sb.toString());
                sb.delete(0, sb.length());
                builder.addFeedLine(1);
            }
            builder.addFeedLine(1);
            builder.addCut(Builder.CUT_FEED);

    }

    private void buildDataForDashboard(Builder builder, StringBuilder sb) throws EposException {


            if ("exclusive".equals(valetType.toLowerCase())) {
                addLogo(builder, logoData);
            }

            builder.addTextAlign(Builder.ALIGN_LEFT);
            builder.addTextSize(1, 1);
            sb.append(" No. Tiket     : " + noTransaksi + "\n");
            sb.append(" No. Plat      : " + platNo + "\n");
            sb.append(" Valet         : " + valetType + "\n");
            sb.append(" Checkin       : " + date + "\n");
            sb.append(" Tipe Mobil    : " + response.getData().getAttribute().getCar() + "\n");
            if (response.getData().getAttribute().getColor() != null) {
                sb.append(" Warna         : " + response.getData().getAttribute().getColor() + "\n");
            }
            sb.append(" Drop Point    : " + dropPoint + "\n");
            //sb.append(" Harga         :  " + MakeCurrencyString.fromInt(response.getData().getAttribute().getFee()));

            builder.addText(sb.toString());
            sb.delete(0, sb.length());
            builder.addFeedLine(2);

            builder.addTextAlign(Builder.ALIGN_CENTER);
            builder.addTextSize(2,1);
            builder.addText("DASHBOARD RECEIPT");

            builder.addFeedLine(1);
            builder.addCut(Builder.CUT_FEED);

    }
}
