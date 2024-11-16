package org.indoles.memberserviceserver.core.domain.enums;

import org.indoles.memberserviceserver.core.domain.validate.ValidateRole;

public enum Role {

    SELLER("판매자"),
    BUYER("입찰자(구매자");

    private final String description;

    Role(final String description) {
        this.description = description;
    }

    public static Role find(final String userRole) {
        return ValidateRole.validate(userRole);
    }
}

