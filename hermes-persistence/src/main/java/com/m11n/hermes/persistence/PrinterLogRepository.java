package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.PrinterLog;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrinterLogRepository extends PagingAndSortingRepository<PrinterLog, String> {
    PrinterLog findByOrderId(String orderId);
}
