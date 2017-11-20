package valet.intan.com.valetparking.service;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.util.UnknownFormatConversionException;

import retrofit2.Response;
import valet.intan.com.valetparking.dao.FinishCheckoutDao;
import valet.intan.com.valetparking.domain.CheckoutData;
import valet.intan.com.valetparking.domain.EntryCheckinContainer;
import valet.intan.com.valetparking.domain.EntryCheckinResponse;
import valet.intan.com.valetparking.util.Logging;

/**
 * Created by fadlymunandar on 10/3/17.
 */

public class LoggingUtils {

    private Logging logging;
    private static LoggingUtils loggingUtils;
    private Context context;
    public static final String ACTION_STATUS_SEND_REPORT = "com.status.send.report";
    public static final String EXTRA_STATUS_SEND_REPORT = "com.extra.report";
    private LoggingUtils(Context context) {
        this.context = context;
        logging = Logging.getInstance(context);
    }

    public static LoggingUtils getInstance(Context context) {
        if (loggingUtils == null) {
            loggingUtils = new LoggingUtils(context);
        }
        return loggingUtils;
    }

    /*
    --------- Logging from login to set site -----------
     */
    public void logLogin(String username){
        logging.debug(username + " login");
    }

    public void logLoginSucced(String username) {
        logging.info("Login succeed with username: %s", username);
    }

    public void logToken(String token) {
        logging.info("Login secced with token: %s", token);
    }

    public void logExpiredDate(String expDate) {
        logging.info("Token Expired date: %s", expDate);
    }

    public void logLoginError(String username, String message) {
        logging.error("Login failed with username: " + username + ". Message: " + message);
    }

    public void logSetSiteAndLobby(String username, String site, String lobby) {
        logging.debug(username + " setting site: " + site + ", Lobby: " + lobby);
    }

    public void logSetSiteAndLobbySucceed(String site, String lobby, String remoteIdDevice, int lastCounterTicket) {
        logging.info("Set site and lobby succeed: %s", site + " - " + lobby);
        logging.info("ID Device: %s", remoteIdDevice);
        logging.info("Last counter Ticket: %s", String.valueOf(lastCounterTicket));
    }

    public void logSetSiteAndLobbyError(String message) {
        logging.error("Set site and lobby error due to %s", message);
    }

    public void logSetPrinter(String macAddress) {
        logging.info("Printer set with mac address: %s", macAddress);
    }


    /*
    ------ Logging add car / checkin -------
     */

    public void logAddCar(String username, String noTiket, String platNo, String carType, String carColor,String lobby) {
        logging.info("Add car/checkin : %S", "Username " + username + ", no tiket: " + noTiket + ", platNo: " + platNo
        + ", Car type: " + carType + ", Car color: " + carColor + ", lobby: " + lobby);
    }

    public void logPrintCheckinSucceed(String noTiket) {
        logging.info("Print checkin ticket suceed: %s", noTiket);
    }

    public void logPrintCheckinError(String noTiket) {
        logging.error("Print checkin ticket failed: %s", noTiket);
    }

    public void logSyncCheckinCalled() {
        logging.debug("Sync checkin service called");
    }

    public void logSyncCheckinAborted() {
        logging.error("Sync checkin service aborted due to internet connection problem");
    }

    public void logTotalSyncCheckin(int total) {
        logging.debug("Total checkin to post: " + total);
    }

    public void logPostingCheckin(EntryCheckinContainer entryCheckinContainer) {
        if (entryCheckinContainer != null && entryCheckinContainer.getEntryCheckin()!= null && entryCheckinContainer.getEntryCheckin().getAttrib() != null) {
            String noTiket = entryCheckinContainer.getEntryCheckin().getAttrib().getTicketNo();
            String platNo = entryCheckinContainer.getEntryCheckin().getAttrib().getTicketNo();
            logging.debug("Posting checkin, Ticket no: " + noTiket + ", platNo: " + platNo);
        }
    }

    public void logJsonCheckin(String json) {
        logging.json(json);
    }

    public void logPostingCheckinSucceed(EntryCheckinResponse response) {
        String noTiket = response.getData().getAttribute().getNoTiket().trim().replace(" ", "");
        String platNo = response.getData().getAttribute().getPlatNo();
        int remoteVthdId = response.getData().getAttribute().getId();
        String tiketSeq = response.getData().getAttribute().getIdTransaksi();

        logging.info("Posting checkin succeed %s", "No Tiket: " + noTiket + ", Plat NO: " + platNo +
        ", RemoteVthdId: " + remoteVthdId + ", Ticket Seq: " + tiketSeq);

    }

