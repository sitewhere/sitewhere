package com.sitewhere.microservice.security.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sitewhere.spi.user.SiteWhereAuthority;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface GrpcSecured {

    /**
     * List of roles required to execute method.
     * 
     * @return
     */
    public SiteWhereAuthority[] value();
}