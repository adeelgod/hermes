package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.BankStatementPattern;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankStatementPatternRepository extends PagingAndSortingRepository<BankStatementPattern, String> {
    BankStatementPattern findByName(String name);
}
