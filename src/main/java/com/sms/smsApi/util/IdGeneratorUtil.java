package com.sms.smsApi.util;

public class IdGeneratorUtil {

    private IdGeneratorUtil() {

    }

    public static String generateNextId(String latestId, String prefix, int numberLength) {

        if (latestId == null || latestId.isBlank()) {
            return prefix + String.format("%0" + numberLength + "d", 1);
        }

        int seq = Integer.parseInt(latestId.substring(prefix.length()));

        // increment and format
        return prefix + String.format("%0" + numberLength + "d", seq + 1);
    }
}
