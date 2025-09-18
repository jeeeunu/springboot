package jpabook.javaspring.exception;

import jpabook.javaspring.common.dto.ApiResponse;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 모든 예외(Exception)를 처리하는 핸들러
     * - 예상하지 못한 에러가 발생했을 때 호출
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("서버에러가 발생되었습니다."));
    }


    /**
     * DTO 검증(@Valid) 실패 시 발생하는 MethodArgumentNotValidException 처리
     * - 유효성 검사 실패한 필드와 메시지를 모아 하나의 문자열로 합침
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        // 상태 코드 400(Bad Request)와 함께 공통 ApiResponse 포맷에 담아 반환
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errorMessage));
    }

    /**
     * 잘못된 요청 관련 예외 처리
     * - IllegalArgumentException: 잘못된 인자 전달
     * - UsernameNotFoundException: 사용자 조회 실패
     * - BadCredentialsException: 잘못된 로그인 정보
     */
    @ExceptionHandler({
            IllegalArgumentException.class,
            UsernameNotFoundException.class,
            BadCredentialsException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequestExceptions(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }

    /**
     * 권한 관련 예외 처리
     * - AccessDeniedException: 인증은 되었지만 접근 권한이 없는 경우
     * - 403 Forbidden 상태 코드와 고정된 메시지를 반환
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, "접근 권한이 없습니다."));
    }


    /**
     * 잘못된 정렬 속성 요청 시 발생하는 예외 처리
     * - Sort.by("존재하지 않는 필드") 같은 경우 PropertyReferenceException 발생
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiResponse<Void>> handlePropertyReferenceException(PropertyReferenceException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("정렬 속성이 유효하지 않습니다: " + e.getPropertyName()));
    }

    /**
     * JPA 잘못된 API 사용 시 발생하는 예외 처리
     * - InvalidDataAccessApiUsageException
     * - 정렬 방향(Direction) 관련 오류일 경우 "asc/desc만 사용 가능" 메시지 반환
     * - 그 외의 경우는 일반 데이터 접근 오류 메시지를 반환
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
        String message = e.getMessage();
        if (message != null && message.contains("Direction")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("정렬 방향이 유효하지 않습니다. 'asc' 또는 'desc'를 사용하세요."));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("데이터 접근 오류: " + message));
    }
}
