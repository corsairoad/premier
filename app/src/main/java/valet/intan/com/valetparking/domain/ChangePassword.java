package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 2/20/2017.
 */

public class ChangePassword {
    @SerializedName("data")
    private Data data;

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("type")
        private final String type = "me/change_password";
        @SerializedName("attributes")
        private Attr attr;

        public void setAttr(Attr attr) {
            this.attr = attr;
        }

        public static class Attr {
            @SerializedName("old_usmsPassword")
            private String oldPassword;
            @SerializedName("new_usmsPassword")
            private String newPassword;
            @SerializedName("con_usmsPassword")
            private String retypePassword;

            public void setOldPassword(String oldPassword) {
                this.oldPassword = oldPassword;
            }

            public void setNewPassword(String newPassword) {
                this.newPassword = newPassword;
            }

            public void setRetypePassword(String retypePassword) {
                this.retypePassword = retypePassword;
            }
        }
    }
}
