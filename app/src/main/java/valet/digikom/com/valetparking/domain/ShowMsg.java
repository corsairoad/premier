package valet.digikom.com.valetparking.domain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.epson.epos2.Epos2CallbackCode;
import com.epson.epos2.Epos2Exception;
import com.epson.eposprint.Print;

import valet.digikom.com.valetparking.R;

public class ShowMsg {

    static final int BITCNT_INT = 32;

    public static void showException(Exception e, String method, Context context) {
        String msg = "";
        if (e instanceof Epos2Exception) {
            msg = String.format(
                      "%s\n\t%s\n%s\n\t%s",
                      context.getString(R.string.title_err_code),
                      getEposExceptionText(((Epos2Exception) e).getErrorStatus()),
                      context.getString(R.string.title_err_method),
                      method);
        }
        else {
            msg = e.toString();
        }
        show(msg, context);
    }

    public static void showResult(int code, String errMsg, Context context) {
        String msg = "";
        if (errMsg.isEmpty()) {
            msg = String.format(
                      "\t%s\n\t%s\n",
                      context.getString(R.string.title_msg_result),
                      getCodeText(code));
        }
        else {
            msg = String.format(
                      "\t%s\n\t%s\n\n\t%s\n\t%s\n",
                      context.getString(R.string.title_msg_result),
                      getCodeText(code),
                      context.getString(R.string.title_msg_description),
                      errMsg);
        }
        show(msg, context);
    }

    public static void showMsg(String msg, Context context) {
        show(msg, context);
    }

