package com.m11n.hermes.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.m11n.hermes.core.model.DocumentsTub;

public interface DocumentsTubRepository extends PagingAndSortingRepository<DocumentsTub, String> {
    @Query("SELECT dt.groupNo, dt.orderId FROM DocumentsTub dt WHERE dt.printjobId = :printjobId AND type IN (:types) GROUP BY groupNo, orderId ORDER BY groupNo, orderId")
   	List<Object[]> findGroupsOrdersByPrintjobIdAndTypes(@Param("printjobId") Integer printjobId, @Param("types") List<String> types);
	
}
