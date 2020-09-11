package com.maxbys.strona_z_poradami_projekt.comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CommentsService {

    private final CommentsRepository commentsRepository;

    @Autowired
    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    public Optional<Comment> findById(Long id){
        return commentsRepository.findById(id);
    }

    public void save(Comment comment){
        commentsRepository.save(comment);
    }

    public void deleteById(Long id){
        commentsRepository.deleteById(id);
    }

    public Page<Comment> findAllByAnswerId(Long id, Pageable pageable) {
        return commentsRepository.findAllByAnswerId(id, pageable);
    }

    public Page<Comment> findAllByUser_Email(String email, Pageable pageable) {
        return commentsRepository.findAllByUser_Email(email, pageable);
    }
}
