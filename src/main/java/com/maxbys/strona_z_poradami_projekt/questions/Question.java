package com.maxbys.strona_z_poradami_projekt.questions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.categories.Category;
import com.maxbys.strona_z_poradami_projekt.users.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 1800)
    private String value;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
//    @OneToMany(mappedBy = "question")
//    private Set<Answer> answers;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    @CreationTimestamp
    private LocalDate creationDate;
    private Long allTimeViews;
}
