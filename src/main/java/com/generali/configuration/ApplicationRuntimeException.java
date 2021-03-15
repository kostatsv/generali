package com.generali.configuration;

public class ApplicationRuntimeException extends RuntimeException{

    public ApplicationRuntimeException(String message) {
        super(message);
    }

    public ApplicationRuntimeException(String message, Exception ex) {
        super(message, ex);
    }

}
