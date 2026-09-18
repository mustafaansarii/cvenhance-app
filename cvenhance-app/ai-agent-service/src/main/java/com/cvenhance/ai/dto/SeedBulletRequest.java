package com.cvenhance.ai.dto;

import lombok.Data;
import java.util.List;

@Data
public class SeedBulletRequest {
    private String role;
    private String section;
    private List<String> bullets;
}
