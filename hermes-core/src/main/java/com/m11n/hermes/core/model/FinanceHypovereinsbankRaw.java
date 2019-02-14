package com.m11n.hermes.core.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

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

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "Kontonummer")
    private String kontonummer;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "Buchungsdatum")
    private String buchungsdatum;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "Valuta")
    private String valuta;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "Empfaenger_1")
    private String empfaenger1;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "Empfaenger_2")
    private String empfaenger2;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "Verwendungszweck", length = 65535, columnDefinition="TEXT")
    @Type(type="text")
    private String verwendungszweck;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "Betrag")
    private String betrag;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "Waehrung")
    private String waehrung;
}
