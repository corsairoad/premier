package valet.digikom.com.valetparking.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DIGIKOM-EX4 on 1/29/2017.
 */

public class AuthResponse {

    public static final String KEY = AuthResponse.class.getSimpleName();
    public static final String KEY_PASSWORD = "pwx";

    @SerializedName("data")
    private Data data;

    public AuthResponse() {
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        @SerializedName("type")
        private String type;
        @SerializedName("user")
        private User user;
        @SerializedName("role")
        private Role role;

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
        }
    }
}
