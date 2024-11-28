package org.indoles.memberserviceserver.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.indoles.memberserviceserver.core.controller.interfaces.Buyer;
import org.indoles.memberserviceserver.core.controller.interfaces.PublicAccess;
import org.indoles.memberserviceserver.core.controller.interfaces.Seller;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.dto.request.SignInfoRequest;
import org.indoles.memberserviceserver.global.exception.AuthorizationException;
import org.indoles.memberserviceserver.global.exception.ErrorCode;
import org.indoles.memberserviceserver.global.util.JwtTokenProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationContext authenticationContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getRequestURI().equals("/members/points/transfer") ||
                request.getRequestURI().equals("/members/points/refund")) {
            return true;
        }

        if (handler instanceof ResourceHttpRequestHandler || CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(PublicAccess.class)) {
                return true;
            }
        }

        String token = extractToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            SignInfoRequest signInfoRequest = jwtTokenProvider.getSignInInfoFromToken(token);
            authenticationContext.setPrincipal(signInfoRequest);

            authorize(handler, signInfoRequest);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing JWT token");
            return false;
        }

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void authorize(Object handler, SignInfoRequest signInfoRequest) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        if (handlerMethod.hasMethodAnnotation(Buyer.class) && !signInfoRequest.isType(Role.BUYER)) {
            throw new AuthorizationException("구매자만 요청할 수 있는 경로(API) 입니다.", ErrorCode.AU02);
        }

        if (handlerMethod.hasMethodAnnotation(Seller.class) && !signInfoRequest.isType(Role.SELLER)) {
            throw new AuthorizationException("판매자만 요청할 수 있는 경로(API) 입니다.", ErrorCode.AU01);
        }
    }
}
