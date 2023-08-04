package com.trygod.prophiusassessment.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslJpaPredicateExecutor;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import jakarta.persistence.EntityManager;
import java.util.List;

public class QuerydslPredicateProjectionRepositoryImpl<T> extends QuerydslJpaPredicateExecutor<T> implements QuerydslPredicateProjectionRepository<T> {
    private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;

    private final Querydsl querydsl;

    public QuerydslPredicateProjectionRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this(entityInformation, entityManager, DEFAULT_ENTITY_PATH_RESOLVER);
    }

    public QuerydslPredicateProjectionRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager, EntityPathResolver resolver) {
        super(entityInformation, entityManager, resolver, null);

        EntityPath<T> path = resolver.createPath(entityInformation.getJavaType());
        PathBuilder<T> builder = new PathBuilder<T>(path.getType(), path.getMetadata());
        this.querydsl = new Querydsl(entityManager, builder);
    }

    public QuerydslPredicateProjectionRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager, EntityPathResolver resolver, CrudMethodMetadata metadata) {
        super(entityInformation, entityManager, resolver, metadata);
        EntityPath<T> path = resolver.createPath(entityInformation.getJavaType());
        PathBuilder<T> builder = new PathBuilder<T>(path.getType(), path.getMetadata());
        this.querydsl = new Querydsl(entityManager, builder);
    }

    @Override
    public <Projection> List<Projection> findAll(Predicate predicate, FactoryExpression<Projection> factoryExpression) {
        return createQuery(predicate).select(factoryExpression).fetch();
    }

    @Override
    public <Projection> List<Projection> findAll(Predicate predicate, Sort sort, FactoryExpression<Projection> factoryExpression) {
        JPQLQuery<Projection> query = createQuery(predicate).select(factoryExpression);
        querydsl.applySorting(sort, query);

        return query.fetch();
    }

    @Override
    public <Projection> Page<Projection> findAll(Predicate predicate, Pageable pageable, FactoryExpression<Projection> factoryExpression) {
        JPQLQuery<Projection> query = createQuery(predicate).select(factoryExpression);
        querydsl.applyPagination(pageable, query);
        querydsl.applySorting(pageable.getSort(), query);

        QueryResults<Projection> queryResults = query.fetchResults();
        return new PageImpl<>(queryResults.getResults(), pageable, queryResults.getTotal());
    }
}
