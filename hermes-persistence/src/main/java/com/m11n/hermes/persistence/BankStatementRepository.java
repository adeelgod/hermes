package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.BankStatement;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankStatementRepository extends PagingAndSortingRepository<BankStatement, String> {
    BankStatement findByHash(String hash);

    List<BankStatement> findByConfirmedAndAmountGreaterThan(Boolean confirmed, Double amount);
}
