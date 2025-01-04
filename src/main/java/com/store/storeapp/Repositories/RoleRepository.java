package com.store.storeapp.Repositories;


import com.store.storeapp.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query(
            "Select r From Role r where r.name = :name"
    )
    Role findByName(String name);
}
