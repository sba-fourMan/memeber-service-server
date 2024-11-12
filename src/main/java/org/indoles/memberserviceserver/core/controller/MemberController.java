package org.indoles.memberserviceserver.core.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.indoles.memberserviceserver.core.controller.interfaces.Login;
import org.indoles.memberserviceserver.core.controller.interfaces.Roles;
import org.indoles.memberserviceserver.core.dto.*;
import org.indoles.memberserviceserver.core.domain.enums.Role;
import org.indoles.memberserviceserver.core.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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
    public ResponseEntity<SignInResponseInfo> signin(@RequestBody SignInRequestInfo request, HttpSession session) {
        SignInInfo signInInfo = memberService.signIn(request);
        session.setAttribute("signInMember", signInInfo);

        SignInResponseInfo signInResponseInfo = new SignInResponseInfo(signInInfo.role());
        return ResponseEntity.ok(signInResponseInfo);
    }

    /**
     * 로그아웃 API
     */

    @PostMapping("/signout")
    public ResponseEntity<Void> signout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    /**
     * 포인트 충전 API
     */

    @Roles({Role.BUYER, Role.SELLER})
    @PostMapping("/points/charge")
    public ResponseEntity<Void> chargePoint(@Login SignInInfo memberInfo,
                                            @RequestBody MemberChargePointCommand command) {
        memberService.chargePoint(memberInfo, command.amount());
        return ResponseEntity.ok().build();
    }
}
