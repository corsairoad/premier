package valet.intan.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DIGIKOM-EX4 on 1/29/2017.
 */

public class AuthResponse {

    public static final String KEY = AuthResponse.class.getSimpleName();
    public static final String KEY_PASSWORD = "pwx";

    @SerializedName("data")
    private Data data;
    @SerializedName("meta")
    private Meta meta;

    public AuthResponse() {
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public static class Data {
        @SerializedName("type")
        private String type;
        @SerializedName("user")
        private User user;
        @SerializedName("role")
        private Role role;
        @SerializedName("role_options")
        private List<RoleOption> roleOptions;

        public Data() {
        }

        public String getType() {
            return type;
        }

        public User getUser() {
            return user;
        }

        public Role getRole() {
            return role;
        }

        public List<RoleOption> getRoleOptions() {
            return roleOptions;
        }

        public static class User {
            @SerializedName("usms_id")
            private int userId;
            @SerializedName("usms_name")
            private String userName;
            @SerializedName("usms_email")
            private String userEmail;
            @SerializedName("usms_birth_date")
            private String userBirthDate;
            @SerializedName("usms_address")
            private String userAddress;
            @SerializedName("usms_gender")
            private String userGender;
            @SerializedName("usms_phone")
            private String userPhone;
            @SerializedName("usms_pcms_id")
            private String postCode;

            public User() {
            }

            public int getUserId() {
                return userId;
            }

            public String getUserName() {
                return userName;
            }

            public String getUserEmail() {
                return userEmail;
            }

            public String getUserBirthDate() {
                return userBirthDate;
            }

            public String getUserAddress() {
                return userAddress;
            }

            public String getUserGender() {
                return userGender;
            }

            public String getUserPhone() {
                return userPhone;
            }

            public String getPostCode() {
                return postCode;
            }
        }

        public static class Role{
            @SerializedName("rlms_id")
            private int roleId;
            @SerializedName("rlms_key")
            private String role_key;
            @SerializedName("rlms_name")
            private String role_name;
            @SerializedName("rlms_desc")
            private String role_desc;
            @SerializedName("usrl_id")
            private int urserRoleId;
            @SerializedName("usrl_enabled")
            private String userRoleEnabled;
            @SerializedName("usrl_level")
            private String userLevel;
            @SerializedName("csms_id")
            private int siteId;
            @SerializedName("csms_name")
            private String siteName;
            @SerializedName("drms_id")
            private int idLobby;
            @SerializedName("drms_name")
            private String lobbyName;

            public Role() {
            }

            public int getRoleId() {
                return roleId;
            }

            public String getRole_key() {
                return role_key;
            }

            public String getRole_name() {
                return role_name;
            }

            public String getRole_desc() {
                return role_desc;
            }

            public int getUrserRoleId() {
                return urserRoleId;
            }

            public String getUserRoleEnabled() {
                return userRoleEnabled;
            }

            public int getSiteId() {
                return siteId;
            }

            public String getSiteName() {
                return siteName;
            }

            public int getIdLobby() {
                return idLobby;
            }

            public String getLobbyName() {
                return lobbyName;
            }

            public String getUserLevel() {
                return userLevel;
            }
        }

        public static class RoleOption {
            @SerializedName("usrl_id")
            private int userRoleId;
            @SerializedName("usrl_enabled")
            private String userRoleEnabled;
            @SerializedName("csms_id")
            private int siteId;
            @SerializedName("csms_name")
            private String siteName;
            @SerializedName("rlms_id")
            private int roleMasterId;
            @SerializedName("rlms_name")
            private String roleMasterName;
            @SerializedName("drms_id")
            private int idLobby;
            @SerializedName("drms_name")
            private String lobbyName;

            public int getUserRoleId() {
                return userRoleId;
            }

            public String getUserRoleEnabled() {
                return userRoleEnabled;
            }

            public int getSiteId() {
                return siteId;
            }

            public String getSiteName() {
                return siteName;
            }

            public int getRoleMasterId() {
                return roleMasterId;
            }

            public String getRoleMasterName() {
                return roleMasterName;
            }

            public int getIdLobby() {
                return idLobby;
            }

            public String getLobbyName() {
                return lobbyName;
            }
        }
    }

    public static class Meta {
        @SerializedName("token")
        private String token;
        @SerializedName("token_lifetime")
        private String expiredDate;

        public String getToken() {
            return token;
        }

        public String getExpiredDate() {
            return expiredDate;
        }
    }

    public class MetaContainer {

        @SerializedName("data")
        private Data data;

        public Data getData() {
            return data;
        }

        public class Data {
            @SerializedName("meta")
            private Meta meta;

            public Meta getMeta() {
                return meta;
            }
        }
    }


}
