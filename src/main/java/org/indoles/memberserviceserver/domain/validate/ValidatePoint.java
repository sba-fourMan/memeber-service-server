package org.indoles.memberserviceserver.domain.validate;

import org.indoles.memberserviceserver.entity.exception.MemberException;
import org.indoles.memberserviceserver.entity.exception.MemberExceptionCode;

public class ValidatePoint {


    public void validateMinusPoint(Long amount,Long minusAmount) {
        if (amount < minusAmount) {
            throw new MemberException(MemberExceptionCode.POINT_NOT_ENOUGH, minusAmount);
        }
        amount -= minusAmount;
    }

    public void validatePlusPoint(Long amount,Long price) {
        if (price > 0 && amount > Long.MAX_VALUE - price) {
            throw new MemberException(MemberExceptionCode.POINT_OVER_MAX, price);
        }
        amount += price;
    }

    public void validatePositiveAmount(Long amount) {
        if (amount < 0) {
            throw new MemberException(MemberExceptionCode.NUMBER_NOT_POSITIVE, amount);
        }
    }
}
