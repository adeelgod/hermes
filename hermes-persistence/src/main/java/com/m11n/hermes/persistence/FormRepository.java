package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.Form;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends PagingAndSortingRepository<Form, String> {
    Form findByName(String name);
}
