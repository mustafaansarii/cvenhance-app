package com.cvenhance.doc.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompileDocRequest {

    @NotBlank(message = "latexCode is required")
    private String latexCode;
}
