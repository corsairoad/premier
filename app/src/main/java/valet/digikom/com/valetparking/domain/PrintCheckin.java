package valet.digikom.com.valetparking.domain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class PrintCheckin implements ReceiveListener {

    private Context context;
    private EntryCheckinResponse response;
    private Printer mPrinter;
    PrefManager prefManager;

    public PrintCheckin(Context context, EntryCheckinResponse response) {
        this.context = context;
        this.response = response;
        prefManager = PrefManager.getInstance(context);
        initializeObject();
    }

    public void print() {
        try {
            String noTransaksi = response.getData().getAttribute().getIdTransaksi();
            Bitmap logoData = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_1);
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String dropPoint = response.getData().getAttribute().getDropPoint();
            StringBuilder sb = new StringBuilder();

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);

            mPrinter.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);

            mPrinter.addFeedLine(2);

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addText(date + "           ");
            mPrinter.addText(dropPoint);

            mPrinter.addFeedLine(2);

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(2, 2);
            mPrinter.addText(noTransaksi);

            mPrinter.addFeedLine(2);

            mPrinter.addCut(Printer.CUT_FEED);

            //---------------------------------------
            mPrinter.addTextSize(1,1);
            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addImage(logoData, 0, 0,
                    logoData.getWidth(),
                    logoData.getHeight(),
                    Printer.COLOR_1,
                    Printer.MODE_MONO,
                    Printer.HALFTONE_DITHER,
                    Printer.PARAM_DEFAULT,
                    Printer.COMPRESS_AUTO);

            mPrinter.addFeedLine(2);

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextAlign(Printer.ALIGN_LEFT);
            mPrinter.addText(date + "           ");
            mPrinter.addText(dropPoint);

            mPrinter.addFeedLine(2);

            mPrinter.addTextAlign(Printer.ALIGN_CENTER);
            mPrinter.addTextSize(2, 2);
            mPrinter.addText(noTransaksi);

            mPrinter.addFeedLine(3);

            mPrinter.addTextAlign(Printer.ALIGN_LEFT);

            sb.append(" No. Plat      :    " + response.getData().getAttribute().getPlatNo() + "\n");
            sb.append(" Tipe Mobil    :    " + response.getData().getAttribute().getCar() + "\n");
            sb.append(" Warna         :    " + response.getData().getAttribute().getColor() + "\n");
            sb.append(" Sektor Parkir :    " + response.getData().getAttribute().getSektorParkir() + "\n");
            sb.append(" Blok Parkir   :    " + response.getData().getAttribute().getBlokParkir() + "\n");
            sb.append(" Area Parkir   :    " + response.getData().getAttribute().getAreaParkir() + "\n");

            mPrinter.addTextSize(1,1);
            mPrinter.addText(sb.toString());

            mPrinter.addFeedLine(2);

            mPrinter.addCut(Printer.CUT_FEED);

            //----------------------------
            connectPrinter();
        } catch (Epos2Exception e) {
            e.printStackTrace();
        }

    }
    private boolean initializeObject() {
        try {
            mPrinter = new Printer(Printer.TM_T88,Printer.LANG_EN,context);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "Printer", context);
            return false;
        }

        mPrinter.setReceiveEventListener(this);

        return true;
    }

    @Override
    public void onPtrReceive(final Printer printerObj, final int code, final PrinterStatusInfo status, final String printJobId) {

    }

    private String makeErrorMessage(PrinterStatusInfo status) {
        String msg = "";

        if (status.getOnline() == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_offline);
        }
        if (status.getConnection() == Printer.FALSE) {
            msg += context.getString(R.string.handlingmsg_err_no_response);
        }
        if (status.getCoverOpen() == Printer.TRUE) {
            msg += context.getString(R.string.handlingmsg_err_cover_open);
        }
        if (status.getPaper() == Printer.PAPER_EMPTY) {
            msg += context.getString(R.string.handlingmsg_err_receipt_end);
        }
        if (status.getPaperFeed() == Printer.TRUE || status.getPanelSwitch() == Printer.SWITCH_ON) {
            msg += context.getString(R.string.handlingmsg_err_paper_feed);
        }
        if (status.getErrorStatus() == Printer.MECHANICAL_ERR || status.getErrorStatus() == Printer.AUTOCUTTER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_autocutter);
            msg += context.getString(R.string.handlingmsg_err_need_recover);
        }
        if (status.getErrorStatus() == Printer.UNRECOVER_ERR) {
            msg += context.getString(R.string.handlingmsg_err_unrecover);
        }
        if (status.getErrorStatus() == Printer.AUTORECOVER_ERR) {
            if (status.getAutoRecoverError() == Printer.HEAD_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_head);
            }
            if (status.getAutoRecoverError() == Printer.MOTOR_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_motor);
            }
            if (status.getAutoRecoverError() == Printer.BATTERY_OVERHEAT) {
                msg += context.getString(R.string.handlingmsg_err_overheat);
                msg += context.getString(R.string.handlingmsg_err_battery);
            }
            if (status.getAutoRecoverError() == Printer.WRONG_PAPER) {
                msg += context.getString(R.string.handlingmsg_err_wrong_paper);
            }
        }
        if (status.getBatteryLevel() == Printer.BATTERY_LEVEL_0) {
            msg += context.getString(R.string.handlingmsg_err_battery_real_end);
        }

        return msg;
    }

    private boolean connectPrinter() {
        boolean isBeginTransaction = false;
        String target = prefManager.getPrinterMacAddress();
        if (mPrinter == null) {
            return false;
        }

        if (target == null) {
            return false;
        }
        try {
            mPrinter.connect(target,Printer.PARAM_DEFAULT);
        }
        catch (Exception e) {
            ShowMsg.showException(e, "connect", context);
            return false;
        }

        try {
            mPrinter.beginTransaction();
            isBeginTransaction = true;
            mPrinter.sendData(Printer.PARAM_DEFAULT);
            //disconnectPrinter();
        } catch (Exception e) {
            ShowMsg.showException(e, "beginTransaction", context);
        }

        if (isBeginTransaction == false) {
            try {
                mPrinter.disconnect();
            }
            catch (Epos2Exception e) {
                // Do nothing
                return false;
            }
        }

        return true;
    }

    private void finalizeObject() {
        if (mPrinter == null) {
            return;
        }

        mPrinter.clearCommandBuffer();

        mPrinter.setReceiveEventListener(null);

        mPrinter = null;
    }

    private void disconnectPrinter() {
        if (mPrinter == null) {
            return;
        }

        try {
            mPrinter.endTransaction();
        }
        catch (final Exception e) {

        }

        try {
            mPrinter.disconnect();
        }
        catch (final Exception e) {

        }

        finalizeObject();
    }
}
