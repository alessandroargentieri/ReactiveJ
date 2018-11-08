package reactivej.mawashi.nio.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author alessandroargentieri
 *
 * Functional interface implemented by the programmer in the class which extends the abstract class Endpoints
 * It represents the action to which the request, response are sent by the GenericNioServlet
 */
@FunctionalInterface
public interface Action {
    void act(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
