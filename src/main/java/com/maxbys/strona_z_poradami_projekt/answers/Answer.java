package com.maxbys.strona_z_poradami_projekt.answers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maxbys.strona_z_poradami_projekt.questions.Question;
import com.maxbys.strona_z_poradami_projekt.users.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne
    private User user;
    private Integer rating;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private Question question;
    @Column(length = 1800)
    private String value;
    @CreationTimestamp
    private LocalDate creationDate;
}
