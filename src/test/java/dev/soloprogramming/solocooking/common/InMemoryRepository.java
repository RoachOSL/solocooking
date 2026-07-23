/*
 * Copyright (c) 2026 dev.soloprogramming
 */
package dev.soloprogramming.solocooking.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

public abstract class InMemoryRepository<T, ID> implements JpaRepository<T, ID> {

    private final Map<ID, T> entities = new LinkedHashMap<>();

    protected abstract ID getId(T entity);

    protected abstract void setId(T entity, ID id);

    protected abstract ID generateId();

    @Override
    public <S extends T> S save(S entity) {
        var entityId = getId(entity);
        if (entityId == null) {
            entityId = generateId();
            setId(entity, entityId);
        }

        entities.put(entityId, entity);
        return entity;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entitiesToSave) {
        var savedEntities = new ArrayList<S>();
        entitiesToSave.forEach(entity -> savedEntities.add(save(entity)));
        return savedEntities;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public boolean existsById(ID id) {
        return entities.containsKey(id);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        var foundEntities = new ArrayList<T>();
        ids.forEach(id -> findById(id).ifPresent(foundEntities::add));
        return foundEntities;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return toPage(findAll(), pageable);
    }

    protected Page<T> toPage(List<T> content, Pageable pageable) {
        var sortedContent = sort(content, pageable.getSort());
        if (pageable.isUnpaged()) {
            return new PageImpl<>(sortedContent);
        }

        var startIndex = Math.toIntExact(pageable.getOffset());
        if (startIndex >= sortedContent.size()) {
            return new PageImpl<>(List.of(), pageable, sortedContent.size());
        }

        var endIndex = Math.min(startIndex + pageable.getPageSize(), sortedContent.size());
        return new PageImpl<>(sortedContent.subList(startIndex, endIndex), pageable, sortedContent.size());
    }

    private List<T> sort(List<T> content, Sort sort) {
        if (sort.isUnsorted()) {
            return content;
        }

        var comparator = sort.stream()
                .map(this::propertyComparator)
                .reduce(Comparator::thenComparing)
                .orElseThrow();
        return content.stream().sorted(comparator).toList();
    }

    private Comparator<T> propertyComparator(Sort.Order order) {
        Comparator<T> comparator = Comparator.comparing(
                entity -> propertyValue(entity, order.getProperty()),
                Comparator.nullsLast(Comparator.naturalOrder())
        );
        if (order.isDescending()) {
            return comparator.reversed();
        } else {
            return comparator;
        }
    }

    @SuppressWarnings("unchecked")
    private Comparable<Object> propertyValue(T entity, String property) {
        return (Comparable<Object>) new BeanWrapperImpl(entity).getPropertyValue(property);
    }

    @Override
    public long count() {
        return entities.size();
    }

    @Override
    public void deleteById(ID id) {
        entities.remove(id);
    }

    @Override
    public void delete(T entity) {
        deleteById(getId(entity));
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entitiesToDelete) {
        entitiesToDelete.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        entities.clear();
    }

    @Override
    public void flush() {
        throw unsupported("flush");
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<T> entities) {
        throw unsupported("deleteAllInBatch");
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        throw unsupported("deleteAllByIdInBatch");
    }

    @Override
    public void deleteAllInBatch() {
        throw unsupported("deleteAllInBatch");
    }

    @Override
    public T getOne(ID id) {
        throw unsupported("getOne");
    }

    @Override
    public T getById(ID id) {
        throw unsupported("getById");
    }

    @Override
    public T getReferenceById(ID id) {
        throw unsupported("getReferenceById");
    }

    @Override
    public List<T> findAll(Sort sort) {
        throw unsupported("sorting");
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        throw unsupported("query by example");
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        throw unsupported("query by example");
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        throw unsupported("query by example");
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw unsupported("query by example");
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        throw unsupported("query by example");
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        throw unsupported("query by example");
    }

    @Override
    public <S extends T, R> R findBy(
            Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction
    ) {
        throw unsupported("query by example");
    }

    private UnsupportedOperationException unsupported(String operation) {
        return new UnsupportedOperationException(
                "Operation [%s] is not implemented by the in-memory repository".formatted(operation)
        );
    }
}
