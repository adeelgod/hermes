package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.PrinterLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PrinterLogRepository extends PagingAndSortingRepository<PrinterLog, String> {
    PrinterLog findByOrderId(String orderId);

    List<PrinterLog> findByProcessedAtBetween(Date from, Date until);

    List<PrinterLog> findByProcessedAtBetween(Date from, Date until, Pageable pageable);
}
