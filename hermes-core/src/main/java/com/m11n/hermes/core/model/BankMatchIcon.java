package com.m11n.hermes.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankMatchIcon {
    private String shop;
    private String type;
    private String status;
    private String icon;
    private String action1;
    private String action2;
}
