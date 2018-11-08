package reactivej.mawashi.nio.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author alessandroargentieri
 *
 * descriptive annotation for the endpoints
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Api {
    String path()        default "/";
    String method()      default "GET";
    String consumes()    default "";
    String produces()    default "plain/text";
    String description() default "";
}