    public void logPostingCheckinError(String message, EntryCheckinContainer entryCheckinContainer) {
        String noTiket = null;
        String platNo = null;
        if (entryCheckinContainer != null && entryCheckinContainer.getEntryCheckin() != null && entryCheckinContainer.getEntryCheckin().getAttrib() != null) {
            noTiket = entryCheckinContainer.getEntryCheckin().getAttrib().getTicketNo();
            platNo = entryCheckinContainer.getEntryCheckin().getAttrib().getPlatNo();
        }
        logging.error("Posting checkin error %s", "Message: " + message + ", No Ticket: " + noTiket + ", " + "plat no: " + platNo);
    }

    public void logPostingCheckinFailed(Response<EntryCheckinResponse> response) {
        int code;
        String message;

        if (response != null) {
            code = response.code();
            message = response.message();
            logging.error("Posting checkin failed %s", "Code: " + code + ", Message: " + message);
        }
    }

    public void logTotalCheckoutDataToPost(int total) {
        logging.info("Total checkout data to post: %s", String.valueOf(total));
    }

    public void logCheckinDataEmpty() {
        logging.debug("Checkin data to post empty");
    }

    public void logAddParkedCarFromOtherDevices(EntryCheckinResponse entryCheckinResponse) {
        if (entryCheckinResponse != null && entryCheckinResponse.getData()!= null && entryCheckinResponse.getData().getAttribute() != null) {
            String platNo = entryCheckinResponse.getData().getAttribute().getPlatNo();
            String noTicket = entryCheckinResponse.getData().getAttribute().getNoTiket();

            logging.debug("Add parked car from other devices - No Ticket: " + noTicket + ", Platno: " + platNo);
        }
    }

    public void logServiceAddCheckinFromOtherDeviceCalled() {
        logging.debug("Service download checkin data from other devices called");
    }

    /*
    -------- log checkout process
     */

    public void logCheckout(String username, String noTicket, String platNo, String lobbyCheckout, String lobbyCheckin, String paymentMethod) {
        logging.debug(username + " checked out car - " + " No Ticket: " + noTicket + ", Plat no: " + platNo + ", Payment: " + paymentMethod + ", lobby checkout: " + lobbyCheckout + ", lobby checkin: " + lobbyCheckin);
        logPrintCheckoutSucceed(platNo, noTicket);
    }

    private void logPrintCheckoutSucceed(String platNo, String noTicket) {
        logging.debug("Print checkout succeed - " + "Plat No: " + platNo + ", No Ticket: " + noTicket);
    }

    public void logPrintCheckoutFailed(FinishCheckoutDao finishCheckoutDao) {

        if (finishCheckoutDao != null && finishCheckoutDao.getEntryCheckinResponse() != null && finishCheckoutDao.getEntryCheckinResponse().getData() != null
                && finishCheckoutDao.getEntryCheckinResponse().getData().getAttribute() != null) {
            String platNo = finishCheckoutDao.getEntryCheckinResponse().getData().getAttribute().getPlatNo();
            String noTicket = finishCheckoutDao.getEntryCheckinResponse().getData().getAttribute().getNoTiket();

            logging.error("Print checkout ticket failed - " + "No Ticket: " + noTicket + ", Plat No: " + platNo);
        }

    }

    public void logPostCheckoutServiceCalled() {
        logging.debug("Post checkout service called");
    }

    public void logPostCheckoutEmpty() {
        logging.debug("Checkout data to post is empty");
    }

    public void logPostCheckoutData(CheckoutData checkoutData) {
        if (checkoutData != null) {
            int id = checkoutData.getRemoteVthdId();
            String noTicket = checkoutData.getNoTiket();
            String json = checkoutData.getJsonData();

            logging.debug("Posting data checkout - " + "No Ticket: " + noTicket + ", " + "ID: " + id);
            logJsonCheckout(json);
        }
    }

    private void logJsonCheckout(String json) {
        logging.json(json);
    }

    public void logPostCheckoutSucceed(String noTicket) {
        logging.info("Post checkout succeed with ticket no: %s", noTicket);
    }

    public void logPostCheckoutFailed(String noTicket, int id, int responseCode) {
        logging.error("Post checkout failed with %s", "No Ticket: " + noTicket + ", ID: " + id + ", Response code: " + responseCode);
    }

    public void logPostCheckoutError(String noTicket, String message) {
        logging.error("Post checkout error with %s", "No Ticket: " + noTicket + ", Message: " + message);
    }

    public void logCheckoutFromAnotherLobby(String noTicket, String platNo) {
        logging.info("Checkout from another lobby %s", "No ticket: " + noTicket + ", Plat no: " + platNo);
    }


    /*
    -------- Log sync checkin pending data
     */

    public void logTotalCheckinPendingDataToPost(int total) {
        logging.info("Total checkin pending data to post: %s", String.valueOf(total));
    }

    public void logSyncPendingCheckin() {
        logging.debug("Sync checkin pending data");
    }

