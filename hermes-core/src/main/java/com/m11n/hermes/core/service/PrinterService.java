package com.m11n.hermes.core.service;

import com.m11n.hermes.core.model.Printer;
import com.m11n.hermes.core.model.PrinterStatus;

import java.util.List;

public interface PrinterService {
    enum JobStatus {
        CANCELLED, COMPLETED, FAILED, NO_MORE_EVENTS, UNKNOWN
    }

    enum PrintMethod {
        JAVA, PDFBOX, PAGEABLE, GHOSTSCRIPT, ACROBAT
    }

    PrinterStatus status(String printerName);

    void setPrintQueueStatus(String printQueueStatus);

    String getPrintQueueStatus();

    JobStatus print(String file, String printer) throws Exception;

    @Deprecated
    void print(String file, String pageRange, String printer, String orientation, String mediaId, int copies) throws Exception;

    List<Printer> printers();
}
