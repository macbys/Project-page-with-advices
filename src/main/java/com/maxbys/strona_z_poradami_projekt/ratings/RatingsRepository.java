package com.maxbys.strona_z_poradami_projekt.ratings;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingsRepository extends JpaRepository<Rating, RatingId> {
}
