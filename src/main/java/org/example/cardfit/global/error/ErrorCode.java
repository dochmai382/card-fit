package org.example.cardfit.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT("입력값이 올바르지 않습니다"),
    SERVER_ERROR("서버 오류가 발생했습니다"),

    // 파일 관련
    FILE_EMPTY("파일이 비어있습니다"),
    FILE_NAME_MISSING("파일명이 없습니다"),
    FILE_SIZE_EXCEEDED("파일 크기가 너무 큽니다"),
    INVALID_FILE_TYPE("엑셀 파일(.xlsx, .xls)만 업로드 가능합니다"),

    // 엑셀 파싱
    EXCEL_HEADER_MISSING("엑셀 파일에 헤더 행이 없습니다"),
    EXCEL_PARSE_ERROR("엑셀 파일 파싱 중 오류가 발생했습니다"),
    EXCEL_REQUIRED_COLUMNS_MISSING("필수 열(날짜, 가맹점/내역, 금액)을 찾을 수 없습니다"),
    EXCEL_UNSUPPORTED_FORMAT("헤더 자동감지 실패. 지원하지 않는 엑셀 양식입니다"),

    // LLM
    LLM_RESPONSE_PARSE_ERROR("LLM 응답 파싱 실패"),

    // Validation
    CATEGORY_REQUIRED("카테고리를 선택해주세요"),
    AMOUNT_REQUIRED("금액을 입력해주세요"),
    AMOUNT_MIN("금액은 0 이상이어야 합니다"),
    PERFORMANCE_MIN("예상 실적은 0 이상이어야 합니다"),
    ;

    private final String message;
}
