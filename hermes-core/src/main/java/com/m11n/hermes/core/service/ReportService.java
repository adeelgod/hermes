package com.m11n.hermes.core.service;

import com.m11n.hermes.core.model.Form;

import java.io.OutputStream;
import java.util.Map;

public interface ReportService {
    void generate(String template, Map<String, Object> parameters, String format, String outputName);

    void generate(Form form, Map<String, Object> parameters, OutputStream os);

    String getTemplateDir();
}
