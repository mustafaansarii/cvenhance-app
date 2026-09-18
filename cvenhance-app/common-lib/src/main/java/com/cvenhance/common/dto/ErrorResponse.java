package com.cvenhance.common.dto;

public record ErrorResponse(int status, String error, String message) {
}

