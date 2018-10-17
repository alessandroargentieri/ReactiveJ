package com.mawashi.nio.utils;

/**
 * @author alessandroargentieri
 *
 * This pojo class represents each endpoint
 * then associate together a url path to an action corresponding to it
 */
public class Endpoint {
    private final String path;
    private final Action action;

    public Endpoint(String path, Action action) {
        this.path = path;
        this.action = action;
    }

    public synchronized String getPath() {
        return path;
    }

    public synchronized Action getAction() {
        return action;
    }
}
