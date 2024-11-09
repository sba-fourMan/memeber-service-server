package org.indoles.memberserviceserver.domain;

import lombok.Getter;
import org.indoles.memberserviceserver.entity.exception.MemberException;
import org.indoles.memberserviceserver.entity.exception.MemberExceptionCode;

@Getter
public class Point {

    private Long amount;

    public Point(Long amount) {
        this.amount = amount;
    }

    public void validateMinusPoint(Long minusAmount) {
        if (amount < minusAmount) {
            throw new MemberException(MemberExceptionCode.POINT_NOT_ENOUGH, minusAmount);
        }
        amount -= minusAmount;
    }

    public void validatePlusPoint(Long price) {
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
