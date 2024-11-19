package org.indoles.memberserviceserver.core.dto.validateDto;

import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.indoles.memberserviceserver.global.exception.ErrorCode;

public class ValidateMemberDto {

    public static final String ERROR_NULL_VALUE = "%s는 Null일 수 없습니다.";
    private static final String ERROR_ID_IS_BLANK = "아이디는 빈칸 또는 공백일 수 없습니다.";


    /**
     * 경매 입찰 시 사용되는 DTO의 유효성 검사
     */

    public static void validateReceiverId(Long receiverId) {
        if (receiverId == null || receiverId <= 0) {
            throw new BadRequestException("수신할 판매자의 ID를 찾을 수 없습니다.", ErrorCode.P0010);
        }
    }

    public static void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new BadRequestException("포인트 전송 금액은 null이거나 0보다 작을 수 없습니다.", ErrorCode.P009);
        }
    }

    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(String.format(ERROR_NULL_VALUE, fieldName), ErrorCode.G000);
        }
    }

    public static void validateSignUpId(String signUpId) {
        if (signUpId.isBlank()) {
            throw new BadRequestException(ERROR_ID_IS_BLANK, ErrorCode.M004);
        }
    }
}
