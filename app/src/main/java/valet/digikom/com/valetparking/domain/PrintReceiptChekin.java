package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.epson.epos2.printer.Printer;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;
import com.epson.eposprint.Print;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.util.MakeCurrencyString;

/**
 * Created by DIGIKOM-EX-99 on 2/26/2017.
 */

public class PrintReceiptChekin extends PrintReceipt {

    private EntryCheckinResponse response;
    private Bitmap bitmapDefect;
    private Bitmap bitmapSign;
    private List<AdditionalItems> itemsList;

    public PrintReceiptChekin(Context context, EntryCheckinResponse response, Bitmap bmpDefect, Bitmap bmpSign, List<AdditionalItems> items) {
        super(context);
        this.response = response;
        this.itemsList = items;
        setBitmapSignature(bmpSign);
        setBitmapDefect(bmpDefect);
    }

    public void setBitmapDefect(Bitmap bitmapDefect) {
        if (bitmapDefect == null) {
            this.bitmapDefect = PrintCheckin.scaleBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.car_vector_update_72),300,300);
        }else {
            this.bitmapDefect = PrintCheckin.scaleBitmap(bitmapDefect, 300, 300);
        }
        this.bitmapDefect = PrintCheckin.combineImages(this.bitmapDefect, bitmapSign);
    }

    public void setBitmapSignature(Bitmap bitmapSign) {
        this.bitmapSign = PrintCheckin.scaleBitmap(bitmapSign, 100,100);
        this.bitmapSign = PrintCheckin.createBorder(bitmapSign,2);
    }

    @Override
    public void buildPrintData() {
        String noTransaksi = response.getData().getAttribute().getIdTransaksi();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String dropPoint = response.getData().getAttribute().getDropPoint();
        String site = response.getData().getAttribute().getSiteName();
        String platNo = response.getData().getAttribute().getPlatNo();
        String valetType = response.getData().getAttribute().getValetType();
        Bitmap logoData = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.logo_1);
        Bitmap logoExlusive = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.logo_exclusive);
        if ("exclusive".equals(valetType.toLowerCase())) {
            logoData = PrintCheckin.combineImages(logoExlusive, logoData);
        }
        try {
            Builder builder = getBuilder();
            StringBuilder sb = new StringBuilder();

            builder.addTextAlign(Builder.ALIGN_CENTER);
            builder.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Builder.COLOR_1,
                    Builder.MODE_MONO,
                    Builder.HALFTONE_DITHER,
                    Builder.PARAM_DEFAULT);
            builder.addFeedLine(1);

            builder.addTextAlign(Printer.ALIGN_LEFT);
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
            sb.append("------------------------------------------\n");

            builder.addText(sb.toString());
            sb.delete(0, sb.length());
            builder.addFeedLine(1);
            builder.addTextAlign(Printer.ALIGN_CENTER);
            builder.addTextSize(1,1);
            builder.addText("disclaimer");
            builder.addFeedLine(1);
            builder.addCut(Printer.CUT_FEED);


            /*
            ------------------- Receipt for keyguard
             */
            if ("exclusive".equals(valetType.toLowerCase())) {
                builder.addTextAlign(Printer.ALIGN_CENTER);
                builder.addImage(logoData, 0, 0,
                        logoData.getWidth(),
                        logoData.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);

                builder.addFeedLine(1);

            }

            builder.addTextAlign(Printer.ALIGN_LEFT);
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
                builder.addTextAlign(Printer.ALIGN_CENTER);
                builder.addText("CHECK MOBIL");
                builder.addFeedLine(1);
                builder.addImage(bitmapDefect, 0, 0,
                        bitmapDefect.getWidth(),
                        bitmapDefect.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT);
            }
            builder.addFeedLine(1);

            if (itemsList != null && !itemsList.isEmpty()) {
                builder.addTextAlign(Printer.ALIGN_LEFT);
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
            builder.addCut(Printer.CUT_FEED);

            /*
            ---------- receipt to put on dashboard
             */
            if ("exclusive".equals(valetType.toLowerCase())) {
                builder.addTextAlign(Printer.ALIGN_CENTER);
                builder.addImage(logoData, 0, 0,
                        logoData.getWidth(),
                        logoData.getHeight(),
                        Printer.COLOR_1,
                        Printer.MODE_MONO,
                        Printer.HALFTONE_DITHER,
                        Printer.PARAM_DEFAULT,
                        Printer.COMPRESS_AUTO);

                builder.addFeedLine(1);
            }

            builder.addTextAlign(Printer.ALIGN_LEFT);
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

            builder.addTextAlign(Printer.ALIGN_CENTER);
            builder.addTextSize(2,1);
            builder.addText("DASHBOARD RECEIPT");

            builder.addFeedLine(1);
            builder.addCut(Printer.CUT_FEED);

            print();
        } catch (EposException e) {
            e.printStackTrace();
        }
    }
}
