package org.indoles.memberserviceserver.core.dto.request;

public record RefreshTokenRequest() {
    private static String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }
}
