package com.maxbys.page_with_tips_project.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserWithPoints {
    private UserEntity userEntity;
    private int points;
}
