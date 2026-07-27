package com.education.store.common.exception;

import com.education.store.common.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /** 参数校验异常（表单绑定） */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败(Bind): {}", message);
        return Result.badRequest(message);
    }

    /** JSON 请求体校验异常（@Valid @RequestBody） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败(JSON): {}", message);
        return Result.badRequest(message);
    }

    /** 请求体解析失败（JSON 格式错误/为空） */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败: {}", e.getMessage());
        return Result.badRequest("请求体格式错误或为空，请检查JSON格式");
    }

    /** HTTP 方法不支持（GET 访问 POST 接口等） */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP方法不支持: {}", e.getMessage());
        String[] methods = e.getSupportedMethods();
        String supported = methods != null ? String.join("/", methods) : "未知";
        return Result.error(405, "请求方法 " + e.getMethod() + " 不支持，请使用 " + supported);
    }

    /** 缺少请求参数 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.badRequest("缺少必要参数: " + e.getParameterName());
    }

    /** 参数类型不匹配（PathVariable/RequestParam类型转换失败） */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "正确类型";
        log.warn("参数类型不匹配: {}={}, 期望{}", paramName, e.getValue(), requiredType);
        return Result.badRequest("参数 " + paramName + " 格式不正确，请输入" + getTypeName(requiredType));
    }

    /** 方法级参数校验异常（@RequestParam/@PathVariable上的注解） */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败(Constraint): {}", message);
        return Result.badRequest(message);
    }

    /** 数字格式异常 */
    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleNumberFormat(NumberFormatException e, HttpServletRequest request) {
        log.warn("数字格式异常 [{}] {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return Result.badRequest("参数格式错误，请输入正确的数字");
    }

    /** 非法参数异常 */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数 [{}] {}: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        String msg = e.getMessage();
        return Result.badRequest(msg != null && !msg.isEmpty() ? msg : "参数不合法");
    }

    /** 空指针异常 - 友好兜底 */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleNullPointer(NullPointerException e, HttpServletRequest request) {
        log.error("空指针异常 [{}] {}: ", request.getMethod(), request.getRequestURI(), e);
        return Result.error("服务器内部错误，请稍后重试");
    }

    /** 数据库访问异常 */
    @ExceptionHandler({DataAccessException.class, SQLException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleDataAccess(Exception e, HttpServletRequest request) {
        log.error("数据库异常 [{}] {}: ", request.getMethod(), request.getRequestURI(), e);
        return Result.error("数据访问异常，请稍后重试");
    }

    /** 其他未处理异常 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(HttpServletRequest request, Exception e) {
        log.error("系统异常 [{}] {}: ", request.getMethod(), request.getRequestURI(), e);
        return Result.error("服务器内部错误，请联系管理员");
    }

    private String getTypeName(String type) {
        return switch (type) {
            case "Long", "long", "Integer", "int" -> "数字";
            case "Double", "double", "Float", "float", "BigDecimal" -> "数值";
            case "Boolean", "boolean" -> "true/false";
            default -> type;
        };
    }
}
