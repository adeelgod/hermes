package com.m11n.hermes.persistence;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.m11n.hermes.core.model.DocumentsPrintjobItem;

public interface DocumentsPrintjobItemRepository extends PagingAndSortingRepository<DocumentsPrintjobItem, String>{
    @Query("SELECT dpi FROM DocumentsPrintjobItem dpi WHERE dpi.printjobId = :printjobId AND id IN (:ids) ORDER BY sequence, id")
   	List<DocumentsPrintjobItem> findPrintjobItems(@Param("printjobId") Integer printjobId, @Param("ids") List<Integer> ids);
	
    @Query("UPDATE DocumentsPrintjobItem dpi SET status = 'queued' WHERE dpi.printjobId = :printjobId AND id IN (:ids) ORDER BY sequence, id")
    void resetPrintjobItems(@Param("printjobId") Integer printjobId, @Param("ids") List<Integer> ids);
    
    @Query("SELECT MAX(groupNo) FROM DocumentsPrintjobItem dpi where dpi.printjobId = :printjobId")
    long countGroups(@Param("printjobId") Integer printjobId);

}
