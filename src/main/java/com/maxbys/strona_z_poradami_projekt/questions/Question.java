package com.maxbys.strona_z_poradami_projekt.questions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maxbys.strona_z_poradami_projekt.categories.Category;
import com.maxbys.strona_z_poradami_projekt.users.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDate;

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
    @ManyToOne
    private User user;
    @JsonIgnore
    @ManyToOne
    private Category category;
    @CreationTimestamp
    private LocalDate creationDate;
}
