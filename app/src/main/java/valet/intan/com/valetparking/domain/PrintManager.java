package valet.intan.com.valetparking.domain;

import android.content.Context;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;

/**
 * Created by DIGIKOM-EX4 on 2/18/2017.
 */

public class PrintManager {

    private static Printer mPrinter;


    public static Printer getPrinterInstance(Context context) throws Epos2Exception {
        if (mPrinter == null) {
            mPrinter = new Printer(Printer.TM_T88,Printer.MODEL_ANK,context);
        }

        return mPrinter;
    }
}
