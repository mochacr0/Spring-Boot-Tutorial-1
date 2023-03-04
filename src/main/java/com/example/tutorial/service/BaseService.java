package com.example.tutorial.service;

import com.example.tutorial.common.data.PageParameter;
import com.example.tutorial.exception.IncorrectParameterException;
import com.example.tutorial.model.ToEntity;
import com.example.tutorial.model.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public abstract class BaseService<D extends ToEntity<E>, E extends AbstractEntity<D>> {

    public abstract JpaRepository<E, UUID> getRepository();

    public abstract Class<E> getEntityClass();

//    public abstract Class<D> getDataClass();
    D save(D data) {
        E entity = data.toEntity();
        log.info(entity.toString());
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        else {
            if (entity.getCreatedAt() == null) {
                E fetchedEntity = this.getRepository().getReferenceById(entity.getId());
                if (fetchedEntity != null) {
                    entity.setCreatedAt(fetchedEntity.getCreatedAt());
                }
            }
        }
        entity = this.getRepository().save(entity);
        return entity.toData();
    }

    public void validatePageParameter(PageParameter pageParameter) {
        if (pageParameter.getPage() < 0) {
            throw new IncorrectParameterException("Page number should be positive");
        }
        if (pageParameter.getPageSize() < 0) {
            throw new IncorrectParameterException("Page size should be positive");
        }
//        boolean isSortPropertySupported = Arrays.stream(getEntityClass().getFields()).anyMatch(field -> field.getName().equals(pageParameter.getSortProperty()));
//        if (!isSortPropertySupported) {
//            throw new IncorrectParameterException("Unsupported sort property for " + this.getEntityClass().getSimpleName() + ": " + pageParameter.getSortProperty());
//        }
    }
}
