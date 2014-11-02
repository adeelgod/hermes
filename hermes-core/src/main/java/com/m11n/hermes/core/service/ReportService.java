package com.m11n.hermes.core.service;

import java.util.Map;

public interface ReportService {
    void generate(String template, Map<String, Object> parameters, String format, String outputName);

    String getTemplateDir();
}
