package valet.digikom.com.valetparking.domain;

import android.content.Context;

import com.epson.epos2.printer.Printer;

import valet.digikom.com.valetparking.CheckoutActivity;
import valet.digikom.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX4 on 2/27/2017.
 */

public class PrintCheckoutParam {

    String totalBayar;
    EntryCheckinResponse entryCheckinResponse;
    FinishCheckOut finishCheckOut;
    int overNightFine;
    int lostTicketFine;
    String noVoucher;
    MembershipResponse.Data dataMembership;
    String idMembersip;
    String checkoutTime;
    String totalCheckin;
    PaymentMethod.Data paymentData;
    Bank.Data bankData;

    public PrintCheckoutParam(Builder builder) {
        setTotalBayar(builder.totalBayar);
        setEntryCheckinResponse(builder.entryCheckinResponse);
        setFinishCheckOut(builder.finishCheckOut);
        setOverNightFine(builder.overNightFine);
        setLostTicketFine(builder.lostTicketFine);
        setNoVoucher(builder.noVoucher);
        setTotalCheckin(builder.totalCheckin);
        setDataMembership(builder.dataMembership);
        setIdMembersip(builder.idMembersip);
        setCheckoutTime(builder.checkoutTime);
        setPaymentData(builder.paymentData);
        setBankData(builder.bankData);
    }

    public String getTotalCheckin() {
        return totalCheckin;
    }

    public void setTotalCheckin(String totalCheckin) {
        this.totalCheckin = totalCheckin;
    }

    public String getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(String totalBayar) {
        this.totalBayar = totalBayar;
    }

    public EntryCheckinResponse getEntryCheckinResponse() {
        return entryCheckinResponse;
    }

    public void setEntryCheckinResponse(EntryCheckinResponse entryCheckinResponse) {
        this.entryCheckinResponse = entryCheckinResponse;
    }

    public FinishCheckOut getFinishCheckOut() {
        return finishCheckOut;
    }

    public void setFinishCheckOut(FinishCheckOut finishCheckOut) {
        this.finishCheckOut = finishCheckOut;
    }

    public int getOverNightFine() {
        return overNightFine;
    }

    public void setOverNightFine(int overNightFine) {
        this.overNightFine = overNightFine;
    }

    public int getLostTicketFine() {
        return lostTicketFine;
    }

    public void setLostTicketFine(int lostTicketFine) {
        this.lostTicketFine = lostTicketFine;
    }

    public String getNoVoucher() {
        return noVoucher;
    }

    public void setNoVoucher(String noVoucher) {
        this.noVoucher = noVoucher;
    }

    public MembershipResponse.Data getDataMembership() {
        return dataMembership;
    }

    public void setDataMembership(MembershipResponse.Data dataMembership) {
        this.dataMembership = dataMembership;
    }

    public String getIdMembersip() {
        return idMembersip;
    }

    public void setIdMembersip(String idMembersip) {
        this.idMembersip = idMembersip;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public PaymentMethod.Data getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(PaymentMethod.Data paymentData) {
        this.paymentData = paymentData;
    }

    public Bank.Data getBankData() {
        return bankData;
    }

    public void setBankData(Bank.Data bankData) {
        this.bankData = bankData;
    }

    public static class Builder {
        String totalBayar;
        EntryCheckinResponse entryCheckinResponse;
        FinishCheckOut finishCheckOut;
        int overNightFine;
        int lostTicketFine;
        String noVoucher;
        MembershipResponse.Data dataMembership;
        String idMembersip;
        String checkoutTime;
        String totalCheckin;
        PaymentMethod.Data paymentData;
        Bank.Data bankData;

        public Builder() {
        }

        public Builder setTotalBayar(String totalBayar) {
            this.totalBayar = totalBayar;
            return this;
        }

        public Builder setEntryCheckinResponse(EntryCheckinResponse response) {
            this.entryCheckinResponse = response;
            return this;
        }

        public Builder setTotalCheckin(String totalCheckin) {
            this.totalCheckin = totalCheckin;
            return this;
        }

        public Builder setFinishCheckout(FinishCheckOut finishCheckout) {
            this.finishCheckOut = finishCheckout;
            return this;
        }

        public Builder setOvernigthFine(int fine) {
            this.overNightFine = fine;
            return this;
        }

        public Builder setLostTicketFine(int fine) {
            this.lostTicketFine = fine;
            return this;
        }

        public Builder setNovoucher(String noVoucher) {
            this.noVoucher = noVoucher;
            return this;
        }

        public Builder setDataMembership(MembershipResponse.Data dataMembership) {
            this.dataMembership = dataMembership;
            return this;
        }

        public Builder setIdMembership (String idMembership) {
            this.idMembersip = idMembership;
            return this;
        }

        public Builder setCheckoutTime(String checkoutTime) {
            this.checkoutTime = checkoutTime;
            return this;
        }

        public Builder setPaymentData(PaymentMethod.Data paymentData) {
            this.paymentData = paymentData;
            return this;
        }

        public Builder setBankData(Bank.Data bankData) {
            this.bankData = bankData;
            return this;
        }

        public PrintCheckoutParam build() {
            return new PrintCheckoutParam(this);
        }
    }
}
