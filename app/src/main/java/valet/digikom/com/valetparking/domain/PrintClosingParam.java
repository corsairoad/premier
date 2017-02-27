package valet.digikom.com.valetparking.domain;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 2/27/2017.
 */

public class PrintClosingParam {

    private List<ClosingData.Data> closingData;
    private String lobbyName;
    private String siteName;
    private String dateFrom;
    private String dateTo;
    private String adminName;
    private int numRegular;
    private int numExclusive;
    private int total;

    public PrintClosingParam(Builder builder) {
        this.closingData = builder.closingData;
        this.lobbyName = builder.lobbyName;
        this.siteName = builder.siteName;
        this.dateFrom = builder.dateFrom;
        this.dateTo = builder.dateTo;
        this.adminName = builder.adminName;
        this.numRegular = builder.numRegular;
        this.numExclusive = builder.numExclusive;
        this.total = builder.total;
    }

    public List<ClosingData.Data> getClosingData() {
        return closingData;
    }

    public void setClosingData(List<ClosingData.Data> closingData) {
        this.closingData = closingData;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public void setLobbyName(String lobbyName) {
        this.lobbyName = lobbyName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public int getNumRegular() {
        return numRegular;
    }

    public void setNumRegular(int numRegular) {
        this.numRegular = numRegular;
    }

    public int getNumExclusive() {
        return numExclusive;
    }

    public void setNumExclusive(int numExclusive) {
        this.numExclusive = numExclusive;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public static class Builder{
        private List<ClosingData.Data> closingData;
        private String lobbyName;
        private String siteName;
        private String dateFrom;
        private String dateTo;
        private String adminName;
        private int numRegular;
        private int numExclusive;
        private int total;

        public Builder setClosingData(List<ClosingData.Data> closingData) {
            this.closingData = closingData;
            return this;
        }

        public Builder setLobbyName(String lobbyName) {
            this.lobbyName = lobbyName;
            return this;
        }

        public Builder setSiteName(String siteName) {
            this.siteName = siteName;
            return this;
        }

        public Builder setDateFrom(String dateFrom) {
            this.dateFrom = dateFrom;
            return this;
        }

        public Builder setDateTo(String dateTo) {
            this.dateTo = dateTo;
            return this;
        }

        public Builder setAdminName(String adminName){
            this.adminName = adminName;
            return this;
        }

        public Builder setNumRegular(int numRegular) {
            this.numRegular = numRegular;
            return this;
        }

        public Builder setNumExClusive(int numExClusive) {
            this.numExclusive = numExClusive;
            return this;
        }

        public Builder setNumTotal(int numTotal){
            this.total = numTotal;
            return this;
        }

        public PrintClosingParam build() {
            return new PrintClosingParam(this);
        }
    }
}
