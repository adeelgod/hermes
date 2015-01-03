package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.BankStatement;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BankStatementRepository extends PagingAndSortingRepository<BankStatement, String> {
    BankStatement findByHash(String hash);

    List<BankStatement> findByStatusAndAmountGreaterThan(String status, BigDecimal amount);

    @Modifying(clearAutomatically = true)
    @Query("update BankStatement bs set bs.status =:status where bs.id =:id")
    void updateStatus(@Param("id") String id, @Param("status") String status);
}
