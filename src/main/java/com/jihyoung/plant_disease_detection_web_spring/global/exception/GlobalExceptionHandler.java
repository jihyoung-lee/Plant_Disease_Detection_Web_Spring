package com.jihyoung.plant_disease_detection_web_spring.global.exception;

import com.jihyoung.plant_disease_detection_web_spring.global.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        return error(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                e.getConstraintViolations().stream()
                        .findFirst()
                        .map(ConstraintViolation::getMessage)
                        .orElse("잘못된 요청입니다.")
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", e.getMessage());
    }

    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ErrorResponse handleMaxUploadSizeExceededException() {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "FILE_TOO_LARGE", "업로드 가능한 파일 크기를 초과했습니다.");
    }

    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ExceptionHandler(AiTimeoutException.class)
    public ErrorResponse handleAiTimeout(AiTimeoutException e) {
        log.warn("AI 서버 응답 시간이 초과되었습니다.", e);
        return error(HttpStatus.GATEWAY_TIMEOUT, "AI_TIMEOUT", "AI 서버 응답 시간이 초과되었습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(AiServerException.class)
    public ErrorResponse handleAiServer(AiServerException e) {
        log.warn("AI 서버 호출에 실패했습니다.", e);
        return error(HttpStatus.BAD_GATEWAY, "AI_SERVER_ERROR", "AI 서버를 호출하지 못했습니다.");
    }

    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ExceptionHandler(PestApiTimeoutException.class)
    public ErrorResponse handlePestApiTimeout(PestApiTimeoutException e) {
        log.warn("병해충 API 응답 시간이 초과되었습니다.", e);
        return error(HttpStatus.GATEWAY_TIMEOUT, "PEST_API_TIMEOUT", "병해충 정보 서버의 응답 시간이 초과되었습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(PestApiException.class)
    public ErrorResponse handlePestApi(PestApiException e) {
        log.warn("병해충 API 호출에 실패했습니다.", e);
        return error(HttpStatus.BAD_GATEWAY, "PEST_API_ERROR", "병해충 정보 서버를 호출하지 못했습니다.");
    }

    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ExceptionHandler(TimeoutException.class)
    public ErrorResponse handleTimeoutException(TimeoutException e) {
        log.warn("외부 API 응답 시간이 초과되었습니다.", e);
        return error(HttpStatus.GATEWAY_TIMEOUT, "EXTERNAL_API_TIMEOUT", "외부 서버의 응답 시간이 초과되었습니다.");
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler({WebClientRequestException.class, WebClientResponseException.class})
    public ErrorResponse handleUnwrappedWebClientException(RuntimeException e) {
        log.warn("처리되지 않은 외부 API 오류가 발생했습니다.", e);
        return error(HttpStatus.BAD_GATEWAY, "EXTERNAL_API_ERROR", "외부 서버를 호출하지 못했습니다.");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception e) {
        log.error("처리되지 않은 서버 오류가 발생했습니다.", e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
    }

    private ErrorResponse error(HttpStatus status, String code, String message) {
        return new ErrorResponse(status.value(), code, message);
    }
}
