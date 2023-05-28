package com.example.tutorial.service;

import com.example.tutorial.common.data.PageParameter;
import com.example.tutorial.common.security.SecurityUser;
import com.example.tutorial.exception.IncorrectParameterException;
import com.example.tutorial.exception.InvalidDataException;
import com.example.tutorial.model.ToEntity;
import com.example.tutorial.model.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public abstract class DataBaseService<D extends ToEntity<E>, E extends AbstractEntity<D>> extends AbstractService{

    public abstract JpaRepository<E, UUID> getRepository();

    public abstract Class<E> getEntityClass();

//    public abstract Class<D> getDataClass();
    D save(D data) {
        E entity = data.toEntity();
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        else {
            if (entity.getCreatedAt() == null) {
                Optional<E> fetchedEntity = this.getRepository().findById(entity.getId());
                entity.setCreatedAt(fetchedEntity.map(AbstractEntity::getCreatedAt).orElse(null));
            }
        }
        entity = this.getRepository().save(entity);
        return entity.toData();
    }

}
