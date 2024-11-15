package org.indoles.memberserviceserver.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.dto.*;
import org.indoles.memberserviceserver.core.service.MemberService;
import org.indoles.memberserviceserver.global.util.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 API
     */

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignUpRequestInfo request) {
        memberService.signUp(request);

        return ResponseEntity.ok()
                .build();
    }

    /**
     * 로그인 API
     */

    @PostMapping("/signin")
    public ResponseEntity<SignInResponseInfo> signin(@RequestBody SignInRequestInfo request) {
        SignInInfo signInInfo = memberService.signIn(request);
        String accessToken = jwtTokenProvider.createAccessToken(signInInfo);
        String refreshToken = jwtTokenProvider.createRefreshToken(signInInfo.id());

        SignInResponseInfo signInResponseInfo = new SignInResponseInfo(signInInfo.role(), accessToken, refreshToken);
        return ResponseEntity.ok(signInResponseInfo);
    }

    /**
     * 로그아웃 API
     */

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                jwtTokenProvider.getSignInInfoFromToken(token);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * 포인트 충전 API
     */

    @PostMapping("/points/charge")
    public ResponseEntity<Void> chargePoint(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestBody MemberChargePointCommand command) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInInfo memberInfo = jwtTokenProvider.getSignInInfoFromToken(token);
                memberService.chargePoint(memberInfo, command.amount());
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Access Token 재발급 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<SignInResponseInfo> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        Long userId = jwtTokenProvider.validateRefreshToken(refreshToken);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Role role = jwtTokenProvider.getRoleFromToken(refreshToken);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 새로운 액세스 토큰 생성
        SignInInfo signInInfo = new SignInInfo(userId, role);
        String accessToken = jwtTokenProvider.createAccessToken(signInInfo);

        //기존 리프레시 토큰 재갱신
        jwtTokenProvider.invalidateRefreshToken(refreshToken);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        SignInResponseInfo signInResponseInfo = new SignInResponseInfo(signInInfo.role(), accessToken, newRefreshToken);
        return ResponseEntity.ok(signInResponseInfo);
    }
}
