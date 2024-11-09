package org.indoles.memberserviceserver.domain;

import lombok.Getter;
import org.indoles.memberserviceserver.domain.validate.ValidatePoint;

@Getter
public class Point {

    private Long amount;
    private final ValidatePoint validatePoint = new ValidatePoint();

    public Point(Long amount) {
        validatePoint.validatePositiveAmount(amount);
        this.amount = amount;
    }

    public void minus(Long minusAmount) {
        validatePoint.validatePositiveAmount(minusAmount);
        validatePoint.validateMinusPoint(amount, minusAmount);
        amount -= minusAmount;
    }

    public void plus(Long price) {
        validatePoint.validatePositiveAmount(price);
        validatePoint.validatePlusPoint(amount, price);
        amount += price;
    }
}