    private static void show(String msg, Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                return ;
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private static String getEposExceptionText(int state) {
        String return_text = "";
        switch (state) {
            case    Epos2Exception.ERR_PARAM:
                return_text = "ERR_PARAM";
                break;
            case    Epos2Exception.ERR_CONNECT:
                return_text = "ERR_CONNECT";
                break;
            case    Epos2Exception.ERR_TIMEOUT:
                return_text = "ERR_TIMEOUT";
                break;
            case    Epos2Exception.ERR_MEMORY:
                return_text = "ERR_MEMORY";
                break;
            case    Epos2Exception.ERR_ILLEGAL:
                return_text = "ERR_ILLEGAL";
                break;
            case    Epos2Exception.ERR_PROCESSING:
                return_text = "ERR_PROCESSING";
                break;
            case    Epos2Exception.ERR_NOT_FOUND:
                return_text = "ERR_NOT_FOUND";
                break;
            case    Epos2Exception.ERR_IN_USE:
                return_text = "ERR_IN_USE";
                break;
            case    Epos2Exception.ERR_TYPE_INVALID:
                return_text = "ERR_TYPE_INVALID";
                break;
            case    Epos2Exception.ERR_DISCONNECT:
                return_text = "ERR_DISCONNECT";
                break;
            case    Epos2Exception.ERR_ALREADY_OPENED:
                return_text = "ERR_ALREADY_OPENED";
                break;
            case    Epos2Exception.ERR_ALREADY_USED:
                return_text = "ERR_ALREADY_USED";
                break;
            case    Epos2Exception.ERR_BOX_COUNT_OVER:
                return_text = "ERR_BOX_COUNT_OVER";
                break;
            case    Epos2Exception.ERR_BOX_CLIENT_OVER:
                return_text = "ERR_BOX_CLIENT_OVER";
                break;
            case    Epos2Exception.ERR_UNSUPPORTED:
                return_text = "ERR_UNSUPPORTED";
                break;
            case    Epos2Exception.ERR_FAILURE:
                return_text = "ERR_FAILURE";
                break;
            default:
                return_text = String.format("%d", state);
                break;
        }
        return return_text;
    }

    private static String getCodeText(int state) {
        String return_text = "";
        switch (state) {
            case Epos2CallbackCode.CODE_SUCCESS:
                return_text = "PRINT_SUCCESS";
                break;
            case Epos2CallbackCode.CODE_PRINTING:
                return_text = "PRINTING";
                break;
            case Epos2CallbackCode.CODE_ERR_AUTORECOVER:
                return_text = "ERR_AUTORECOVER";
                break;
            case Epos2CallbackCode.CODE_ERR_COVER_OPEN:
                return_text = "ERR_COVER_OPEN";
                break;
            case Epos2CallbackCode.CODE_ERR_CUTTER:
                return_text = "ERR_CUTTER";
                break;
            case Epos2CallbackCode.CODE_ERR_MECHANICAL:
                return_text = "ERR_MECHANICAL";
                break;
            case Epos2CallbackCode.CODE_ERR_EMPTY:
                return_text = "ERR_EMPTY";
                break;
            case Epos2CallbackCode.CODE_ERR_UNRECOVERABLE:
                return_text = "ERR_UNRECOVERABLE";
                break;
            case Epos2CallbackCode.CODE_ERR_FAILURE:
                return_text = "ERR_FAILURE";
                break;
            case Epos2CallbackCode.CODE_ERR_NOT_FOUND:
                return_text = "ERR_NOT_FOUND";
                break;
            case Epos2CallbackCode.CODE_ERR_SYSTEM:
                return_text = "ERR_SYSTEM";
                break;
            case Epos2CallbackCode.CODE_ERR_PORT:
                return_text = "ERR_PORT";
                break;
            case Epos2CallbackCode.CODE_ERR_TIMEOUT:
                return_text = "ERR_TIMEOUT";
                break;
            case Epos2CallbackCode.CODE_ERR_JOB_NOT_FOUND:
                return_text = "ERR_JOB_NOT_FOUND";
                break;
            case Epos2CallbackCode.CODE_ERR_SPOOLER:
                return_text = "ERR_SPOOLER";
                break;
            case Epos2CallbackCode.CODE_ERR_BATTERY_LOW:
                return_text = "ERR_BATTERY_LOW";
                break;
            case Epos2CallbackCode.CODE_ERR_TOO_MANY_REQUESTS:
                return_text = "ERR_TOO_MANY_REQUESTS";
                break;
            default:
                return_text = String.format("%d", state);
                break;
        }
        return return_text;
    }

    static void showStatus(int result, int status, int battery, Context context){
        String msg;
        msg = String.format(
                "%s\n\t%s\n%s\n%s\n%s\n\t0x%04X",
                "STATUS",
                getEposExceptionText(result),
               "STATUS",
                getEposStatusText(status),
               "BATTERY STATUS",
                battery);
        show(msg, context);
    }

    private static String getEposStatusText(int status){
        String result = "";

        for(int bit = 0; bit <BITCNT_INT; bit++){
            int value = 1 << bit;
            if((value & status) != 0){
                String msg = "";
                switch(value){
                    case    Print.ST_NO_RESPONSE:
                        msg = "NO_RESPONSE";
                        break;
                    case    Print.ST_PRINT_SUCCESS:
                        msg = "PRINT_SUCCESS";
                        break;
                    case    Print.ST_DRAWER_KICK:
                        msg = "DRAWER_KICK";
                        break;
                    case    Print.ST_OFF_LINE:
                        msg = "OFF_LINE";
                        break;
                    case    Print.ST_COVER_OPEN:
                        msg = "COVER_OPEN";
                        break;
                    case    Print.ST_PAPER_FEED:
                        msg = "PAPER_FEED";
                        break;
                    case    Print.ST_WAIT_ON_LINE:
                        msg = "WAIT_ON_LINE";
                        break;
                    case    Print.ST_PANEL_SWITCH:
                        msg = "PANEL_SWITCH";
                        break;
                    case    Print.ST_MECHANICAL_ERR:
                        msg = "MECHANICAL_ERR";
                        break;
                    case    Print.ST_AUTOCUTTER_ERR:
                        msg = "AUTOCUTTER_ERR";
                        break;
                    case    Print.ST_UNRECOVER_ERR:
                        msg = "UNRECOVER_ERR";
                        break;
                    case    Print.ST_AUTORECOVER_ERR:
                        msg = "AUTORECOVER_ERR";
                        break;
                    case    Print.ST_RECEIPT_NEAR_END:
                        msg = "RECEIPT_NEAR_END";
                        break;
                    case    Print.ST_RECEIPT_END:
                        msg = "RECEIPT_END";
                        break;
                    case    Print.ST_BUZZER:
                        break;
                    case	Print.ST_HEAD_OVERHEAT:
                        msg = "HEAD_OVERHEAT";
                        break;
                    case	Print.ST_MOTOR_OVERHEAT:
                        msg = "MOTOR_OVERHEAT";
                        break;
                    case	Print.ST_BATTERY_OVERHEAT:
                        msg = "BATTERY_OVERHEAT";
                        break;
                    case	Print.ST_WRONG_PAPER:
                        msg = "WRONG_PAPER";
                        break;
                    default:
                        msg = String.format("%d", value);
                        break;
                }
                if(!msg.isEmpty()){
                    if(!result.isEmpty()){
                        result += "\n";
                    }
                    result += "\t" + msg;
                }
            }
        }

        return result;
    }

}
