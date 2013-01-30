package net.kkolyan.utils.benchme.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface View {
	String displayName();
	String cellKey();
    String rowsBy();
    String columnsBy() default "";
    String tablesBy() default "";
}
