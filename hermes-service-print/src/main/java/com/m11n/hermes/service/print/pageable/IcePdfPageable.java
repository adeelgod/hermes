package com.m11n.hermes.service.print.pageable;

import org.icepdf.core.pobjects.Document;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class IcePdfPageable implements Pageable, Printable {
    private Document document;

    public IcePdfPageable(Document document) {
        this.document = document;
    }
    @Override
    public int getNumberOfPages() {
        return document.getNumberOfPages();
    }

    @Override
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return null;
    }

    @Override
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        return null;
        //return document.getPageTree();
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        //document.paintPage(pageIndex, graphics);
        return 0;
    }
}
