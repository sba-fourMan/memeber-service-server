package org.indoles.memberserviceserver.domain;

import lombok.Getter;
import org.indoles.memberserviceserver.domain.validate.ValidatePoint;
import org.indoles.memberserviceserver.entity.exception.MemberException;
import org.indoles.memberserviceserver.entity.exception.MemberExceptionCode;

@Getter
public class Point {

    private Long amount;
    private final ValidatePoint validator = new ValidatePoint();

    public Point(Long amount) {
        validator.validatePositiveAmount(amount);
        this.amount = amount;
    }

    public void minus(Long minusAmount) {
        validator.validatePositiveAmount(minusAmount);
        validator.validateMinusPoint(amount, minusAmount);
        amount -= minusAmount;
    }

    public void plus(Long price) {
        validator.validatePositiveAmount(price);
        validator.validatePlusPoint(amount, price);
        amount += price;
    }
}
