package com.maxbys.strona_z_poradami_projekt.ratings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingsService {

    private final RatingsRepository ratingsRepository;

    @Autowired
    public RatingsService(RatingsRepository ratingsRepository) {
        this.ratingsRepository = ratingsRepository;
    }

    public Optional<Rating> findyById(RatingId ratingId) {
        return ratingsRepository.findById(ratingId);
    }

    public void save(Rating rating) {
        ratingsRepository.save(rating);
    }

    public void deleteById(RatingId ratingId) {
        ratingsRepository.deleteById(ratingId);
    }
}
