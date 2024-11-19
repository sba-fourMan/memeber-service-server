package org.indoles.memberserviceserver.core.dto.request;


import java.util.Objects;

import static org.indoles.memberserviceserver.core.dto.validateDto.ValidateMemberDto.validateNotNull;
import static org.indoles.memberserviceserver.core.dto.validateDto.ValidateMemberDto.validateSignUpId;

public record SignUpRequest(
        String signUpId,
        String password,
        String userRole
) {
    private static final String ERROR_ID_IS_BLANK = "아이디는 빈칸 또는 공백일 수 없습니다.";

    public SignUpRequest {
        validateNotNull(signUpId, "회원가입 ID");
        validateNotNull(password, "회원가입 패스워드");
        validateNotNull(userRole, "사용자 역할");
        validateSignUpId(signUpId);
    }
}

