package org.indoles.memberserviceserver.controller;

import lombok.RequiredArgsConstructor;
import org.indoles.memberserviceserver.dto.request.SignInRequestInfo;
import org.indoles.memberserviceserver.dto.request.SignUpRequestInfo;
import org.indoles.memberserviceserver.service.MemberService;
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
    public ResponseEntity<Void> signIn(@RequestBody SignInRequestInfo request) {
        memberService.signIn(request);

        return ResponseEntity.ok()
                .build();
    }

    /**
     * 로그아웃 API
     */

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut() {
        return ResponseEntity.ok()
                .build();
    }
}
