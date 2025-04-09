package com.develop.datajpa.util;

import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class RandomCodeUtil {

    public static String generateVerificationCode() {
        Random random = new Random();
        int verificationCode = random.nextInt(888888) + 111111;
        return Integer.toString(verificationCode);
    }

}
