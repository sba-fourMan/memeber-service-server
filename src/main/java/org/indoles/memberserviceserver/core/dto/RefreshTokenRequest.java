package org.indoles.memberserviceserver.core.dto;

public record RefreshTokenRequest() {
    private static String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }
}
