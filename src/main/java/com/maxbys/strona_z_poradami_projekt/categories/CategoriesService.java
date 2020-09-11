package com.maxbys.strona_z_poradami_projekt.categories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Autowired
    public CategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public List<Category> findAll(){
        return categoriesRepository.findAll();
    }

    public Optional<Category> findById(String id){
        return categoriesRepository.findById(id);
    }

    public void save(Category category){
        categoriesRepository.save(category);
    }

    public void deleteById(String id){
        categoriesRepository.deleteById(id);
    }

    public Page<Category> findAllByCategoryIsNull(Pageable pageable) {
        return categoriesRepository.findAllByCategoryIsNull(pageable);
    }

    public Page<Category> findAllByCategoryNameIs(String id, Pageable pageable) {
        return categoriesRepository.findAllByCategoryNameIs(id, pageable);
    }
}
