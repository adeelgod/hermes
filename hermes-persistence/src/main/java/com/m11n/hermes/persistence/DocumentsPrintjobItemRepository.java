package com.m11n.hermes.persistence;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.m11n.hermes.core.model.DocumentsPrintjobItem;

public interface DocumentsPrintjobItemRepository extends PagingAndSortingRepository<DocumentsPrintjobItem, String>{
    @Query("SELECT dpi FROM DocumentsPrintjobItem dpi WHERE dpi.printjobId = :printjobId AND groupNo = :groupNo ORDER BY sequence, id")
   	List<DocumentsPrintjobItem> findPrintjobItems(@Param("printjobId") Integer printjobId, @Param("groupNo") Integer groupNo);
	
    @Query("SELECT dpi.id FROM DocumentsPrintjobItem dpi WHERE dpi.printjobId = :printjobId AND groupNo = :groupNo ORDER BY sequence, id")
    Set<Integer> findPrintjobItemIds(@Param("printjobId") Integer printjobId, @Param("groupNo") Integer groupNo);
    
    @Modifying
    @Query("UPDATE DocumentsPrintjobItem dpi SET status = 'queued' WHERE dpi.printjobId = ?1 AND id IN (?2)")
    void resetPrintjobItems(Integer printjobId, Collection<Integer> currentIds);
    
    @Query("SELECT MAX(groupNo) FROM DocumentsPrintjobItem dpi where dpi.printjobId = :printjobId")
    long countGroups(@Param("printjobId") Integer printjobId);

}
