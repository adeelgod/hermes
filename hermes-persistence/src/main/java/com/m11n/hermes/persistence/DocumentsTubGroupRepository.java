package com.m11n.hermes.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.m11n.hermes.core.model.DocumentsTub;
import com.m11n.hermes.core.model.DocumentsTubGroup;

public interface DocumentsTubGroupRepository extends PagingAndSortingRepository<DocumentsTubGroup, String> {
	// TODO wrong query
    @Query("SELECT dt.groupNo FROM DocumentsTub dt WHERE dt.printjobId = :printjobId AND type = :type GROUP BY groupNo")
   	List<Integer> findGroupsByPrintjobId(@Param("printjobId") Integer printjobId, @Param("type") String type);
	
	List<DocumentsTub> findByPrintjobId(Integer printjobId);
	
    @Query("SELECT MAX(groupNo) FROM DocumentsTubGroup dtg where dtg.printjobId = :printjobId")
    long countGroups(@Param("printjobId") Integer printjobId);

}
