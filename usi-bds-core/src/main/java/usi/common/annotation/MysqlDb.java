package usi.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 区别各数据库的实现类的注解
 * @author zhang.dechang
 * @date 2015年3月30日 上午10:06:00
 * 
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MysqlDb {
	String value() default "";
}
