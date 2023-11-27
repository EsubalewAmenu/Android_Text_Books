package com.herma.apps.textbooks.common;

import java.util.Base64;
import java.util.Date;

public class TokenUtils {
    public static boolean isTokenExpired(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid token format");
        }

        String claimsBase64 = parts[1];
        byte[] claimsBytes = Base64.getDecoder().decode(claimsBase64);
        String claims = new String(claimsBytes);

        // Parse the claims JSON to extract the expiration time
        long expirationTime;
        try {
            expirationTime = Long.parseLong(getClaimValue(claims));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid expiration time");
        }

        long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds
        return currentTime > expirationTime;
    }

    private static String getClaimValue(String claims) {
        String claimName = "exp";
        String claimPrefix = "\"" + claimName + "\":";
        int startIndex = claims.indexOf(claimPrefix);
        if (startIndex == -1) {
            throw new IllegalArgumentException("Claim not found: " + claimName);
        }

        startIndex += claimPrefix.length();
        int endIndex = claims.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = claims.indexOf("}", startIndex);
        }

        return claims.substring(startIndex, endIndex).replaceAll("\"", "").trim();
    }

}
