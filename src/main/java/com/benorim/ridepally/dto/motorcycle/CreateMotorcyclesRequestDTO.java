package com.benorim.ridepally.dto.motorcycle;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMotorcyclesRequestDTO {
    @NotEmpty(message = "At least one motorcycle must be provided")
    @Size(max = 10, message = "Cannot add more than 10 motorcycles at once")
    @Valid
    private List<CreateMotorcycleRequestDTO> motorcycles;
} 