package com.maxbys.page_with_tips_project.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserWithPointsDTO {
    private UserDTO userDTO;
    private int points;

    public static UserWithPointsDTO apply(UserWithPoints userWithPoints) {
        UserWithPointsDTO userWithPointsDTO = new UserWithPointsDTO();
        userWithPointsDTO.points = userWithPoints.getPoints();
        userWithPointsDTO.userDTO = UserDTO.apply(userWithPoints.getUserEntity());
        return userWithPointsDTO;
    }
}
