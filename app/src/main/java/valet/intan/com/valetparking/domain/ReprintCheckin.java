package valet.intan.com.valetparking.domain;

import android.content.Context;
import android.graphics.Bitmap;

import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 4/5/2017.
 */

public class ReprintCheckin extends PrintReceiptChekin {
    private Context context;

    public ReprintCheckin(Context context, EntryCheckinResponse response, Bitmap bmpDefect, Bitmap bmpSign, List<AdditionalItems> items) {
        super(context, response, bmpDefect, bmpSign, items);
        this.context = context;
    }

    @Override
    public void buildDataForCustomer(Builder builder, StringBuilder sb) throws EposException {
        addReprintHeader(builder);
        super.buildDataForCustomer(builder, sb);
    }

    @Override
    public void buildDataForDashboard(Builder builder, StringBuilder sb) throws EposException {
        addReprintHeader(builder);
        super.buildDataForDashboard(builder, sb);
    }

    @Override
    public void buildDataForKeyGuard(Builder builder, StringBuilder sb) throws EposException {
        addReprintHeader(builder);
        super.buildDataForKeyGuard(builder, sb);
    }

    private void addReprintHeader(Builder builder) throws EposException {
        builder.addTextAlign(Builder.ALIGN_CENTER);
        builder.addTextSize(1, 1);
        builder.addText("REPRINT");
        builder.addFeedLine(1);
    }
}
