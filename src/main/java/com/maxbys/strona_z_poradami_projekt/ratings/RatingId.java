package com.maxbys.strona_z_poradami_projekt.ratings;

import com.maxbys.strona_z_poradami_projekt.answers.AnswerEntity;
import com.maxbys.strona_z_poradami_projekt.users.UserEntity;
import lombok.*;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Embeddable
public class RatingId implements Serializable {
    @ManyToOne
    private UserEntity userEntity;
    @ManyToOne
    private AnswerEntity answerEntity;
}
