package com.store.storeapp.Utils;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404() {
        return "error/errorPage";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex) {
        ex.printStackTrace();
        return "error/errorPage";
    }
}

