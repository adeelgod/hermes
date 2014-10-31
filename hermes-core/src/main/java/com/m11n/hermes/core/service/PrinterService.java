package com.m11n.hermes.core.service;

import com.m11n.hermes.core.model.Printer;
import com.m11n.hermes.core.model.PrinterStatus;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.InputStream;
import java.util.List;

public interface PrinterService {

    PrinterStatus status(String printerName);

    void print(String file, String printer) throws Exception;

    void print(String file, String pageRange, String printer, String orientation, String mediaId, int copies) throws Exception;

    List<Printer> printers();
}
