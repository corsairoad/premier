package valet.intan.com.valetparking.util;

import com.epson.eposprint.EposException;

/**
 * Created by DIGIKOM-EX4 on 4/6/2017.
 */

public class MyPrinterException extends EposException {


    public MyPrinterException(int value) {
        super(value);
    }


}
