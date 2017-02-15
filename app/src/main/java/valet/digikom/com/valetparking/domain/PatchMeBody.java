package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 2/15/2017.
 */

public class PatchMeBody {
    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        @SerializedName("type")
        private final String type = "me";
        @SerializedName("role_options")
        private RoleOpt roleOpt;

        public void setRoleOpt(RoleOpt roleOpt) {
            this.roleOpt = roleOpt;
        }

        public static class RoleOpt {
            @SerializedName("usrl_id")
            private int userRoleId;
            @SerializedName("drms_id")
            private int lobbyId = 0;

            public void setUserRoleId(int userRoleId) {
                this.userRoleId = userRoleId;
            }

            public void setLobbyId(int lobbyId) {
                this.lobbyId = lobbyId;
            }
        }
    }
}
