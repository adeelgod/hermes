package com.m11n.hermes.core.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class IntegrationReportTest {

    private IntegrationReport report;

    @Before
    public void setUp() {
        report = new IntegrationReport();
        report.incrementProcessed();
        report.incrementProcessed();
        report.incrementProcessed();
        report.incrementSuccess();
        report.reportFailure(2, "Lorem");
        report.reportFailure(3, "Lorem dolorem");
    }

    @Test
    public void shouldReportProperNumberOfFails() {
        // given - when
        int actual = report.getFailCount();

        // then
        Assert.assertEquals(2, actual);
    }

    @Test
    public void shouldReportProperNumberOfSuccess() {
        // given - when
        int actual = report.getSuccessCount();

        // then
        Assert.assertEquals(1, actual);
    }

    @Test
    public void shouldReportProperTotal() {
        // given - when
        int actual = report.getTotalCount();

        // then
        Assert.assertEquals(3, actual);
    }

    @Test
    public void shouldDisplayProperReport() {
        // given - when
        String actual = report.getSummary();

        // then
        log.info(actual);
        log.info("Sample report above");
        Assert.assertTrue(actual.contains("Lorem"));
        Assert.assertTrue(actual.contains("dolorem"));
    }
}