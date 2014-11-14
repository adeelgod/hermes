package com.m11n.hermes.service.print.pageable;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class SwinglabsPageable implements Pageable, Printable {

    private final PDFFile pdf;
    private PageFormat format;

    public SwinglabsPageable(PDFFile pdf) {
        this(pdf, new PageFormat());

        format.setOrientation(PageFormat.PORTRAIT);
    }

    public SwinglabsPageable(PDFFile pdf, PageFormat format) {
        this.pdf = pdf;
        this.format = format;
    }

    @Override
    public int getNumberOfPages() {
        return pdf.getNumPages();
    }

    @Override
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return format;
    }

    public void setFormat(PageFormat format) {
        this.format = format;
    }

    @Override
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        //pdf.getPage(pageIndex);
        return this;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        // Check for a page
        if(pageIndex >= pdf.getNumPages())
            return NO_SUCH_PAGE;

        // is this necessary?
        graphics.translate(0, 0);

        PDFPage page = pdf.getPage(pageIndex + 1);
        PDFRenderer renderer = new PDFRenderer(page, (Graphics2D) graphics, getImageableRectangle(pageFormat), null, Color.white);

        try {
            // umm... wait for finish what? Apparently this is required and not well documented!
            page.waitForFinish();
            renderer.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return PAGE_EXISTS;
    }

    private Rectangle getImageableRectangle(PageFormat format) {
        return new Rectangle(
                (int) format.getImageableX(), (int) format.getImageableY(),
                (int) format.getImageableWidth(), (int) format.getImageableHeight());
    }

}
