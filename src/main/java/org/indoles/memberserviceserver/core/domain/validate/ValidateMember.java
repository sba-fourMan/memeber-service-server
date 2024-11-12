package org.indoles.memberserviceserver.core.domain.validate;

import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.indoles.memberserviceserver.global.exception.ErrorCode;

import java.util.regex.Pattern;

public class ValidateMember {

    private static final int MINIMUM_ID_LENGTH = 2;
    private static final int MAXIMUM_ID_LENGTH = 20;
    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MAXIMUM_PASSWORD_LENGTH = 20;
    private static final Pattern PASSWORD_DIGIT_REQUIRED = Pattern.compile(".*[0-9].*");
    private static final Pattern PASSWORD_LOWERCASE_REQUIRED = Pattern.compile(".*[a-z].*");
    private static final Pattern PASSWORD_VALID_CHARACTERS = Pattern.compile("^[a-zA-Z0-9]*$");
    private static final String ERROR_ID_IS_BLANK = "아이디는 빈칸 또는 공백일 수 없습니다.";
    private static final String ERROR_SIGN_IN_ID_LENGTH = "아이디 길이는 %d ~ %d자 여야합니다. 현재 길이: %d";
    private static final String ERROR_PASSWORD_IS_BLANK = "비밀번호는 빈칸 또는 공백일 수 없습니다.";
    private static final String ERROR_PASSWORD_LENGTH = "비밀번호는 %d자 이상 %d자 이하로 입력해주세요. 현재 길이=%d";
    private static final String ERROR_PASSWORD_DIGIT_REQUIRED = "비밀번호는 숫자가 반드시 포함되어야 합니다.";
    private static final String ERROR_PASSWORD_LOWERCASE_REQUIRED = "비밀번호는 알파벳 소문자가 반드시 포함되어야 합니다.";
    private static final String ERROR_PASSWORD_VALID_CHARACTERS = "비밀번호는 영문자와 숫자만 사용할 수 있습니다.";


    /**
     * 아이디 유효성 검사
     */

    public void validateSignInId(String signInId) {
        if (signInId == null || signInId.isBlank()) {
            throw new BadRequestException(ERROR_ID_IS_BLANK, ErrorCode.M004);
        }

        if (signInId.length() < MINIMUM_ID_LENGTH || signInId.length() > MAXIMUM_ID_LENGTH) {
            throw new BadRequestException(
                    String.format(ERROR_SIGN_IN_ID_LENGTH, MINIMUM_ID_LENGTH, MAXIMUM_ID_LENGTH, signInId.length()),
                    ErrorCode.M005);
        }
    }

    /**
     * 비밀번호 유효성 검사
     */

    public void validateSignInPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BadRequestException(ERROR_PASSWORD_IS_BLANK, ErrorCode.M006);
        }

        if (password.length() < MINIMUM_PASSWORD_LENGTH || password.length() > MAXIMUM_PASSWORD_LENGTH) {
            throw new BadRequestException(
                    String.format(ERROR_PASSWORD_LENGTH, MINIMUM_PASSWORD_LENGTH, MAXIMUM_PASSWORD_LENGTH,
                            password.length()), ErrorCode.M007);
        }

        if (!PASSWORD_DIGIT_REQUIRED.matcher(password).matches()) {
            throw new BadRequestException(ERROR_PASSWORD_DIGIT_REQUIRED, ErrorCode.M008);
        }

        if (!PASSWORD_LOWERCASE_REQUIRED.matcher(password).matches()) {
            throw new BadRequestException(ERROR_PASSWORD_LOWERCASE_REQUIRED, ErrorCode.M009);
        }

        if (!PASSWORD_VALID_CHARACTERS.matcher(password).matches()) {
            throw new BadRequestException(ERROR_PASSWORD_VALID_CHARACTERS, ErrorCode.M010);
        }
    }
}
