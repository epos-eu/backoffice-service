package org.epos.backoffice.exception;

public class RouterException extends Exception {

    public RouterException() {
    }

    public RouterException(String message) {
        super("RouterException\n" + message);
    }
}
