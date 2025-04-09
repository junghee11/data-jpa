package com.develop.datajpa.entity.user;

public class UserType {

    public enum Role {
        GENERAL,
        DEVELOPER,
        ADMIN,
        SUPPORTERS,
        DORMANT,
        WITHDRAWAL;

        public static final int[] GENERAL_ROLE = {GENERAL.ordinal(), SUPPORTERS.ordinal()};

    }

}
