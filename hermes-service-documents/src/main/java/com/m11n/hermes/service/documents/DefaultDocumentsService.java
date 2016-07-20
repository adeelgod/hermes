package com.m11n.hermes.service.documents;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.model.DocumentsDocuments;
import com.m11n.hermes.core.model.DocumentsPrintjobItem;
import com.m11n.hermes.core.model.DocumentsTub;
import com.m11n.hermes.core.model.DocumentsTubGroup;
import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.service.DocumentsService;
import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.core.service.SshService;
import com.m11n.hermes.core.util.PathUtil;
import com.m11n.hermes.core.util.PropertiesUtil;
import com.m11n.hermes.persistence.DocumentsDocumentsRepository;
import com.m11n.hermes.persistence.DocumentsOrdersRepository;
import com.m11n.hermes.persistence.DocumentsPrintjobItemRepository;
import com.m11n.hermes.persistence.DocumentsTubGroupRepository;
import com.m11n.hermes.persistence.DocumentsTubRepository;
import com.m11n.hermes.persistence.FormRepository;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

@Service
public class DefaultDocumentsService implements DocumentsService {
	private static final Logger logger = LoggerFactory.getLogger(DefaultDocumentsService.class);
	
    @Inject
    private SshService sshService;

	@Inject
	private DocumentsOrdersRepository documentsOrdersRepository;
	
	@Inject
	private DocumentsDocumentsRepository documentsDocumentsRepository;
	
	@Inject
	private DocumentsPrintjobItemRepository documentsPrintjobItemRepository;
	
	@Inject
	private DocumentsTubGroupRepository documentsTubGroupRepository;
	
	@Inject
	private DocumentsTubRepository documentsTubRepository;
	
	@Inject
	private FormRepository formRepository;

    @Inject
    private ReportService reportService;
    
    @Value("${hermes.result.dir}")
    private String resultDir;

    @Value("${hermes.server.result.dir}")
    private String serverResultDir;

    @Value("${hermes.remote.enabled:false}")
    private boolean remoteEnabled;

    @Value("${hermes.invoice.api.url}")
    protected String url;

    @Value("${hermes.invoice.api.username}")
    protected String username;

    @Value("${hermes.invoice.api.password}")
    protected String password;
    
    @Inject
    @Named("jdbcTemplateAuswertung")
    protected NamedParameterJdbcTemplate jdbcTemplate;
    
    @Value("${hibernate.jdbc.batch_size:10000}")
    private int batchSize;

    protected OkHttpClient client = new OkHttpClient();

	private Integer currentGroupNo;

	private static final int NUM_TUBS = 12;
	
	private static final int NUM_TUBS_PER_TROLLEY = 12;
	
	private static final double MAX_VOLUME = 999.0;
	
	protected static final Map<String, Integer> NUM_TUBS_FOR_TYPE = ImmutableMap.<String, Integer>builder()
	.put("A3", 3)
	.put("A2", 2)
	.put("A1", 1)
	.put("B2", 1)
	.put("B1", 1)
	.build();

	private static final List<String> TYPES_A =
			Arrays.asList("A3", "A2", "A1");

	private static final List<String> TYPES_B =
			Arrays.asList("B1", "B2");
	
	public class Trolley {
		private int groupNo;
		private int tubIndex = 0;
		private Map<Object, List<Integer>> tubs = new HashMap<>();
		private Map<Object, Integer> currentTub = new HashMap<>();
		public Trolley(int groupNo) {
			this.groupNo = groupNo;
		}
		public boolean isFull() {
			return tubIndex >= NUM_TUBS_PER_TROLLEY;
		}
		public boolean hasSpace(int numTubs) {
			return this.tubIndex + numTubs <= NUM_TUBS_PER_TROLLEY;
		}
		public void addTubEntry(Object grouper, DocumentsTub documentsTub, Integer numTubs) {
			// create new tubs
			if (!this.tubs.containsKey(grouper)) {
				this.tubs.put(grouper, new LinkedList<>());
				this.currentTub.put(grouper, 0);
				for (int i = 0; i < numTubs; i++) {
					this.tubs.get(grouper).add(this.tubIndex + i);
				}
				this.tubIndex += numTubs;
			}
			// alternating assignment of tub_no
			Integer currentTubIndex = this.currentTub.get(grouper);
			Integer tubNo = this.tubs.get(grouper).get(currentTubIndex) + 1;
			currentTubIndex += 1;
			if (currentTubIndex >= this.tubs.get(grouper).size()) {
				currentTubIndex = 0;
			}
			this.currentTub.put(grouper, currentTubIndex);
			if(this.currentTub.get(grouper) != currentTubIndex) {
				logger.error("Error currentTubIndex, expected " + currentTubIndex.toString() + " but got  " +this.currentTub.get(grouper));;
			}
			documentsTub.setGroupNo(this.groupNo);
			documentsTub.setTubNo(tubNo);
		}
		public int getGroupNo() {
			return groupNo;
		}
		public Integer getNumTubs() {
			return tubs.size();
		}
	}
	
