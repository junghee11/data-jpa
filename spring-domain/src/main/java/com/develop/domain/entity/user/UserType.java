package com.develop.domain.entity.user;

public class UserType {

    public enum Role {
        GENERAL,
        DEVELOPER,
        ADMIN,
        SUPPORTERS,
        DORMANT,
        WITHDRAWAL;

        public static final String[] GENERAL_ROLE = {GENERAL.name(), SUPPORTERS.name()};

    }

}
