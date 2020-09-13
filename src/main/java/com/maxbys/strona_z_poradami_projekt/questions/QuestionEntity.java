package com.maxbys.strona_z_poradami_projekt.questions;

import com.maxbys.strona_z_poradami_projekt.categories.CategoryEntity;
import com.maxbys.strona_z_poradami_projekt.users.UserEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 1800)
    private String value;
    @ManyToOne
    private UserEntity userEntity;
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
