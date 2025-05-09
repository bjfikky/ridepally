package com.benorim.ridepally.dto.motorcycle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMotorcycleRequestDTO {
    @NotBlank(message = "Make is required")
    @Size(min = 2, max = 50, message = "Make must be between 2 and 50 characters")
    private String make;

    @NotBlank(message = "Model is required")
    @Size(min = 2, max = 50, message = "Model must be between 2 and 50 characters")
    private String model;

    @NotBlank(message = "Year is required")
    @Pattern(regexp = "^\\d{4}$", message = "Year must be a valid 4-digit year")
    private String year;

    @NotBlank(message = "Color is required")
    @Size(min = 2, max = 30, message = "Color must be between 2 and 30 characters")
    private String color;

    @Size(max = 30, message = "Nickname must not exceed 30 characters")
    private String nickname;
} 