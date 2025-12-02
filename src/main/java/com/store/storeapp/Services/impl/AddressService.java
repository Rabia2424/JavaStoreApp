package com.store.storeapp.Services.impl;
import com.store.storeapp.Models.UserAddress;
import com.store.storeapp.Repositories.UserAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    @Autowired
    private UserAddressRepository repo;

    public List<UserAddress> listByUser(Long userId){
        return repo.findByUserId(userId);
    }

    public Optional<UserAddress> getByIdAndUser(Long id, Long userId){
        return repo.findByIdAndUserId(id, userId);
    }

    public UserAddress add(UserAddress address){
        try{
            return repo.save(address);
        }catch(Exception e){
            throw new RuntimeException("Error saving address: " + e.getMessage(), e);
        }
    }
    public UserAddress update(UserAddress address){
        try{
            return repo.save(address);
        }catch(Exception e){
            throw new RuntimeException("Error saving address: " + e.getMessage(), e);
        }
    }

    public UserAddress findById(Long id){ return repo.findById(id).orElse(null); }

    public void delete(Long id){  repo.deleteById(id); }

    public void clearDefaultShipping(Long userId) {
        repo.clearDefaultShipping(userId);
    }

    public void clearDefaultBilling(Long userId) {
        repo.clearDefaultBilling(userId);
    }

    public long countByUserId(Long userId) {
        return repo.countByUserId(userId);
    }
}
