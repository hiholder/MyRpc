package com.hodur.remote.annotation;

import com.hodur.remote.spring.CustomScanner;
import com.hodur.remote.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegistrar.class)
@Inherited
public @interface RpcReference {
    /**
     * @describe 服务版本，默认值是空字符串
     * @author Hodur
     * @date 2021/4/15 16:48
     * @return java.lang.String
     */
    String version() default "";
    /**
     * @describe 服务器组，默认是空字符串
     * @author Hodur
     * @date 2021/4/15 16:48
     * @return java.lang.String
     */
    String group() default "";
}
