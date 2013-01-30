package net.kkolyan.utils.benchme.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Parameter {

    /**
     * built-in result key. bound with {@link net.kkolyan.utils.benchme.api.Scenario#getName()} value
     */
	String SCENARIO = "Scenario";

	String name();

	int[] values();

    /**
     *
     * @return array of values to use when warming up. empty array causes usage of {@link #values()} for warming up
     */
    int[] warmUp() default {};
}
