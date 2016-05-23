package com.m11n.hermes.core.service;

import java.util.List;
import java.util.Set;

public interface DocumentsService {

	public void createPicklist(int printjobId);

	public Set<String> getInvoices(List<String> orderIds);

	public Set<String> getLabels(List<String> orderIds, List<String> labelPaths);
	
	public Set<String> getMissingFiles(List<String> orderIds, String type);
}
