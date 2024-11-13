package org.indoles.memberserviceserver.core.domain;

import lombok.Getter;
import org.indoles.memberserviceserver.core.domain.validate.ValidatePoint;

@Getter
public class Point {

    private long amount;
    private final ValidatePoint validatePoint = new ValidatePoint();

    public Point(long amount) {
        validatePoint.validatePositiveAmount(amount);
        this.amount = amount;
    }

    public void minus(long minusAmount) {
        validatePoint.validatePositiveAmount(minusAmount);
        validatePoint.validateMinusPoint(amount, minusAmount);
        amount -= minusAmount;
    }

    public void plus(Long price) {
        validatePoint.validatePositiveAmount(price);
        validatePoint.validatePlusPoint(amount, price);
        amount += price;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Point point = (Point) o;
        return getAmount() == point.getAmount();
    }

    public Long getValue() {
        return amount;
    }
}
