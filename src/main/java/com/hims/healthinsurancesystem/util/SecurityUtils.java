package com.hims.healthinsurancesystem.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public final class SecurityUtils {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom rnd = new SecureRandom();
    private static final int DEFAULT_PASSWORD_LENGTH = 10;

    private SecurityUtils() {}

    public static String generateRandomPassword() {
        return generateRandomPassword(DEFAULT_PASSWORD_LENGTH);
    }

    public static String generateRandomPassword(int length) {
        if (length <= 0) {
            logger.warn("Requested password length <= 0, falling back to default {}", DEFAULT_PASSWORD_LENGTH);
            length = DEFAULT_PASSWORD_LENGTH;
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(rnd.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}

