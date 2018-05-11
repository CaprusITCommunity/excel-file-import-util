/*
 * copyright (C) reserved to CaprusIT (I) Pvt. Ltd. 2018 - 2018 All rights reserved.
 *
 * This Software is licensed under the CaprusIT private license version 1.0
 * any breach or unauthorized reutilization of this will be strictly prohibited and may leads to leagal issue.  
 */
package com.caprus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class ImportResult.
 * @author - VijayaSaradhi R
 */
public class ImportResult {
	
	/** The import successful. */
	boolean importSuccessful = true;
	
	/**
	 * Checks if is import successful.
	 *
	 * @return true, if is import successful
	 */
	public boolean isImportSuccessful() {
		return importSuccessful;
	}
	
	/**
	 * Sets the import successful.
	 *
	 * @param importSuccessful the new import successful
	 */
	public void setImportSuccessful(boolean importSuccessful) {
		this.importSuccessful = importSuccessful;
	}

	/** The rows processed. */
	int rowsProcessed=0;
	
	/** The rows failed. */
	int rowsFailed=0;
	
	/** The table name. */
	String tableName;
	
	/** The error messages. */
	Map<String,List<Object>> errorMessages = new LinkedHashMap<String,List<Object>>();
	
	/** The error report. */
	List<ErrorReport> errorReport = new LinkedList<ErrorReport>();
	
	/**
	 * Gets the error report.
	 *
	 * @return the error report
	 */
	public List<ErrorReport> getErrorReport() {
		return errorReport;
	}
	
	/**
	 * Sets the error report.
	 *
	 * @param errorReport the new error report
	 */
	public void setErrorReport(List<ErrorReport> errorReport) {
		this.errorReport = errorReport;
	}

	/** The data types. */
	Set<String> dataTypes = new HashSet();
	
	/** The failed beans. */
	Set<Object> failedBeans = new LinkedHashSet();

	
	
	/**
	 * Gets the data types.
	 *
	 * @return the data types
	 */
	public Set<String> getDataTypes() {
		return dataTypes;
	}
	
	/**
	 * Sets the data types.
	 *
	 * @param dataTypes the new data types
	 */
	public void setDataTypes(Set<String> dataTypes) {
		this.dataTypes = dataTypes;
	}
	
	/**
	 * Gets the table name.
	 *
	 * @return the table name
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Sets the table name.
	 *
	 * @param tableName the new table name
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * Gets the failed beans.
	 *
	 * @return the failed beans
	 */
	public Set<Object> getFailedBeans() {
		return failedBeans;
	}
	
	/**
	 * Adds the failed bean.
	 *
	 * @param failedBean the failed bean
	 */
	public void addFailedBean(Object failedBean) {
		ErrorReport er = new ErrorReport();
		er.setBean(failedBean);
		Map<String,List<Object>> temp = new LinkedHashMap<String,List<Object>>();
				temp.putAll(errorMessages);
		er.setErrorMessage(temp);
		errorReport.add(er);
		errorMessages.clear();
		
	}

/**
 * Gets the rows processed.
 *
 * @return the rows processed
 */
/*	public Map<String,List<Object>> getErrorMessages() {
		return errorMessages;
	}
	public void setErrorMessages(Map<String,List<Object>> errorMessages) {
		this.errorMessages = errorMessages;
	}
*/	public int getRowsProcessed() {
		return rowsProcessed;
	}
	
	/**
	 * Sets the rows processed.
	 *
	 * @param rowsProcessed the new rows processed
	 */
	public void setRowsProcessed(int rowsProcessed) {
		this.rowsProcessed = rowsProcessed;
	}
	
	/**
	 * Gets the rows failed.
	 *
	 * @return the rows failed
	 */
	public int getRowsFailed() {
		return rowsFailed;
	}
	
	/**
	 * Sets the rows failed.
	 *
	 * @param rowsFailed the new rows failed
	 */
	public void setRowsFailed(int rowsFailed) {
		this.rowsFailed = rowsFailed;
	}
	/*public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}*/
	
	/**
	 * Adds the error message.
	 *
	 * @param index the index
	 * @param message the message
	 */
	public void addErrorMessage(String index,String message) {
		if(this.errorMessages.containsKey(index)){
			this.errorMessages.get(index).add(message);
		} else {
			List temp = new ArrayList();
			temp.add(message);
			this.errorMessages.put(index, temp);
		}
	}
	
	/**
	 * Checks for error.
	 *
	 * @return true, if successful
	 */
	public boolean hasError() {
		return this.errorMessages.size() >= 1;
	}
	
	/**
	 * The Class ErrorReport.
	 */
	private static class ErrorReport {
		
		/** The bean. */
		Object bean;
		
		/**
		 * Gets the bean.
		 *
		 * @return the bean
		 */
		public Object getBean() {
			return bean;
		}
		
		/**
		 * Sets the bean.
		 *
		 * @param bean the new bean
		 */
		public void setBean(Object bean) {
			this.bean = bean;
		}
		
		/**
		 * Gets the error message.
		 *
		 * @return the error message
		 */
		public Map<String, List<Object>> getErrorMessage() {
			return errorMessage;
		}
		
		/**
		 * Sets the error message.
		 *
		 * @param errorMessage the error message
		 */
		public void setErrorMessage(Map<String, List<Object>> errorMessage) {
			this.errorMessage = errorMessage;
		}
		
		/** The error message. */
		Map<String,List<Object>> errorMessage = new LinkedHashMap<String,List<Object>>();
		
	}
}
