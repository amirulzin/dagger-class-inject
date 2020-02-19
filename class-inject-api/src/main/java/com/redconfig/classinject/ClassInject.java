package com.redconfig.classinject;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated by this annotation will have its Java class added uniquely
 * into ClassInject processor.
 * <p>
 * ClassInject in modular mode supports adding public and package local classes at the cost of generating
 * Dagger modules in the same package of the annotated class. Root module(s) which automatically include
 * all these modules will be generated in the same package of the class annotated with {@link ClassInjectOrigin}.
 * <p>
 * In contrast, ClassInject in monolith mode only supports public class and all these class providers
 * will be added into the root module(s) generated for classes with {@link ClassInjectOrigin} annotation.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ClassInject {

}
