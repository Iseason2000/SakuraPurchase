package top.iseason.sakurapurchase.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.iseason.sakurapurchase.utils.Result;

@RestControllerAdvice
public class ProjectExceptionAdvice {

    @ExceptionHandler(Throwable.class)
    public Result<String> doException(Throwable exception) {
        exception.printStackTrace();
        return Result.failure(exception.getMessage());
    }

}
