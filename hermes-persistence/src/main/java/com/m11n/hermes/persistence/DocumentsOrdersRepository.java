package com.m11n.hermes.persistence;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.m11n.hermes.core.model.DocumentsOrders;

public interface DocumentsOrdersRepository extends PagingAndSortingRepository<DocumentsOrders, String>{
    @Query("SELECT d FROM DocumentsOrders d WHERE d.printjobId = :printjobId and d.type IN (:types) ORDER BY d.type DESC, d.productCount DESC")
    public List<DocumentsOrders> findByPrintjobIdFilteredOrdered(@Param("printjobId") Integer printjobId, @Param("types") List<String> typesB);
    
    @Query("SELECT COUNT(*) FROM DocumentsOrders do WHERE status = 'planned'")
    long countPlanned();

}
