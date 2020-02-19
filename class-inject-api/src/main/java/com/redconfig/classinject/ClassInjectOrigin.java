package com.redconfig.classinject;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes annotated by this annotation will have its package added uniquely
 * into ClassInject processor.
 * <p>
 * The "root" Dagger module(s) will be generated in this package.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ClassInjectOrigin {

}
