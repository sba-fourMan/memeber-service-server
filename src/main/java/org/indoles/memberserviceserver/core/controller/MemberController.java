package org.indoles.memberserviceserver.core.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.indoles.memberserviceserver.core.controller.interfaces.Login;
import org.indoles.memberserviceserver.core.dto.request.*;
import org.indoles.memberserviceserver.core.dto.response.RefundResponse;
import org.indoles.memberserviceserver.core.dto.response.SignInfoRequest;
import org.indoles.memberserviceserver.core.dto.response.SignInResponse;
import org.indoles.memberserviceserver.core.dto.response.TransferPointResponse;
import org.indoles.memberserviceserver.core.service.MemberService;
import org.indoles.memberserviceserver.core.service.PointService;
import org.indoles.memberserviceserver.global.util.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> signup(
            @RequestBody SignUpRequest request
    ) {
        memberService.signUp(request);

        return ResponseEntity.ok()
                .build();
    }

    /**
     * 로그인 API
     */

    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signin(
            @RequestBody SignInRequest request
    ) {
        SignInfoRequest signInfoRequest = memberService.signIn(request);
        String accessToken = jwtTokenProvider.createAccessToken(signInfoRequest);
        String refreshToken = jwtTokenProvider.createRefreshToken(signInfoRequest.id(), signInfoRequest.role());

        SignInResponse signInResponse = new SignInResponse(signInfoRequest.role(), accessToken, refreshToken);
        return ResponseEntity.ok(signInResponse);
    }

    /**
     * 로그아웃 API
     */

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(
            @Login SignInfoRequest signInfoRequest
    ) {

        log.info("User signed out: {}", signInfoRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * 포인트 충전 API
     */

    @PostMapping("/points/charge")
    public ResponseEntity<Void> chargePoint(
            @Login SignInfoRequest signInfoRequest,
            @RequestBody MemberChargePointRequest memberChargePointRequest
    ) {
        pointService.chargePoint(signInfoRequest, memberChargePointRequest.amount());

        return ResponseEntity.ok().build();
    }

    /**
     * 경매 서버 - 입찰 시 포인트 전송을 위한 API
     */
    @PostMapping("/points/transfer")
    public ResponseEntity<TransferPointResponse> transferPoint(
            @Login SignInfoRequest signInfoRequest,
            @RequestBody TransferPointRequest transferPointRequest
    ) {
        pointService.pointTransfer(signInfoRequest.id(), transferPointRequest.receiverId(), transferPointRequest.amount());
        Long remainingPoints = pointService.getRemainingPoints(signInfoRequest.id());
        TransferPointResponse response = new TransferPointResponse(signInfoRequest.id(), transferPointRequest.receiverId(), transferPointRequest.amount(), remainingPoints);
        return ResponseEntity.ok(response);
    }

    /**
     * 경매 서버 - 환불 시 포인트 환불을 위한 API
     */
    @PostMapping("/points/refund")
    public ResponseEntity<RefundResponse> refundPoint(
            @Login SignInfoRequest signInfoRequest,
            @RequestBody RefundRequest refundRequest
    ) {
        pointService.refundPoint(signInfoRequest.id(), refundRequest.receiverId(), refundRequest.amount());

        Long remainingPoints = pointService.getRemainingPoints(signInfoRequest.id());
        RefundResponse response = new RefundResponse(signInfoRequest.id(), refundRequest.receiverId(), refundRequest.amount(), remainingPoints);

        return ResponseEntity.ok(response);
    }
}
