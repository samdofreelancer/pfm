package com.pfm.infrastructure.persistence.repository;

import com.pfm.domain.user.model.Email;
import com.pfm.domain.user.model.User;
import com.pfm.domain.user.model.UserId;
import com.pfm.domain.user.repository.UserRepository;
import com.pfm.infrastructure.persistence.jpa.mapper.UserPersistenceMapper;
import com.pfm.infrastructure.persistence.jpa.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findById(UserId id) {
        return jpaUserRepository.findById(id.getValue())
            .map(mapper::toDomainUser);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaUserRepository.findByEmail(email.getValue())
            .map(mapper::toDomainUser);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaUserRepository.existsByEmail(email.getValue());
    }

    @Override
    public User save(User user) {
        var entity = mapper.toJpaEntity(user);
        var savedEntity = jpaUserRepository.save(entity);
        return mapper.toDomainUser(savedEntity);
    }

    @Override
    public void delete(UserId id) {
        jpaUserRepository.deleteById(id.getValue());
    }
}