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
@Table(name = "fidor_raw")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinanceFidorRaw {
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Id
    @Column(name = "id", unique = true)
    private Integer id;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "date")
    private Date date;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "text", length = 65535, columnDefinition="TEXT")
    @Type(type="text")
    private String text;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "text2", length = 65535, columnDefinition="TEXT")
    @Type(type="text")
    private String text2;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "value", precision = 8, scale = 2)
    private BigDecimal value;
}
