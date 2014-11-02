package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.PrinterLog;
import org.springframework.data.repository.CrudRepository;

public interface PrinterLogRepository extends CrudRepository<PrinterLog, String> {
}
