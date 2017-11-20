package valet.intan.com.valetparking.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;

import java.util.UnknownFormatConversionException;

/**
 * Created by fadlymunandar on 10/3/17.
 */

public class Logging {

    private Context context;
    private static Logging logging;

    private Logging(Context context) {
        this.context = context;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String versionName = packageInfo.versionName;
            FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                    .tag("MVS " + versionName)
                    .build();
            Logger.addLogAdapter(new DiskLogAdapter(formatStrategy));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Logging getInstance(Context context) {
        if (logging == null) {
            logging = new Logging(context);
        }
        return logging;
    }

    public void debug(String message) {
        try {
            Logger.d(message);
        }catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }

    }

    public void error(String message) {
        try {
            Logger.e(message);
        }catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }
    }

    public void error(String message, String value) {
        try {
            Logger.e(message, value);
        }catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }
    }

    public void info(String message, String value) {
        try {
            Logger.i(message, value);
        }catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }

    }

    public void warning(String message, Object object) {
        try {
            Logger.w(message, object);
        }catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }

    }

    public void json(String json) {
        try {
            Logger.json(json);
        }catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }

    }
}
