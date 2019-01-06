package com.m11n.hermes.core.model;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Getter
@ToString
public class IntegrationReport {
    private int totalCount = 0;
    private int successCount = 0;
    private int failCount = 0;
    private Map<Integer,String> failReport = new HashMap<>();

    public void incrementProcessed() {
        totalCount++;
    }

    public void incrementSuccess() {
        successCount++;
    }

    public void reportFailure(Integer rowNumber, String reason) {
        failCount++;
        failReport.put(rowNumber,reason);
    }

    public void reportFailureOnCurrentProcessed(String reason) {
        failCount++;
        failReport.put(totalCount,reason);
    }

    public String getSummary() {
        return String.format(
                "Total: %s, Successful: %s, Failures: %s, Failures report:%n%s",
                totalCount, successCount, failCount, getFailureReport()
        );
    }

    private String getFailureReport() {
        if (failCount==0) {
            return "-";
        }

        StringBuilder report = new StringBuilder();
        report.append(getReportHeader());
        Iterator it = failReport.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            report.append(getReportRow(pair.getKey(), pair.getValue()));
            it.remove(); // avoids a ConcurrentModificationException
        }
        report.append(getReportFooter());
        return report.toString();
    }

    private String getReportHeader() {
        return String.format(
                "+--------+----------------------+%n|%-8s|%s%n+--------+----------------------+%n",
                "ROW", "FAILURE DESCRIPTION"
        );
    }

    private String getReportRow(Object row, Object info) {
        return String.format("|%-8s|%s%n", row, info);
    }

    private String getReportFooter() {
        return "+--------+----------------------+";
    }
}
