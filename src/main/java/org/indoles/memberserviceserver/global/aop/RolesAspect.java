package org.indoles.memberserviceserver.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.indoles.memberserviceserver.core.controller.interfaces.Roles;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.dto.request.SignInfoRequest;
import org.indoles.memberserviceserver.global.exception.AuthorizationException;
import org.indoles.memberserviceserver.global.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RolesAspect {

    @Around("@annotation(roles)")
    public Object checkRoles(ProceedingJoinPoint joinPoint, Roles roles) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthorizationException("API 접근 권한이 없습니다.", ErrorCode.AU00);
        }

        SignInfoRequest signInfoRequest = (SignInfoRequest) authentication.getPrincipal();
        Role userRole = signInfoRequest.role();

        boolean hasRole = false;

        for (Role requiredRole : roles.value()) {
            if (userRole.equals(requiredRole)) {
                hasRole = true;
                break;
            }
        }

        if (!hasRole) {
            if (userRole.equals(Role.SELLER)) {
                throw new AuthorizationException("판매자만 요청할 수 있는 경로(API) 입니다.", ErrorCode.AU01);
            } else if (userRole.equals(Role.BUYER)) {
                throw new AuthorizationException("구매자만 요청할 수 있는 경로(API) 입니다.", ErrorCode.AU02);
            } else {
                throw new AuthorizationException("API 접근 권한이 없습니다.", ErrorCode.AU00);
            }
        }

        return joinPoint.proceed();
    }
}
