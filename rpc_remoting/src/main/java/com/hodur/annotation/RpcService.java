package com.hodur.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {
    /**
     * @describe
     * @author Hodur
     * @date 2021/4/15 18:19 
     * @return java.lang.String
     */
    String version() default "";
    /**
     * @describe
     * @author Hodur
     * @date 2021/4/15 19:49
 * @return java.lang.String
     */
    String group() default "";
}
