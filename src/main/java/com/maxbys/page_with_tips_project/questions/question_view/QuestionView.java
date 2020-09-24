package com.maxbys.page_with_tips_project.questions.question_view;

import com.maxbys.page_with_tips_project.questions.QuestionEntity;
import com.maxbys.page_with_tips_project.users.UserEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class QuestionView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    @Temporal(TemporalType.DATE)
    private Date creationTime;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    private QuestionEntity question;
    @ManyToOne
    private UserEntity userEntity;
}
