package com.maxbys.page_with_tips_project.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    void deleteByEmail(String email);
    @Query(value="SELECT u.id,\n" +
            "       u.name,\n" +
            "       u.email,\n" +
            "       CASE\n" +
            "           WHEN (sum(a.rating) IS NULL)\n" +
            "                AND (sum(q.all_views_of_this_question) IS NULL) THEN 0\n" +
            "           WHEN sum(a.rating) IS NULL THEN CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "           WHEN sum(q.all_views_of_this_question) IS NULL THEN sum(a.rating)\n" +
            "           ELSE sum(a.rating)+CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "       END AS col_1_0_,\n" +
            "       rank() OVER (\n" +
            "                    ORDER BY CASE\n" +
            "                                 WHEN (sum(a.rating) IS NULL)\n" +
            "                                      AND (sum(q.all_views_of_this_question) IS NULL) THEN 0\n" +
            "                                 WHEN sum(a.rating) IS NULL THEN CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "                                 WHEN sum(q.all_views_of_this_question) IS NULL THEN sum(a.rating)\n" +
            "                                 ELSE sum(a.rating)+CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "                             END DESC)\n" +
            "FROM user_entity u\n" +
            "LEFT OUTER JOIN answer_entity a ON (u.id=a.user_entity_id)\n" +
            "LEFT OUTER JOIN question_entity q ON (u.id=q.user_entity_id)\n" +
            "GROUP BY u.id\n" +
            "ORDER BY CASE\n" +
            "             WHEN (sum(a.rating) IS NULL)\n" +
            "                  AND (sum(q.all_views_of_this_question) IS NULL) THEN 0\n" +
            "             WHEN sum(a.rating) IS NULL THEN CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "             WHEN sum(q.all_views_of_this_question) IS NULL THEN sum(a.rating)\n" +
            "             ELSE sum(a.rating)+CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "         END DESC\n" +
            "LIMIT 100;", nativeQuery=true)
    List<Object[]> getRanking();
    @Query(value="SELECT\n" +
            "       u.id,\n" +
            "       CASE\n" +
            "           WHEN (sum(a.rating) IS NULL)\n" +
            "                AND (sum(q.all_views_of_this_question) IS NULL) THEN 0\n" +
            "           WHEN sum(a.rating) IS NULL THEN CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "           WHEN sum(q.all_views_of_this_question) IS NULL THEN sum(a.rating)\n" +
            "           ELSE sum(a.rating)+CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "       END AS col_1_0_,\n" +
            "       rank() OVER (\n" +
            "                    ORDER BY CASE\n" +
            "                                 WHEN (sum(a.rating) IS NULL)\n" +
            "                                      AND (sum(q.all_views_of_this_question) IS NULL) THEN 0\n" +
            "                                 WHEN sum(a.rating) IS NULL THEN CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "                                 WHEN sum(q.all_views_of_this_question) IS NULL THEN sum(a.rating)\n" +
            "                                 ELSE sum(a.rating)+CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "                             END DESC)\n" +
            "FROM user_entity u\n" +
            "LEFT OUTER JOIN answer_entity a ON (u.id=a.user_entity_id)\n" +
            "LEFT OUTER JOIN question_entity q ON (u.id=q.user_entity_id)\n" +
            "GROUP BY u.id\n" +
            "HAVING u.id=?1\n" +
            "ORDER BY CASE\n" +
            "             WHEN (sum(a.rating) IS NULL)\n" +
            "                  AND (sum(q.all_views_of_this_question) IS NULL) THEN 0\n" +
            "             WHEN sum(a.rating) IS NULL THEN CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "             WHEN sum(q.all_views_of_this_question) IS NULL THEN sum(a.rating)\n" +
            "             ELSE sum(a.rating)+CAST(sum(q.all_views_of_this_question)/10 AS INTEGER)\n" +
            "         END DESC\n" +
            "LIMIT 100;", nativeQuery=true)
    Object[] getUsersPointsAndRanking(Long id);
}
