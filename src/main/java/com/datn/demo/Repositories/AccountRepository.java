package com.datn.demo.Repositories;

import com.datn.demo.Entities.AccountEntity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {
    AccountEntity findByUsername(String username);
    AccountEntity findByEmail(String email);
    AccountEntity findByToken(String token);
    List<AccountEntity> findByRole_RoleName(String roleName); 
    
   
}

