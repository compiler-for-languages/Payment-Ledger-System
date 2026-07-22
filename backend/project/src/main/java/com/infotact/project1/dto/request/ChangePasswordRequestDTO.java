package com.infotact.project1.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDTO {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 6, max = 100)
    private String newPassword;
}
