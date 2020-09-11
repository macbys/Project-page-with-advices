package com.maxbys.strona_z_poradami_projekt.ratings;

import com.maxbys.strona_z_poradami_projekt.answers.Answer;
import com.maxbys.strona_z_poradami_projekt.users.User;
import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Embeddable
public class RatingId implements Serializable {
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    private Answer answer;
}
