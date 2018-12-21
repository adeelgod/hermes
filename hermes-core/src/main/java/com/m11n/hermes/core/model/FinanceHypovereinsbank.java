package com.m11n.hermes.core.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "hypovereinsbank")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinanceHypovereinsbank {
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Id
    @Column(name = "id", unique = true)
    private Integer id;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "UUID")
    private String uuid;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "date")
    private Date date;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "text", length = 65535, columnDefinition="TEXT")
    @Type(type="text")
    private String text;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "text_b")
    private String textB;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "value", precision = 8, scale = 2)
    private BigDecimal value;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "reference")
    private String reference;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "reference_id")
    private String reference_id;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "status")
    private String status;
}
