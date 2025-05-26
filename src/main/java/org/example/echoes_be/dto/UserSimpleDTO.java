package org.example.echoes_be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echoes_be.domain.Users;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSimpleDTO {
    private Long id;
    private String nickname;

    public UserSimpleDTO(Users user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
    }
}
