package com.m11n.hermes.core.service;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.InputStream;
import java.util.List;

public interface PdfService {

    PDDocument parse(InputStream is) throws Exception;

    String value(InputStream is, int page, String fieldName) throws Exception;

    String value(PDDocument doc, int page, String fieldName) throws Exception;

    List<PDDocument> split(InputStream is, int page) throws Exception;

    List<PDDocument> split(PDDocument doc, int page) throws Exception;
}
