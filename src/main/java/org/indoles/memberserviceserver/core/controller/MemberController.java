package org.indoles.memberserviceserver.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.dto.request.*;
import org.indoles.memberserviceserver.core.dto.response.RefundResponse;
import org.indoles.memberserviceserver.core.dto.response.SignInfoRequest;
import org.indoles.memberserviceserver.core.dto.response.SignInResponse;
import org.indoles.memberserviceserver.core.dto.response.TransferPointResponse;
import org.indoles.memberserviceserver.core.service.MemberService;
import org.indoles.memberserviceserver.core.service.PointService;
import org.indoles.memberserviceserver.global.util.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PointService pointService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 API
     */

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignUpRequest request) {
        memberService.signUp(request);

        return ResponseEntity.ok()
                .build();
    }

    /**
     * 로그인 API
     */

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signin(@RequestBody SignInRequest request) {
        SignInfoRequest signInfoRequest = memberService.signIn(request);
        String accessToken = jwtTokenProvider.createAccessToken(signInfoRequest);
        String refreshToken = jwtTokenProvider.createRefreshToken(signInfoRequest.id());

        SignInResponse signInResponse = new SignInResponse(signInfoRequest.role(), accessToken, refreshToken);
        return ResponseEntity.ok(signInResponse);
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
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
    }

    /**
     * 포인트 충전 API
     */

    @PostMapping("/points/charge")
    public ResponseEntity<Void> chargePoint(@RequestHeader("Authorization") String authorizationHeader,
                                            @RequestBody MemberChargePointRequest memberChargePointRequest) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInfoRequest memberInfo = jwtTokenProvider.getSignInInfoFromToken(token);
                pointService.chargePoint(memberInfo, memberChargePointRequest.amount());
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                log.error("Error during chargePoint: {}", e.getMessage());
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            return ResponseEntity.status(UNAUTHORIZED).build();
        }
    }

    /**
     * Access Token 재발급 API
     */
    @PostMapping("/refresh")
    public ResponseEntity<SignInResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        Long userId = jwtTokenProvider.validateRefreshToken(refreshToken);
        if (userId == null) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        Role role = jwtTokenProvider.getRoleFromToken(refreshToken);
        if (role == null) {
            return ResponseEntity.status(UNAUTHORIZED).build();
        }

        // 새로운 액세스 토큰 생성
        SignInfoRequest signInfoRequest = new SignInfoRequest(userId, role);
        String accessToken = jwtTokenProvider.createAccessToken(signInfoRequest);

        //기존 리프레시 토큰 재갱신
        jwtTokenProvider.invalidateRefreshToken(refreshToken);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        SignInResponse signInResponse = new SignInResponse(signInfoRequest.role(), accessToken, newRefreshToken);
        return ResponseEntity.ok(signInResponse);
    }

    /**
     * 경매 서버 - 입찰 시 포인트 전송을 위한 API
     */
    @PostMapping("/points/transfer")
    public ResponseEntity<TransferPointResponse> transferPoint(@RequestHeader("Authorization") String authorizationHeader,
                                                               @RequestBody TransferPointRequest transferPointRequest) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInfoRequest memberInfo = jwtTokenProvider.getSignInInfoFromToken(token);
                pointService.pointTransfer(memberInfo.id(), transferPointRequest.receiverId(), transferPointRequest.amount());

                Long remainingPoints = pointService.getRemainingPoints(memberInfo.id());
                TransferPointResponse response = new TransferPointResponse(memberInfo.id(), transferPointRequest.receiverId(), transferPointRequest.amount(), remainingPoints);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                log.error("Error during transferPoint: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * 경매 서버 - 환불 시 포인트 환불을 위한 API
     */
    @PostMapping("/points/refund")
    public ResponseEntity<RefundResponse> refundPoint(@RequestHeader("Authorization") String authorizationHeader,
                                                      @RequestBody RefundRequest refundRequest) {

        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
            try {
                SignInfoRequest memberInfo = jwtTokenProvider.getSignInInfoFromToken(token);
                pointService.refundPoint(memberInfo.id(), refundRequest.receiverId(), refundRequest.amount());

                Long remainingPoints = pointService.getRemainingPoints(memberInfo.id());
                RefundResponse response = new RefundResponse(memberInfo.id(), refundRequest.receiverId(), refundRequest.amount(), remainingPoints);
                return ResponseEntity.ok(response);

            } catch (Exception e) {
                log.error("Error during refundPoint: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            log.error("Unauthorized: JWT validation failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
