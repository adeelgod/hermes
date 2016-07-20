package com.m11n.hermes.core.service;

import java.util.List;
import java.util.Set;

public interface DocumentsService {

	public void createPicklist(int printjobId);
	
	public Set<String> getInvoices(List<String> orderIds, List<String> invoiceIds);

	public Set<String> getLabels(List<String> orderIds, List<String> labelPaths);

	boolean create(String type, String orderId, String sourceFilename, SshService sshService, boolean override);

	String getPathRemote(String orderId);

	String getFilenameRemote(String type, String orderId);

}
