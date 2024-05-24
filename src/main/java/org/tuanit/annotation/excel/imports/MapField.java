package org.tuanit.annotation.excel.imports;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MapField {//tao annotation
    String value() default "";
    boolean booleanType() default false;
}
