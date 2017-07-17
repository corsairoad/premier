package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.eposprint.Builder;
import com.epson.eposprint.EposException;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.PrintReceipt;

import static valet.digikom.com.valetparking.domain.ShowMsg.getEposExceptionText;

/**
 * Created by DIGIKOM-EX4 on 3/2/2017.
 */

public class PrintReceiptTest extends PrintReceipt {
    
    public PrintReceiptTest(Context context) {
        super(context);
    }

    @Override
    public void buildPrintData() {
        try {
            Builder builder = getBuilder();
            StringBuilder sb = new StringBuilder();

            buildData(builder, sb);
            print();
            closePrinter();
            
        } catch (EposException e) {
            e.printStackTrace();
        }
    }
    
    private void buildData(Builder builder, StringBuilder textData) throws EposException {
        builder.addFeedLine(1);

        builder.addText("the quick brown fox jumped over the lazy dog\n" +
                "1234567890\n");

        builder.addFeedLine(1);

        builder.addCut(Printer.CUT_FEED);

        print();
    }
}
