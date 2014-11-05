package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends PagingAndSortingRepository<Form, String> {
    List<FormField> findByName(String name);
}
