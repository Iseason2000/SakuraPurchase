package top.iseason.sakurapurchase.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {

    @ExceptionHandler(Throwable.class)
    public String doException(Throwable exception) {
        exception.printStackTrace();
        return "{\"Error\": \"" + exception.getMessage() + "\"}";
    }

}
