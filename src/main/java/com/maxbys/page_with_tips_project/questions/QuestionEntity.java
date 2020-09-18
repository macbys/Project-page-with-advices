package com.maxbys.page_with_tips_project.questions;

import com.maxbys.page_with_tips_project.categories.CategoryEntity;
import com.maxbys.page_with_tips_project.users.UserEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@EqualsAndHashCode
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 1800)
    private String value;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private UserEntity userEntity;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private CategoryEntity categoryEntity;
    @CreationTimestamp
    private LocalDate creationDate;

    public static QuestionEntity apply(FormQuestionTemplate formQuestionTemplate, UserEntity userEntity, CategoryEntity categoryEntity) {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.id = formQuestionTemplate.getId();
        questionEntity.value = formQuestionTemplate.getQuestionValue();
        questionEntity.userEntity = userEntity;
        questionEntity.categoryEntity = categoryEntity;
        return questionEntity;
    }

    public static QuestionEntity update(FormQuestionTemplate formQuestionTemplate, QuestionEntity questionEntity) {
        questionEntity.value = formQuestionTemplate.getQuestionValue();
        return questionEntity;
    }
}
