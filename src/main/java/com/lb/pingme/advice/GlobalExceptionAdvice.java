package com.lb.pingme.advice;

import com.lb.pingme.common.bean.APIResponseBean;
import com.lb.pingme.common.bean.APIResponseBeanUtil;
import com.lb.pingme.common.enums.APIErrorCommonEnum;
import com.lb.pingme.common.exception.AuthException;
import com.lb.pingme.common.exception.BusinessException;
import com.lb.pingme.common.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<APIResponseBean> exceptionHandler(HttpServletRequest request, Exception e) {
        log.error("异常详情信息：", e);
        if (e instanceof AuthException) {
            APIResponseBean apiResponseBean = APIResponseBeanUtil.error(((AuthException) e).getCode(), e.getMessage());
            return new ResponseEntity(apiResponseBean, HttpStatus.OK);
        }
        if (e instanceof IllegalArgumentException) {
            APIResponseBean apiResponseBean = APIResponseBeanUtil.error(500, e.getMessage());
            return new ResponseEntity(apiResponseBean, HttpStatus.OK);
        } else if (e instanceof BusinessException) {
            APIResponseBean apiResponseBean = APIResponseBeanUtil.error(((BusinessException) e).getCode(), e.getMessage());
            return new ResponseEntity(apiResponseBean, HttpStatus.OK);
        } else if (e instanceof NotFoundException) {
            APIResponseBean apiResponseBean = APIResponseBeanUtil.error(((NotFoundException) e).getCode(), e.getMessage());
            return new ResponseEntity(apiResponseBean, HttpStatus.OK);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            APIResponseBean apiResponseBean = APIResponseBeanUtil.error(500, e.getMessage());
            return new ResponseEntity(apiResponseBean, HttpStatus.OK);
        } else if (e instanceof MethodArgumentNotValidException) {
            /***
             * 处理@Validated参数校验失败异常
             */
            APIResponseBean apiResponseBean = APIResponseBeanUtil.error(APIErrorCommonEnum.VALID_EXCEPTION.getCode(), "参数校验异常");
            return new ResponseEntity(apiResponseBean, HttpStatus.OK);
        } else if (e instanceof HttpMessageConversionException) {
            APIResponseBean apiResponseBean = APIResponseBeanUtil.error(APIErrorCommonEnum.VALID_EXCEPTION.getCode(), "参数转换异常");
            return new ResponseEntity(apiResponseBean, HttpStatus.OK);
        }
        APIResponseBean apiResponseBean = APIResponseBeanUtil.error(500, "服务端繁忙");
        return new ResponseEntity(apiResponseBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
