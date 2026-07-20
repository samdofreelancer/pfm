package com.pfm.infrastructure.persistence.repository;

import com.pfm.domain.account.model.Account;
import com.pfm.domain.account.model.AccountId;
import com.pfm.domain.account.model.AccountOwnerId;
import com.pfm.domain.account.repository.AccountRepository;
import com.pfm.infrastructure.persistence.jpa.entity.JpaAccountEntity;
import com.pfm.infrastructure.persistence.jpa.mapper.AccountPersistenceMapper;
import com.pfm.infrastructure.persistence.jpa.repository.AccountJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository jpaRepository;
    private final AccountPersistenceMapper mapper;

    public AccountRepositoryImpl(AccountJpaRepository jpaRepository, AccountPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Account save(Account account) {
        JpaAccountEntity entity = mapper.toEntity(account);
        JpaAccountEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<Account> findByUserId(AccountOwnerId userId) {
        return jpaRepository.findByUserId(userId.getValue()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(AccountId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(AccountId id) {
        return jpaRepository.existsById(id.getValue());
    }
}