	public class Trolleys {
		private Integer groupNo;
		private List<Trolley> trolleys = new LinkedList<>();
		private Map<Object, Trolley> trolleyByGrouper = new HashMap<>();
		
		public Trolleys(Integer groupNo) {
			this.groupNo = groupNo;
		}
		public void addTubEntry(Object grouper, DocumentsTub documentsTub, Integer numTubs) {
			if (trolleyByGrouper.containsKey(grouper)) {
				// tubs have been assigned
				trolleyByGrouper.get(grouper).addTubEntry(grouper, documentsTub, numTubs);
			} else {
				// tubs have been assigned
				Trolley foundTrolley = null;
				Iterator<Trolley> trolleyIterator = trolleys.iterator();
				while (trolleyIterator.hasNext()) {
					// find trolley, that has enough space
					Trolley trolley = trolleyIterator.next();
					if (trolley.isFull()) {
						trolleyIterator.remove();
						logger.debug("Trolley full: " + trolley.getGroupNo() + " (" + trolley.getNumTubs() + "), " + trolleys.size() + " trolleys open.");
					} else if (trolley.hasSpace(numTubs)) {
						foundTrolley = trolley;
					}
				}
				if (foundTrolley == null) {
					// create new trolley
					foundTrolley = new Trolley(groupNo);
					trolleys.add(foundTrolley);
					this.groupNo += 1;
				}
				foundTrolley.addTubEntry(grouper, documentsTub, numTubs);
				if (!this.trolleyByGrouper.containsKey(grouper)) {
					this.trolleyByGrouper.put(grouper, foundTrolley);
				}
			}
		}
		public Integer getGroupNo() {
			return groupNo;
		}
	}
	
	public enum Grouper {
	    PRODUCT_ID, ORDER_ID 
	}

	@PostConstruct
	public void init() {
	
	}
	
	@Override
	public void createPicklist(int printjobId) {
		/*
		List<DocumentsOrders> documentsOrders;
		Trolleys trolleys;

		// Get orders
		trolleys = new Trolleys(0);
		createPicklist(printjobId, DefaultDocumentsService.TYPES_A, "order_id", trolleys);
		
		trolleys = new Trolleys(trolleys.getGroupNo());
		createPicklist(printjobId, DefaultDocumentsService.TYPES_B, "order_id", trolleys);
		
		*/
		
		this.currentGroupNo = 0;
		Map<Integer, Integer> openGroups = new HashMap<>();
		createPicklistNew(printjobId, DefaultDocumentsService.TYPES_A, Grouper.ORDER_ID, "picklist_a", openGroups);
		openGroups = new HashMap<>();
		createPicklistNew(printjobId, DefaultDocumentsService.TYPES_B, Grouper.PRODUCT_ID, "picklist_b", openGroups);
		
		this.createPicklistDocuments(printjobId, DefaultDocumentsService.TYPES_A, "A", "picklist_a", true);
		this.createPicklistDocuments(printjobId, Arrays.asList("B1"), "B1", "picklist_b1", false);
		this.createPicklistDocuments(printjobId, Arrays.asList("B2"), "B2", null, false);

	}
	
