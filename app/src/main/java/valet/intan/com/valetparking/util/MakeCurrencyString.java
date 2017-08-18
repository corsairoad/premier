package valet.intan.com.valetparking.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class MakeCurrencyString {

    public static String fromInt(int value) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return numberFormat.format(value).replace("$","Rp. ");
    }

}
