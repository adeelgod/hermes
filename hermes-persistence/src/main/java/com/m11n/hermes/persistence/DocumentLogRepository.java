package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.DocumentLog;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentLogRepository extends PagingAndSortingRepository<DocumentLog, String> {
    DocumentLog findByOrderIdAndType(String orderId, String type);
}
