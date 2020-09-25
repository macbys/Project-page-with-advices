package com.maxbys.page_with_tips_project.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigInteger;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserWithPointsDTO {
    private UserDTO userDTO;
    private BigInteger points;
    private BigInteger rank;
}
