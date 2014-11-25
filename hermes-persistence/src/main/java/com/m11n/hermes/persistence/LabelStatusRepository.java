package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.LabelStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelStatusRepository extends PagingAndSortingRepository<LabelStatus, String> {
    List<LabelStatus> findByStatus(String status);

    List<LabelStatus> findByText(String text);
}
