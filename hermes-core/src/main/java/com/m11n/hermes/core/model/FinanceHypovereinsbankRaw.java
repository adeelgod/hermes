package com.m11n.hermes.core.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "hypovereinsbank_raw")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinanceHypovereinsbankRaw {
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Id
    @Column(name = "id", unique = true)
    private Integer id;
}
