package com.m11n.hermes.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankStatementProcessStatusDTO {
    private int magentoUpdates;
    private int dbUpdates;
    private int totalStatements;
    private boolean isRunning;
}
