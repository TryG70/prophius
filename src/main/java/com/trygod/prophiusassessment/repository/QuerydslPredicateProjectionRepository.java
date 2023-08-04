package com.trygod.prophiusassessment.repository;

import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface QuerydslPredicateProjectionRepository<T> {

    <Projection> Page<Projection> findAll(Predicate predicate, Pageable pageable, FactoryExpression<Projection> factoryExpression);
    <Projection> List<Projection> findAll(Predicate predicate, Sort sort, FactoryExpression<Projection> factoryExpression);
    <Projection> List<Projection> findAll(Predicate predicate, FactoryExpression<Projection> factoryExpression);
}