	public void createPicklistNew(int printjobId, List<String> types, Grouper grouper, String formSuffix, Map<Integer, Integer> openGroups) {
		try {
			// get and execute query
			logger.debug("Query: get_documents_tub_groups_" + formSuffix);
			Form form = formRepository.findByName("get_documents_tub_groups_" + formSuffix);
			Map<String, Object> params = ImmutableMap.<String, Object>builder()
					.put("printjobId", printjobId)
					.put("type", types)
					.build();
			List<Map<String, Object>> mageOrders = jdbcTemplate.queryForList(form.getSqlStatement(), params);
			
			List<DocumentsTubGroup> documentsTubGroups = new LinkedList<>();
			for (Map<String, Object> mageOrder : mageOrders) {
				String orderId = null;
				if (grouper.equals(Grouper.ORDER_ID)) {
					orderId = (String) mageOrder.get("order_id");
				}
				Integer productId = null;
				if (grouper.equals(Grouper.PRODUCT_ID)) {
					productId = (Integer) mageOrder.get("product_id");
				}
				String type = (String) mageOrder.get("type");
				Integer size = DefaultDocumentsService.NUM_TUBS_FOR_TYPE.get(type);
				
				Integer foundGroupNo = null;
				for (Entry<Integer, Integer> group : openGroups.entrySet()) {
					Integer groupNo = group.getKey();
					Integer numEntries = group.getValue();
					if (numEntries + size <= DefaultDocumentsService.NUM_TUBS_PER_TROLLEY) {
						foundGroupNo = groupNo;
					}
				}
				if (foundGroupNo == null) {
					foundGroupNo = this.currentGroupNo;
					this.currentGroupNo += 1;
					openGroups.put(foundGroupNo, 0);
				}
				Integer tubNo = openGroups.get(foundGroupNo);
				openGroups.put(foundGroupNo, tubNo + size);
				DocumentsTubGroup documentsTubGroup = new DocumentsTubGroup();
				documentsTubGroup.setPrintjobId(printjobId);
				documentsTubGroup.setType(type);
				documentsTubGroup.setGroupNo(foundGroupNo);
				documentsTubGroup.setTubNo(tubNo + 1);
				documentsTubGroup.setSize(size);
				documentsTubGroup.setOrderId(orderId);
				documentsTubGroup.setProductId(productId);
				documentsTubGroups.add(documentsTubGroup);
				//documentsTubGroupRepository.save(documentsTubGroup);
			}
			logger.debug("Creating Params");
			SqlParameterSource[] insertParams =
					SqlParameterSourceUtils.createBatch(documentsTubGroups.toArray());
			logger.debug("Batch Insert");
			jdbcTemplate.batchUpdate(
					"INSERT INTO hermes_documents_tub_group "
							+ "(printjob_id, type, group_no, tub_no, size, order_id, product_id) "
							+ "values (:printjobId, :type, :groupNo, :tubNo, :size, :orderId, :productId)",
							insertParams
					);
			logger.debug("Query: fill_documents_tubs_" + formSuffix);
			Form form2 = formRepository.findByName("fill_documents_tubs_" + formSuffix);
			Map<String, Object> params2 = ImmutableMap.<String, Object>builder()
					.put("printjobId", printjobId)
					.put("type", types)
					.build();
			jdbcTemplate.update(form2.getSqlStatement(), params2);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
	}
	
	//@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createPicklist(int printjobId, List<String> types, String tubGroupingField, Trolleys trolleys) {
		try {
			// get and execute query
			Form form = formRepository.findByName("fill_documents_printjob_orders");
			Map<String, Object> params = ImmutableMap.<String, Object>builder()
					.put("printjobId", printjobId)
					.put("type", types)
					.build();
			List<Map<String, Object>> mageOrders = jdbcTemplate.queryForList(form.getSqlStatement(), params);
			
			// group orders
			Map<Object, List<Map<String, Object>>> mageOrdersByTubGroupingField = mageOrders
					.stream()
					.collect(Collectors.groupingBy(mo -> (String) mo.get(tubGroupingField)));
			Set<Object> tubGroupingSet = new HashSet<>();
			List<Object> tubGroupingListSorted = new LinkedList<>();
			for (Map<String, Object> mageOrder : mageOrders) {
				Object tubGroupingValue = mageOrder.get(tubGroupingField);
				if (!tubGroupingSet.contains(tubGroupingValue)) {
					tubGroupingListSorted.add(tubGroupingValue);
					tubGroupingSet.add(tubGroupingValue);
				}
			}
			
			
			for (Object tubGroupingValue : tubGroupingListSorted) {
				for (Map<String, Object> mageOrder : mageOrdersByTubGroupingField.get(tubGroupingValue)) {
					String currentOrderId = (String) mageOrder.get("order_id");
					Integer productId = (Integer) mageOrder.get("product_id");
					Integer amount = (Integer) mageOrder.get("amount");
					String type = (String) mageOrder.get("type");
					DocumentsTub entry = new DocumentsTub();
					entry.setPrintjobId(printjobId);
					entry.setType(type);
					entry.setOrderId(currentOrderId);
					entry.setProductId(productId);
					entry.setAmount(amount);
					trolleys.addTubEntry(tubGroupingValue, entry, DefaultDocumentsService.NUM_TUBS_FOR_TYPE.get(type));
					documentsTubRepository.save(entry);
					//logger.debug(entry.toString());
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}

	public void createPicklistDocuments(int printjobId, List<String> types, String typeString, String picklistFilename, boolean doSummary) {
		try {
			logger.debug("creating picklist");
            Properties p = PropertiesUtil.getProperties();
            String dir = p.getProperty("hermes.print.dir");

            List<Integer> groups = new LinkedList<>();
            Map<Integer, List<String>> ordersByGroups = new HashMap<>();
			List<Object[]> groupsOrders = this.documentsTubRepository.findGroupsOrdersByPrintjobIdAndTypes(printjobId, types);
			for (Object[] go : groupsOrders) {
				Integer groupNo = (Integer) go[0];
				String orderId = (String) go[1];
				if (!ordersByGroups.containsKey(groupNo)) {
					groups.add(groupNo);
					ordersByGroups.put(groupNo, new LinkedList<>());
				}
				ordersByGroups.get(groupNo).add(orderId);
			}
			
			// create reports and add to printjob_items
			for (Integer groupNo : groups) {
				logger.debug("Creating Picklists for printjob " + printjobId + " group " + groupNo);
				
				if (picklistFilename != null) {
					String reportOutput = dir + "/reports/picklist_" + printjobId + "_" + groupNo + "_" + typeString + ".pdf";
					createReport(printjobId, groupNo, picklistFilename + ".jrxml", reportOutput);
				}
				
				if (doSummary) {
					String reportOutput2 = dir + "/reports/picklist_" + printjobId + "_" + groupNo + "_summary.pdf";
					createReport(printjobId, groupNo, "picklist_summary.jrxml", reportOutput2);
				}
				
				// get and execute query
				Form form = formRepository.findByName("populate_documents_for_print");
				Map<String, Object> params = ImmutableMap.<String, Object>builder()
						.put("printjobId", printjobId)
						.put("type", types)
						.put("groupNo", groupNo)
						.build();
				jdbcTemplate.update(form.getSqlStatement(), params);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//throw e;
		}
	}
	
	public void createReport(Integer printjobId, Integer groupNo, String filenameTemplate, String filenameOutput) {
		Map<String, Object> params = new HashMap<>();
		params.put("PRINTJOB_ID", printjobId);
		params.put("GROUP_NO", groupNo);
		reportService.generate(filenameTemplate, params, "pdf", filenameOutput);
		DocumentsDocuments doc = new DocumentsDocuments();
		doc.setPathPrint(filenameOutput);
		doc.setType("PICKLIST");
		documentsDocumentsRepository.save(doc);
		DocumentsPrintjobItem item = new DocumentsPrintjobItem();
		item.setPrintjobId(printjobId);
		item.setDocumentId(doc.getId());
		item.setGroupNo(groupNo);
		item.setSequence(0);
		item.setStatus("planned");
		logger.debug(item.toString());
		documentsPrintjobItemRepository.save(item);
	}

	@Override
	public String getPathRemote(String orderId) {
		return serverResultDir + "/" + PathUtil.segment(orderId) + "/";
	}
	
	@Override
	public String getFilenameRemote(String type, String orderId) {
		return getPathRemote(orderId) + type.toLowerCase() + ".pdf";
	}
	
	private String getPathLocal(String orderId) {
		return resultDir + "/" + PathUtil.segment(orderId) + "/";
	}
	
	private String getFilenameLocal(String type, String orderId) {
		return getPathLocal(orderId) + type.toLowerCase() + ".pdf";
	}
	
	@Override
	public boolean create(String type, String orderId, String sourceFilename, SshService sshService, boolean override) {
		// TODO replace old code
		boolean success = true;
		try {
			success = sshService.fileExists(sourceFilename);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		DocumentsDocuments doc = this.documentsDocumentsRepository.findOneByTypeAndOrderId(type, orderId);
		if (doc == null) {
			doc = new DocumentsDocuments();
		}
    	doc.setOrderId(orderId);
    	doc.setType(type.toUpperCase());
		doc.setPathLive(this.getFilenameRemote(type, orderId));
		doc.setPathPrint(this.getFilenameLocal(type, orderId));
		// copy if necessary
    	if (!getFilenameRemote(type, orderId).equals(sourceFilename)) {
    		success = false;
    		String cmd = "mkdir -p " + this.getPathRemote(orderId) + " && cp " + sourceFilename + " " + this.getFilenameRemote(type, orderId);
    		logger.info(cmd);
    		int status = -1;
			try {
				status = sshService.exec(cmd);
				success = status == 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	doc.setPathLive(this.getFilenameRemote(type, orderId));
        if (!remoteEnabled) {
        	// copy to local instance
            File f = new File(this.getPathLocal(orderId));
            if(!f.exists()) {
                f.mkdirs();
            }
            logger.info("COPY: {} -> {}", getFilenameRemote(type, orderId), getFilenameLocal(type, orderId));
            try {
            	sshService.copy(getFilenameRemote(type, orderId), getFilenameLocal(type, orderId));
            } catch (Exception e) {
            	
            }
        }
    	if (success) {
    		if (!override) {
    			documentsDocumentsRepository.save(doc);
    		}
    	} else {
    		logger.debug("No success");
    	}
    	return success;
	}
	
	@Override
	public Set<String> getInvoices(List<String> orderIds, List<String> invoiceIds) {
		logger.debug("Entering getInvoices {}, {}", orderIds, invoiceIds);
		Set<String> invoiceExists = new HashSet<>();
		try {
			sshService.connect();
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return invoiceExists;
        }
		for (int i = 0; i < orderIds.size(); i++) {
			String orderId = orderIds.get(i);
			String invoiceId;

			try {
				if (invoiceIds == null) {
					String stmt = "SELECT Rechnung as `invoiceId` FROM mage_custom_order WHERE Bestellung = :orderId";
					Map<String, Object> params = ImmutableMap.<String, Object>builder()
							.put("orderId", orderId)
							.build();
					List<Map<String, Object>> res = jdbcTemplate.queryForList(stmt, params);
					invoiceId = (String) res.get(0).get("invoiceId");
				} else {
					invoiceId = invoiceIds.get(i);
				}
    			String pathRemote = this.getPathRemote(orderId);
    			String filenameSource = serverResultDir + "/invoices/" + invoiceId + ".pdf";
    			try {
    				if (!sshService.fileExists(filenameSource)) {
    					client.setConnectTimeout(15, TimeUnit.SECONDS);
    					client.setReadTimeout(15, TimeUnit.SECONDS);
    		    		String cmd = "mkdir -p " + pathRemote + " && chmod 777 " + pathRemote;
    		    		logger.info(cmd);
    		    		sshService.exec(cmd);
    		    		String pathRemote2 = serverResultDir + "/invoices";
    		    	    if (pathRemote2 != null && pathRemote2.length() > 0 && pathRemote2.charAt(pathRemote2.length()-1)=='/') {
    		    	    	pathRemote2 = pathRemote2.substring(0, pathRemote2.length()-1);
    		    	      }
    					HttpUrl httpUrl = HttpUrl.parse(this.url)
    							.newBuilder()
    							.addQueryParameter("login", this.username)
    							.addQueryParameter("password", this.password)
    							.addQueryParameter("id", orderId)
    							.addQueryParameter("path", pathRemote2)
    							.build();
    					logger.debug("opening url " + httpUrl);
    					Request request = new Request.Builder()
    							.url(httpUrl)
    							.build();
    					com.squareup.okhttp.Response response = client.newCall(request).execute();
    				}
    			} catch (Exception e) {
    				logger.error("Could not create invoice: " + e.getMessage());
    			}
    			boolean success = this.create(DocumentType.INVOICE.name(), orderId, filenameSource, sshService, false);
    			logger.debug("Success: {}", success);
    			if (success) {
    				invoiceExists.add(orderId);
    			}
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		}
		try {
			sshService.disconnect();
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
    	return invoiceExists;
	}

	@Override
	public Set<String> getLabels(List<String> orderIds, List<String> filenames) {
		Set<String> labelExists = new HashSet<>();
		if(orderIds.size() != filenames.size()) {
			logger.error("Incorrect Array Size. orderIds: {}, paths: {}", orderIds.size(), filenames.size());
		}
		try {
			sshService.connect();
        } catch (Exception e) {
            logger.error(e.toString(), e);
            return labelExists;
        }

		for (int i = 0; i < orderIds.size(); i++) {
			String orderId = orderIds.get(i);
			String filename = filenames.get(i);
			boolean success = this.create(DocumentType.LABEL.name(), orderId, filename, sshService, false);
			if (success) {
				labelExists.add(orderId);
			}
        }
		
		try {
			sshService.disconnect();
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}

        return labelExists;
	}
	
}