package com.maxbys.page_with_tips_project.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    void deleteByEmail(String email);
//    @Query("select u, (0 + sum(a.rating) + sum(q.allViewsOfThisQuestion)) from UserEntity u left join AnswerEntity a on u.email=a.userEntity.email left join QuestionEntity q on u.email=q.userEntity.email group by u.email")
    @Query("select new com.maxbys.page_with_tips_project.users.UserWithPoints(u, case when sum(a.rating) is null and (sum(q.allViewsOfThisQuestion)/10) is null then 0 " +
            "when sum(a.rating) is null then sum(q.allViewsOfThisQuestion)" +
            " when sum(q.allViewsOfThisQuestion) is null then sum(a.rating)" +
            "else (sum(a.rating) + (sum(q.allViewsOfThisQuestion))/10) end as points) from UserEntity u " +
            "left join AnswerEntity a on u.email=a.userEntity.email left join QuestionEntity q on u.email=q.userEntity.email " +
            "group by u.email order by points desc")
//    @Query("select u, (Select sum(a.rating) from UserEntity ue inner join AnswerEntity a on ue.email=a.userEntity.email) as sdf from UserEntity u")
    Page<UserWithPoints> getRanking(Pageable pageable);
}
