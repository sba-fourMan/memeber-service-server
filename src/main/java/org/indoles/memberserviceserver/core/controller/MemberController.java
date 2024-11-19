package org.indoles.memberserviceserver.core.controller;

import lombok.RequiredArgsConstructor;
import org.indoles.memberserviceserver.core.controller.interfaces.Login;
import org.indoles.memberserviceserver.core.controller.interfaces.PublicAccess;
import org.indoles.memberserviceserver.core.controller.interfaces.Roles;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.dto.request.*;
import org.indoles.memberserviceserver.core.dto.response.RefundResponse;
import org.indoles.memberserviceserver.core.dto.request.SignInfoRequest;
import org.indoles.memberserviceserver.core.dto.response.SignInResponse;
import org.indoles.memberserviceserver.core.dto.response.TransferPointResponse;
import org.indoles.memberserviceserver.core.service.MemberService;
import org.indoles.memberserviceserver.core.service.PointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PointService pointService;

    /**
     * 회원가입 API
     */

    @PublicAccess
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

    @PublicAccess
    @PostMapping("/signin")
    public ResponseEntity<SignInResponse> signin(
            @RequestBody SignInRequest request
    ) {
        SignInResponse signInResponse = memberService.signIn(request);
        return ResponseEntity.ok(signInResponse);
    }

    /**
     * 로그아웃 API
     */

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(
            @Login SignInfoRequest signInfoRequest
    ) {
        return ResponseEntity.ok().build();
    }

    /**
     * 포인트 충전 API
     */

    @Roles({Role.BUYER, Role.SELLER})
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
    @Roles({Role.BUYER, Role.SELLER})
    @PostMapping("/points/transfer")
    public ResponseEntity<TransferPointResponse> transferPoint(
            @Login SignInfoRequest signInfoRequest,
            @RequestBody TransferPointRequest transferPointRequest
    ) {
        long senderId = signInfoRequest.id();

        pointService.pointTransfer(senderId, transferPointRequest.receiverId(), transferPointRequest.amount());
        Long remainingPoints = pointService.getRemainingPoints(senderId);

        TransferPointResponse response = new TransferPointResponse(
                senderId, transferPointRequest.receiverId(),
                transferPointRequest.amount(),
                remainingPoints
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 경매 서버 - 환불 시 포인트 환불을 위한 API
     */
    @Roles({Role.BUYER, Role.SELLER})
    @PostMapping("/points/refund")
    public ResponseEntity<RefundResponse> refundPoint(
            @Login SignInfoRequest signInfoRequest,
            @RequestBody RefundRequest refundRequest
    ) {
        pointService.refundPoint(signInfoRequest.id(), refundRequest.receiverId(), refundRequest.amount());
        Long remainingPoints = pointService.getRemainingPoints(signInfoRequest.id());

        RefundResponse response = new RefundResponse(
                signInfoRequest.id(),
                refundRequest.receiverId(),
                refundRequest.amount(),
                remainingPoints);

        return ResponseEntity.ok(response);
    }
}
