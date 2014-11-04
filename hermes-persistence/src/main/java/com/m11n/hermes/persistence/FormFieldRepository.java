package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.FormField;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormFieldRepository extends PagingAndSortingRepository<FormField, String> {
    @Query("select f.form from FormField f group by f.form")
    List<String> findFormGroupByForm();

    @Query("select f from FormField f where f.form = ?1 order by f.position")
    List<FormField> findByFormOrderByPosition(String form);
}
