package com.realestate.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private boolean isActive;
}
