package com.m11n.hermes.core.service;

import com.m11n.hermes.core.model.Printer;
import com.m11n.hermes.core.model.PrinterStatus;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.InputStream;
import java.util.List;

public interface PdfService {

    PDDocument parse(InputStream is) throws Exception;

    String value(InputStream is, int page, String fieldName) throws Exception;

    String value(PDDocument doc, int page, String fieldName) throws Exception;

    PDDocument split(PDDocument doc, int page) throws Exception;

    PrinterStatus status(String printerName);

    void print(String file, String pageRange, String printer, String orientation, String mediaId, int copies) throws Exception;

    List<Printer> printers();
}
