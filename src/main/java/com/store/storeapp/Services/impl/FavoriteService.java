package com.store.storeapp.Services.impl;

import com.store.storeapp.DTOs.FavoriteProductDto;
import com.store.storeapp.Models.Favorite;
import com.store.storeapp.Models.Product;
import com.store.storeapp.Repositories.FavoriteRepository;
import com.store.storeapp.Repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;


import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FavoriteService {
    private final FavoriteRepository repo;
    private final ProductRepository productRepo;

    public FavoriteService(FavoriteRepository repo, ProductRepository productRepo){
        this.repo = repo; this.productRepo = productRepo;
    }

    public boolean isFavorited(Long userId, Long productId){
        if (userId == null) return false;
        return repo.existsByUserIdAndProduct_Id(userId, productId);
    }

    public Set<Long> favoritesOfUserAsSet(Long userId){
        if (userId == null) return Set.of();
        return new HashSet<>(repo.findProductIdsByUserId(userId));
    }

    @Transactional
    public void add(Long userId, Long productId){
        if (isFavorited(userId, productId)) return;
        Product p = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Favorite f = new Favorite();
        f.setUserId(userId); f.setProduct(p);
        repo.save(f);
    }

    @Transactional
    public void remove(Long userId, Long productId){
        repo.deleteByUserIdAndProduct_Id(userId, productId);
    }

//    public Page<Product> listFavorites(Long userId, Pageable pageable){
//        Page<Favorite> page = repo.findByUserId(userId, pageable);
//        List<Product> products = page.map(Favorite::getProduct).toList();
//        return new PageImpl<>(products, pageable, page.getTotalElements());
//    }

    public long countForProduct(Long productId){
        return repo.countByProduct_Id(productId);
    }

    public long countByUserId(Long userId) {
        return repo.countByUserId(userId);
    }

    public List<FavoriteProductDto> getPreview(Long userId, int limit) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .limit(limit)
                .map(fav -> new FavoriteProductDto(
                        fav.getProduct().getId(),
                        fav.getProduct().getName(),
                        fav.getProduct().getPrice(),
                        fav.getProduct().getImageUrl()
                ))
                .toList();
    }

    public List<FavoriteProductDto> getAllByUser(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(fav -> new FavoriteProductDto(
                        fav.getProduct().getId(),
                        fav.getProduct().getName(),
                        fav.getProduct().getPrice(),
                        fav.getProduct().getImageUrl()
                ))
                .toList();
    }
}
