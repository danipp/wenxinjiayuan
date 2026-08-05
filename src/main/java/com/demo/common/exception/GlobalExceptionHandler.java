package com.demo.common.exception;

import com.demo.common.core.result.Result;
import com.demo.common.core.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理器
 * [新增 2026-07-31 16:19] 增加 MethodArgumentNotValidException 处理，提取校验注解的 message 作为前端提示
 */
@RestControllerAdvice(basePackages = "com.demo")
@Slf4j
public class GlobalExceptionHandler {


    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({RuntimeException.class})
    public <T> Result<T> handleRuntimeException(RuntimeException e) {
        log.error("兜底运行时异常，异常原因： "+e.getMessage(), e);
        return Result.failed(ResultCode.SYSTEM_EXECUTION_ERROR,e.getMessage());
    }

    /**
     * [新增 2026-07-31 16:19] 处理 @Valid 参数校验失败异常
     * 只提取校验注解的 message（如"活动类型不能为空"），避免把冗长的异常堆栈信息返回给前端
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public <T> Result<T> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 取第一个字段的校验错误信息，拼接所有错误字段的消息用分号分隔
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message;
        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            // 多个字段校验失败时，拼接所有错误消息
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fieldErrors.size(); i++) {
                if (i > 0) {
                    sb.append("；");
                }
                sb.append(fieldErrors.get(i).getDefaultMessage());
            }
            message = sb.toString();
        } else {
            message = "参数校验失败";
        }
        log.warn("参数校验失败：{}", message);
        return Result.failed(ResultCode.PARAM_VALIDATE_FAILED, message);
    }
    
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({Exception.class})
    public <T> Result<T> handleException(Exception e) {
        log.error("兜底所有异常，异常原因:"+e.getMessage(), e);
        return Result.failed(ResultCode.SYSTEM_EXECUTION_ERROR,e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({BizException.class})
    public <T> Result<T> handleBizException(BizException e) {
        log.error("兜底业务异常，异常原因：" +e.getMessage(),e);
        return e.getResultCode() != null ? Result.failed(e.getResultCode()) : Result.failed(e.getMessage());
    }
}
