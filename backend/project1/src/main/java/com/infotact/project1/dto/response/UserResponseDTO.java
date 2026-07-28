package com.infotact.project1.dto.response;

import com.infotact.project1.enums.RoleType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private RoleType role;
}
