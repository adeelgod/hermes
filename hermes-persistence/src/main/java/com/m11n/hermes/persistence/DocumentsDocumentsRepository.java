package com.m11n.hermes.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.m11n.hermes.core.model.DocumentsDocuments;
import com.m11n.hermes.core.model.DocumentsPrintjobItem;

public interface DocumentsDocumentsRepository extends PagingAndSortingRepository<DocumentsDocuments, String> {
    @Query("SELECT dd FROM DocumentsDocuments dd WHERE id IN (:ids)")
   	List<DocumentsDocuments> findByIds(@Param("ids") List<Integer> ids);
	

}
