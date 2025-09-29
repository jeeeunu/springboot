package jpabook.javaspring.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jpabook.javaspring.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC; // ⬅️ 추가
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.BindException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.JsonMappingException;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String REQUEST_ID_KEY = "requestId"; // 필터에서 MDC에 넣은 키와 동일해야 합니다.

    // 공통 에러 응답 헬퍼
    private ResponseEntity<ApiResponse<Void>> error(HttpStatus status, String message, String requestId) {
        String id = StringUtils.hasText(requestId) ? requestId : UUID.randomUUID().toString(); // 안전한 폴백
        String clientMsg = message + " (requestId=" + id + ")";

        return ResponseEntity.status(status)
                .body(ApiResponse.error(status.value(), clientMsg));
    }

    private String reqId() {
        return MDC.get(REQUEST_ID_KEY);
    }

    /** 400 - Bean Validation(객체 바디 @Valid) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                          HttpServletRequest req) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + (fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid"))
                .collect(Collectors.joining(", "));
        log.warn("[400] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), msg);
        return error(HttpStatus.BAD_REQUEST, msg, reqId());
    }

    /** 400 - Bean Validation(쿼리 파라미터/경로 변수 @Validated) */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException e,
                                                                       HttpServletRequest req) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("[400] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), msg);
        return error(HttpStatus.BAD_REQUEST, msg, reqId());
    }

    /** 400 - 잘못된 요청/로그인 관련 */
    @ExceptionHandler({ IllegalArgumentException.class, UsernameNotFoundException.class, BadCredentialsException.class })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e, HttpServletRequest req) {
        log.warn("[400] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), e.getMessage());
        return error(HttpStatus.BAD_REQUEST, "요청이 유효하지 않습니다.", reqId());
    }

    public ResponseEntity<ApiResponse<Void>> handleBadJson(Exception e, HttpServletRequest req) {
        log.warn("[400] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), e.getMessage());
        return error(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다.", reqId());
    }


    /** 403 - 인증됨 but 권한 없음 */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e, HttpServletRequest req) {
        log.warn("[403] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), e.getMessage());
        return error(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", reqId());
    }

    /** 405 - 지원하지 않는 HTTP 메서드 */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                      HttpServletRequest req) {
        log.warn("[405] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), e.getMessage());
        return error(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청 메서드입니다.", reqId());
    }

    /** 400 - 잘못된 정렬/쿼리 작성 */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiResponse<Void>> handlePropertyReference(PropertyReferenceException e,
                                                                     HttpServletRequest req) {
        String msg = "정렬 속성이 유효하지 않습니다: " + e.getPropertyName();
        log.warn("[400] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), msg);
        return error(HttpStatus.BAD_REQUEST, msg, reqId());
    }

    /** 400 - 잘못된 JPA API 사용 */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidDataAccess(InvalidDataAccessApiUsageException e,
                                                                     HttpServletRequest req) {
        String msg = (e.getMessage() != null && e.getMessage().contains("Direction"))
                ? "정렬 방향이 유효하지 않습니다. 'asc' 또는 'desc'를 사용하세요."
                : "데이터 접근 오류가 발생했습니다.";
        log.warn("[400] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), e.getMessage());
        return error(HttpStatus.BAD_REQUEST, msg, reqId());
    }

    /** 400 - 타입 불일치(경로/쿼리) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e,
                                                                              HttpServletRequest req) {
        log.warn("[400] {} {} reqId={} ex={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), e.getClass().getName(), e.getMessage());
        String msg = "요청 파라미터 타입이 올바르지 않습니다.";
        return error(HttpStatus.BAD_REQUEST, msg, reqId());
    }

    /** 400 - JSON 변환(세부) */
    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageConversion(HttpMessageConversionException e, HttpServletRequest req) {
        log.warn("[400] {} {} reqId={} ex={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), e.getClass().getName(), e.getMessage());
        return error(HttpStatus.BAD_REQUEST, "요청 본문 형식이 올바르지 않습니다.", reqId());
    }

    /** 400 - 경로 변수 누락/바인딩 문제(프레임워크 계열) */
    @ExceptionHandler({ MissingPathVariableException.class, ServletRequestBindingException.class })
    public ResponseEntity<ApiResponse<Void>> handleBindingInfra(Exception e, HttpServletRequest req) {
        log.warn("[400] {} {} reqId={} ex={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), e.getClass().getName(), e.getMessage());
        return error(HttpStatus.BAD_REQUEST, "요청이 유효하지 않습니다.", reqId());
    }

    // 잘못된 ENUM/타입으로 바디 파싱 실패 시: 400 + 친절한 메시지
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadJson(HttpMessageNotReadableException e, HttpServletRequest req) {
        String msg = "요청 형식이 올바르지 않습니다.";
        Throwable cause = e.getCause();

        if (cause instanceof InvalidFormatException ife) {
            // 필드 확인
            String field = ife.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(Objects::nonNull)
                    .reduce((a,b) -> b).orElse("알 수 없는 필드");

            // ENUM 에러 처리
            Class<?> target = ife.getTargetType();
            if (target != null && target.isEnum()) {
                String allowed = Arrays.stream(target.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                String input = String.valueOf(ife.getValue());
                msg = String.format("%s 값은 [%s] 중 하나여야 합니다. 입력값=%s", field, allowed, input);
            }
        }

        log.warn("[400] {} {} reqId={} ex={} -> {}", req.getMethod(), req.getRequestURI(), reqId(),
                e.getClass().getName(), msg);
        return error(HttpStatus.BAD_REQUEST, msg, reqId());
    }


    /** 409 - 무결성 제약 (unique, FK 등) */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException e,
                                                                 HttpServletRequest req) {
        String cause = (e.getMostSpecificCause() != null) ? e.getMostSpecificCause().getMessage() : e.getMessage();
        log.warn("[409] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), cause);
        return error(HttpStatus.CONFLICT, "데이터 무결성 제약 조건을 위반했습니다.", reqId());
    }

    /** 500 - 마지막 방어선 (예상 밖 오류) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAny(Exception e, HttpServletRequest req) {
        // 서버에는 스택트레이스 남김 (클라이언트에 노출 금지)
        log.error("[500] {} {} reqId={} -> {}", req.getMethod(), req.getRequestURI(), reqId(), e.getMessage(), e);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "서버에러가 발생되었습니다.", reqId());
    }
}