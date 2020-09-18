package com.maxbys.page_with_tips_project.ratings;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingsRepository extends JpaRepository<RatingEntity, RatingId> {
}
