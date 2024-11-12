package org.indoles.memberserviceserver.core.controller.interfaces;

import org.indoles.memberserviceserver.core.entity.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Roles {

    Role[] value();
}
