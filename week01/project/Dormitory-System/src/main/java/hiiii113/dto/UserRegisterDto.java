package hiiii113.dto;

import lombok.Data;

/**
 * 用户注册
 */
@Data
public class UserRegisterDto
{
    private String userNumber;
    private String password;
    private Integer role;
}
