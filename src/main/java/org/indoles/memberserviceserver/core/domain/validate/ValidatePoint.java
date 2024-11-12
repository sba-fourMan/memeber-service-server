package org.indoles.memberserviceserver.core.domain.validate;

import org.indoles.memberserviceserver.global.exception.BadRequestException;
import org.indoles.memberserviceserver.global.exception.ErrorCode;

public class ValidatePoint {


    public void validateMinusPoint(Long amount,Long minusAmount) {
        if (amount < minusAmount) {
            throw new BadRequestException("포인트가 부족합니다.", ErrorCode.P001);
        }
        amount -= minusAmount;
    }

    public void validatePlusPoint(Long amount,Long price) {
        if (price > 0 && amount > Long.MAX_VALUE - price) {
            throw new BadRequestException("포인트가 최대치를 초과하였습니다.", ErrorCode.P006);
        }
        amount += price;
    }

    public void validatePositiveAmount(Long amount) {
        if (amount < 0) {
            throw new BadRequestException("금액은 양수여야 합니다.", ErrorCode.P008);
        }
    }
}
