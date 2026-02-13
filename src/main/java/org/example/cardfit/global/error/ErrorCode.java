package org.example.cardfit.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"),

    // 파일 관련
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "파일이 비어있습니다"),
    FILE_NAME_MISSING(HttpStatus.BAD_REQUEST, "파일명이 없습니다"),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 너무 큽니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "엑셀 파일(.xlsx, .xls)만 업로드 가능합니다"),

    // 엑셀 파싱
    EXCEL_HEADER_MISSING(HttpStatus.BAD_REQUEST, "엑셀 파일에 헤더 행이 없습니다"),
    EXCEL_PARSE_ERROR(HttpStatus.BAD_REQUEST, "엑셀 파일 파싱 중 오류가 발생했습니다"),
    EXCEL_REQUIRED_COLUMNS_MISSING(HttpStatus.BAD_REQUEST, "필수 열(날짜, 가맹점/내역, 금액)을 찾을 수 없습니다"),
    EXCEL_UNSUPPORTED_FORMAT(HttpStatus.BAD_REQUEST, "헤더 자동감지 실패. 지원하지 않는 엑셀 양식입니다"),

    // LLM
    LLM_RESPONSE_PARSE_ERROR(HttpStatus.BAD_GATEWAY, "LLM 응답 파싱 실패"),

    // Validation
    CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리를 선택해주세요"),
    AMOUNT_REQUIRED(HttpStatus.BAD_REQUEST, "금액을 입력해주세요"),
    AMOUNT_MIN(HttpStatus.BAD_REQUEST, "금액은 0 이상이어야 합니다"),
    PERFORMANCE_MIN(HttpStatus.BAD_REQUEST, "예상 실적은 0 이상이어야 합니다"),
    ;

    private final HttpStatus status;
    private final String message;
}
