package com.m11n.hermes.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.m11n.hermes.core.model.DocumentsPrintjob;
import com.m11n.hermes.core.model.DocumentsPrintjobItem;

public interface DocumentsPrintjobRepository extends PagingAndSortingRepository<DocumentsPrintjob, Integer> {
//    DocumentsPrintjob findOne(Integer printjobId);
}