    public void logPostingCheckinPendingData(EntryCheckinContainer entryCheckinContainer) {
        if (entryCheckinContainer != null && entryCheckinContainer.getEntryCheckin() != null
                && entryCheckinContainer.getEntryCheckin().getAttrib() != null) {
            String noTicket = entryCheckinContainer.getEntryCheckin().getAttrib().getTicketNo();
            String platNo = entryCheckinContainer.getEntryCheckin().getAttrib().getPlatNo();
            String jsonCheckin = toJson(entryCheckinContainer);

            logging.debug("Posting checkin pending data. No ticket: " + noTicket + ", Plat No: " + platNo);
            logging.json(jsonCheckin);
        }
    }

    public void logPostingCheckinPendingDataSucceed(String noTicket, int lastTicketCounter) {
        try {
            logging.info("Posting checkin pending data succeed %", " No Ticket: " + noTicket + " last counter ticket: " + lastTicketCounter);
        }catch (UnknownFormatConversionException e) {
            e.printStackTrace();
        }
    }

    public void logPostingCheckinPendingDataFailed(String message, int responseCode) {
        logging.error("Posting checkin pending data failed. %", " Message: " + message + ", Response code: " + responseCode);
    }

    private String toJson(EntryCheckinContainer entryCheckinContainer) {
        try {
            return new Gson().toJson(entryCheckinContainer);
        }catch (JsonParseException e) {
            e.printStackTrace();
        }
        return "empty";
    }

    /*
    ------- Logging Checkout pending data
     */

    public void logSyncCheckoutPendingData() {
        logging.debug("Sync checkout pending data");
    }

    public void logTotalCheckoutPendingData(int total) {
        logging.info("Total checkout pending data to post: %s", String.valueOf(total));
    }

    public void logPostingCheckoutPendingData(CheckoutData checkoutData) {
        if (checkoutData != null) {
            String noTiket = checkoutData.getNoTiket();
            logging.debug("Posting checkout pending data: " + "No Ticket: " + noTiket);
        }
    }

    public void logPostingCheckoutPendingDataSucceed(String noTicket) {
        logging.info("Posting checkout pending data succeed: %s", "No ticket: " + noTicket);
    }

    public void logPostingCheckoutPendingDataFailed(String noTicket, String message, int responseCode) {
        logging.error("Posting checkout pending data error: %s", "No Ticket: " + noTicket + ", Message: " + message + ", Respone code: " + responseCode);
    }

    /*
    ------ Log Closing
     */

    public void logDownloadClosingData(String flag) {
        logging.debug("Downloading closing data " + flag);
    }

    public void logProcessingEOD(int total) {
        logging.debug("Processing EOD with total: " + total);
    }

    public void logEODSucceed() {
        logging.debug("EOD succeed");
    }

    public void logEODFailed(int code, String message) {
        logging.error("EOD failed. " + "Message: " + message + " Code: " + code);
    }

    public void logEODError(String message) {
        logging.error("EOD Error. " + "Message: " + message);
    }

    public void logPrintEODSucceed() {
        logging.debug("Print eod succeed");
    }

    public void  logPrintEODFailed(){
        logging.error("Print eod failed");
    }


    /*
    ------ Log logout / signout
     */

    public void logLogout(String username) {
        logging.debug(username + " logging out");
    }

    public void logLogoutSucceed(String username) {
        logging.debug(username + " logged out");
    }

    public void logLogoutFailed(String username, String message, int responseCode) {
        logging.error("Logout failed. %", "Username: " + username + ", Message: " + message + ", Code: " + responseCode);
    }

    public void logLogoutError(String username, String message) {
        logging.error("Logout error. %", "Username: " + username + ", Message: " + message);

    }

    public void removeLogFile() {
        String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folderPath = diskPath + File.separatorChar + "logger";

        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
    }


    /*
    ------ log refresh token
     */

    public void logOldToken(String oldToken) {
        logging.debug("Getting new token. Old token: " + oldToken);
    }

    public void logRefreshTokenSucceed(String newToken) {
        logging.info("Refresh token succed with new token %s", newToken);
    }

    public void logRefreshTokenFailed(String message, int code) {
        logging.error("Refresh token failed. Message " + message + ", Code: " + code);
    }

    public void logRefreshTokenError(String message) {
        logging.error("Refresh token failed. Message " + message);
    }

    public void logReplaceCurrentTokenWithBackupOne() {
        logging.debug("Current token replaced with backup token");
    }

    public void logNewTokenEqualsToTheLastOne(String oldToken, String newToken) {
        logging.error("New token is equals with the last one. Old Token: " + oldToken + ", New Token: " + newToken);
    }

    public void logIfTokenOrExpDateNull(String newToken, String expDate) {
        logging.error("Either new token or exp. date is null. New token: " + newToken + ", ExpDate: " + expDate);
    }

    /*
    -------- log send mail
     */

    public void sendMail() {
        GmailSender.send(context);
    }


}
