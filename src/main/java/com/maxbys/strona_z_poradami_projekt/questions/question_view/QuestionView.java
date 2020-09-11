package com.maxbys.strona_z_poradami_projekt.questions.question_view;

import com.maxbys.strona_z_poradami_projekt.questions.Question;
import com.maxbys.strona_z_poradami_projekt.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    private Date creationTime;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.EAGER)
    private Question question;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
