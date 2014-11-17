package com.m11n.hermes.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
public enum DocumentType {
    INVOICE, LABEL, REPORT
}
