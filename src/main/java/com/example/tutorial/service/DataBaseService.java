package com.example.tutorial.service;

import com.example.tutorial.model.ToEntity;
import com.example.tutorial.model.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Slf4j
public abstract class DataBaseService<D extends ToEntity<E>, E extends AbstractEntity<D>> extends AbstractService{

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


}
