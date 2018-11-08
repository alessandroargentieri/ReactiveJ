package reactivej.mawashi.nio.utils;

import java.time.LocalDateTime;

/**
 * @author alessandroargentieri
 *
 * This pojo class encapsulates the error which will be sent in output in the case of failure
 */
public class Error {
    private int    code;
    private String path;
    private String description;
    private String dateTime;

    public Error() {}

    public Error(int code, String path, String description) {
        this.code = code;
        this.path = path;
        this.description = description;
        this.dateTime = LocalDateTime.now().toString();
    }
}
