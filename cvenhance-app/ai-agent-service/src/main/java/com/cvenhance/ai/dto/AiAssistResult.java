package com.cvenhance.ai.dto;

import java.util.List;

public record AiAssistResult(List<String> questions, List<String> suggestions) { }
