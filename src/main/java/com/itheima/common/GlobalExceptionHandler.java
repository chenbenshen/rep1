package com.itheima.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常代理
 */
@ControllerAdvice(annotations={RestController.class, Controller.class})
@ResponseBody//这个是要返回Jsion数据的
//上面两个等于@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex)
    {
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry"))
        {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
    /**
     * 捕获自己定义的异常
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex)
    {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
