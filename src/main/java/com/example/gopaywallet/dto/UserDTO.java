package com.example.gopaywallet.dto;

import com.example.gopaywallet.model.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;

    public static UserDTO fromUser(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }
}
