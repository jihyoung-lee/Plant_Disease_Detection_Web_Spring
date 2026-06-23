package com.jihyoung.plant_disease_detection_web_spring.global.exception;

import com.jihyoung.plant_disease_detection_web_spring.global.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(
            IllegalArgumentException e
    ) {
        return new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                e.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ErrorResponse handleMaxUploadSizeExceededException() {
        return new ErrorResponse(
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                "FILE_TOO_LARGE",
                "업로드 가능한 파일 크기를 초과했습니다."
        );
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(WebClientResponseException.class)
    public ErrorResponse handleWebClientResponseException(
            WebClientResponseException e
    ) {
        return new ErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                "EXTERNAL_API_ERROR",
                "외부 API 호출 중 오류가 발생했습니다."
        );
    }

    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ExceptionHandler(TimeoutException.class)
    public ErrorResponse handleTimeoutException() {
        return new ErrorResponse(
                HttpStatus.GATEWAY_TIMEOUT.value(),
                "EXTERNAL_API_TIMEOUT",
                "외부 API 응답 시간이 초과되었습니다."
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(
            Exception e
    ) {
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다."
        );

    }

}