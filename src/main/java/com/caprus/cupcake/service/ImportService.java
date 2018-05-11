/*
 * copyright (C) reserved to CaprusIT (I) Pvt. Ltd. 2018 - 2018 All rights reserved.
 *
 * This Software is licensed under the CaprusIT private license version 1.0
 * any breach or unauthorized reutilization of this will be strictly prohibited and may leads to leagal issue.  
 */
package com.caprus.cupcake.service;

import java.beans.Statement;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caprus.ImportResult;
import com.caprus.cupcake.upload.exception.InvalidHeaderException;
import com.caprus.cupcake.upload.utils.DataFormatUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// TODO: Auto-generated Javadoc
/**
 * The Class ImportService.
 * @author - VijayaSaradhi R
 */
@Service
public class ImportService {

	/** The headers map. */
	private Map<String, Map<String, Map<String, Cell>>> headersMap = new LinkedHashMap<String, Map<String, Map<String, Cell>>>();
	

	/** The dbservice. */
	@Autowired
	DBService dbservice;

	/**
	 * Process XML.
	 *
	 * @param file the file
	 * @param table the table
	 * @return the import result
	 * @throws Exception the exception
	 */
	public ImportResult processXML(InputStream file, String table) throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);
		Validator val = Validation.buildDefaultValidatorFactory().getValidator();

		Row row = null;
		ImportResult result = new ImportResult();
		result.setTableName(table);
		int rowsSuceesffullyProcessed = 0;
		int rowsfalied = 0;
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			row = rowIterator.next();
			// Skip read heading
			if (row.getRowNum() == 0) {
				headersMap.clear();
				registerHeaders(table, row);
				// register cell override header values map
				registerCellValueOverrideHeaders(workbook, table);
				System.out.println("Registered Map is : " + this.headersMap);
				continue;
			}
			Object tableBean = dbservice.getBean(table);

			if (tableBean == null) {
				result.setImportSuccessful(false);
				result.addErrorMessage("GeneralError", "Invalid table name :" + table);
				return result;
			}

			Object o = null;
			try {

				o = populateBeanWithCellvalues(tableBean, table, row, result);

				// handle bean with constraints configured in the bean
				Set<ConstraintViolation<Object>> violations = val.validate(o);
				for (ConstraintViolation<Object> violation : violations) {
					result.setImportSuccessful(false);
					result.addErrorMessage("Row:" + row.getRowNum(),
							" Invalid data " + violation.getMessage() + " for property " + violation.getPropertyPath());
					result.setRowsFailed(++rowsfalied);
				}

				if (!violations.isEmpty()) {
					result.addFailedBean(o);
					continue;
				}

				dbservice.processDataBean(o);
				result.setRowsProcessed(++rowsSuceesffullyProcessed);
			} catch (Exception e) {
				Throwable t = e.getCause();
				if (t != null) {
					while ((t != null) && !(t instanceof org.hibernate.exception.ConstraintViolationException)) {
						t = t.getCause();
					}
					if ((t != null) && (t instanceof org.hibernate.exception.ConstraintViolationException)) {
						Throwable cve = t.getCause();
							result.addErrorMessage("Row:" + row.getRowNum(), " Constraint error " + cve.getMessage());
					} else {
						result.addErrorMessage("Row:" + row.getRowNum(), " Error while saving data " + e.getMessage());
					}
				}

				if (e instanceof InvalidHeaderException) {
					result.setImportSuccessful(false);
					break;
				} else if (e instanceof javax.persistence.EntityExistsException) {
						result.addErrorMessage("Row:" + row.getRowNum(), " Error while saving data " + e.getMessage());
				} else {
					// result.addErrorMessage(e.getMessage());
				}

				result.setImportSuccessful(false);
				result.setRowsFailed(++rowsfalied);
				if (o != null)
					result.addFailedBean(o);

				continue;
			}

		}

		if (!result.isImportSuccessful()) {
			result.setDataTypes(getBeanHeaders(table));
		}

		return result;
	}

	/**
	 * Process json.
	 *
	 * @param obj the obj
	 * @param table the table
	 * @return the import result
	 * @throws Exception the exception
	 */
	public ImportResult processJson(List<Object> obj, String table) throws Exception {

		Object tableBean = dbservice.getBean(table);
		ObjectMapper mapper = new ObjectMapper();
		int rowsSuceesffullyProcessed = 0;
		int rowsfalied = 0;
		Validator val = Validation.buildDefaultValidatorFactory().getValidator();

		ImportResult result = new ImportResult();
		result.setTableName(table);

		if (tableBean == null) {
			result.setImportSuccessful(false);
			result.addErrorMessage("GeneralError", "Invalid table name :" + table);
			return result;
		}

		int currentRow = 0;
		for (Object objStrBean : obj) {
			currentRow++;
			Object bean = null;
			try {
				String jsonString = new JSONObject((java.util.LinkedHashMap) objStrBean).toString();
				bean = mapper.readValue(jsonString, tableBean.getClass());

				// handle bean with constraints configured in the bean
				Set<ConstraintViolation<Object>> violations = val.validate(bean);
				for (ConstraintViolation<Object> violation : violations) {
					result.setImportSuccessful(false);
					result.addErrorMessage("Row:" + currentRow,
							" Invalid data " + violation.getMessage() + " for property " + violation.getPropertyPath());
					result.setRowsFailed(++rowsfalied);
				}

				if (!violations.isEmpty()) {
					result.addFailedBean(bean);
					continue;
				}

				dbservice.processDataBean(bean);
				result.setRowsProcessed(++rowsSuceesffullyProcessed);
			} catch (Exception e) {
				Throwable t = e.getCause();
				if (t != null) {
					while ((t != null) && !(t instanceof org.hibernate.exception.ConstraintViolationException)) {
						t = t.getCause();
					}
					if (t instanceof org.hibernate.exception.ConstraintViolationException) {
						Throwable cve = t.getCause();
							result.addErrorMessage("Row:" + currentRow, " Constraint error " + cve.getMessage());
					} else {
						result.addErrorMessage("Row:" + currentRow, " Error while saving data " + t.getMessage());
					}
				}

				if (e instanceof InvalidHeaderException) {
					result.setImportSuccessful(false);
					break;
				} else if (e instanceof JsonParseException) {
					result.addErrorMessage("GeneralError",
							"Unable to parse Json at row : " + currentRow + e.getMessage());
					break;
				} else if (e instanceof JsonMappingException) {
					result.addErrorMessage("Row:" + currentRow,
							" Field type mismatch between json and table " + e.getMessage());
				} else if (e instanceof IOException) {
					result.addErrorMessage("GeneralError", "Bad JSON data at row : " + currentRow + e.getMessage());
					break;
				}

				result.setImportSuccessful(false);
				result.setRowsFailed(++rowsfalied);
				result.addFailedBean(bean);

				continue;

			}

		}

		if (!result.isImportSuccessful())
			result.setDataTypes(getBeanHeaders(table));
		return result;

	}

	/**
	 * Register cell value override headers.
	 *
	 * @param cellValueOverrideHeadersWB the cell value override headers WB
	 * @param table the table
	 */
	private void registerCellValueOverrideHeaders(XSSFWorkbook cellValueOverrideHeadersWB, String table) {

		try {
			XSSFSheet cellValueOverrideHeadersHeet = cellValueOverrideHeadersWB.getSheetAt(1);
			Map<String, Map<String, Cell>> headerKeyMap = this.headersMap.get(table);

			if (headerKeyMap != null) {
				Iterator<Row> rowIterator = cellValueOverrideHeadersHeet.iterator();
				Row row = null;
				Map<String, Cell> map = null;
				boolean headerFound = false;
				while (rowIterator.hasNext()) {
					row = rowIterator.next();
					boolean isHeaderRow = row.getCell(row.getFirstCellNum() + 1) == null;
					if (isHeaderRow) {
						Cell headerCell = row.getCell(row.getFirstCellNum());
						map = headerKeyMap.get(headerCell.toString());
						continue;
					}

					if (!isHeaderRow) {
						Iterator<Cell> it = row.cellIterator();
						while (it.hasNext()) {
							Cell key = ((XSSFCell) it.next());
							Cell value = ((XSSFCell) it.next());
							map.put(key.toString(), value);
						}
					}

				}
			}
		} catch (Exception e) {
			// overridden cell header values sheet not found in file
			System.out.println("overridden cell header values sheet not found in file");
		}

	}

	/**
	 * Gets the matching bean property.
	 *
	 * @param tableBean the table bean
	 * @param cellHeaderName the cell header name
	 * @return the matching bean property
	 */
	private Field getMatchingBeanProperty(Object tableBean, String cellHeaderName) {
		Field beanProperty[] = tableBean.getClass().getDeclaredFields();
		List<Field> fields = Arrays.asList(beanProperty);
		for (Field f : fields) {
			if (f.getName().equalsIgnoreCase(cellHeaderName))
				return f;
		}
		return null;
	}

	/**
	 * Populate bean with cellvalues.
	 *
	 * @param tableBean the table bean
	 * @param table the table
	 * @param row the row
	 * @param result the result
	 * @return the object
	 * @throws Exception the exception
	 */
	private Object populateBeanWithCellvalues(Object tableBean, String table, Row row, ImportResult result)
			throws Exception {
		Map<String, Map<String, Cell>> headersCells = this.headersMap.get(table);

		Set<String> headers = headersCells.keySet();
		Statement stmt;

		int cellHeaderNumber = 0;
		boolean cellExceptionOccured = false;
		for (String cellHeader : headers) {
			// get overridden cell if available
			Map<String, Cell> overriddenHeadersCells = headersCells.get(cellHeader);
			Cell actualCell = row.getCell(cellHeaderNumber++);
			if (actualCell == null)
				continue;

			Cell cell = (overriddenHeadersCells.get(actualCell.toString()) == null) ? actualCell
					: overriddenHeadersCells.get(actualCell.getStringCellValue());

			try {
				Object value = getValueUsingType(tableBean, cellHeader, cell, result);

				if (value != null && !value.equals("Invalid")) {
					stmt = new Statement(tableBean, "set" + cellHeader, new Object[] { value });
					stmt.execute();
				}
			} catch (Exception e) {
				if (e instanceof InvalidHeaderException) {
					result.addErrorMessage("GeneralError", e.getMessage());
					throw e;
				} else {
					cellExceptionOccured = true;
					result.addErrorMessage("Row:" + row.getRowNum(),
							" Invalid value for " + cellHeader + " " + e.getMessage());
				}

			}

		}

		if (cellExceptionOccured) {
			result.addFailedBean(tableBean);
			throw new Exception(" Invalid Cell Data");
		}

		return tableBean;
	}

	/**
	 * Gets the value using type.
	 *
	 * @param tableBean the table bean
	 * @param cellHeader the cell header
	 * @param cell the cell
	 * @param result the result
	 * @return the value using type
	 * @throws InvalidHeaderException the invalid header exception
	 * @throws Exception the exception
	 */
	private Object getValueUsingType(Object tableBean, String cellHeader, Cell cell, ImportResult result)
			throws InvalidHeaderException, Exception {

		Field beanProperty = getMatchingBeanProperty(tableBean, cellHeader);

		if (beanProperty == null)
			throw new InvalidHeaderException("Column header " + cellHeader + " does not match Entity property ");

		Class beanPropertyType = beanProperty.getType();
		String dataType = beanPropertyType.getName();

		// System.out.println(" cell value : " + cellValue);

		if (dataType.equalsIgnoreCase("java.lang.String")) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			return cell.toString();
		} else if (dataType.equalsIgnoreCase("int") || dataType.equalsIgnoreCase("java.lang.Integer"))
			return DataFormatUtils.formatInt(cell);
		else if (dataType.equalsIgnoreCase("double") || dataType.equalsIgnoreCase("java.lang.Double"))
			return DataFormatUtils.formatDouble(cell);
		else if (dataType.equalsIgnoreCase("long") || dataType.equalsIgnoreCase("java.lang.Long"))
			return DataFormatUtils.formatLong(cell);
		else if (dataType.equalsIgnoreCase("float") || dataType.equalsIgnoreCase("java.lang.Float"))
			return DataFormatUtils.formatFloat(cell);
		else if (dataType.equalsIgnoreCase("byte") || dataType.equalsIgnoreCase("java.lang.Byte"))
			return DataFormatUtils.formatByte(cell);
		else if (dataType.equalsIgnoreCase("java.sql.Date"))
			return DataFormatUtils.formatSQLDate(cell);
		else if (dataType.equalsIgnoreCase("java.util.Date"))
			return DataFormatUtils.formatDate(cell);
		else if (dataType.equalsIgnoreCase("java.sql.Timestamp"))
			return DataFormatUtils.formatTimestamp(cell);
		else {
			System.out.println("bean property unknown type :" + dataType);
			return cell.toString();
		}

	}

	/**
	 * Register headers.
	 *
	 * @param table the table
	 * @param row the row
	 */
	private void registerHeaders(String table, Row row) {
		// get header and overridden values map
		Map<String, Map<String, Cell>> headerKeyMap = this.headersMap.get(table);
		if (headerKeyMap == null) {
			Iterator<Cell> it = row.cellIterator();
			Map<String, Map<String, Cell>> headerMapWithNoOverriddenValues = new LinkedHashMap<String, Map<String, Cell>>();
			while (it.hasNext()) {
				headerMapWithNoOverriddenValues.put(((XSSFCell) it.next()).toString(),
						new LinkedHashMap<String, Cell>());
			}
			this.headersMap.put(table, headerMapWithNoOverriddenValues);
		}
	}

	/**
	 * Gets the bean headers.
	 *
	 * @param tableName the table name
	 * @return the bean headers
	 */
	public Set<String> getBeanHeaders(String tableName) {
		Set<String> builder = new TreeSet<String>();
		Object o = this.dbservice.getBean(tableName);
		if (o != null) {
			for (Method m : o.getClass().getDeclaredMethods()) {
				if (m.getName().startsWith("get")
						&& (m.getReturnType().isPrimitive() || m.getReturnType().isAssignableFrom(String.class)
								|| m.getReturnType().isAssignableFrom(Date.class)
								|| m.getReturnType().isAssignableFrom(java.util.Date.class)
								|| m.getReturnType().isAssignableFrom(java.lang.Integer.class)
								|| m.getReturnType().isAssignableFrom(java.lang.Float.class)
								|| m.getReturnType().isAssignableFrom(java.lang.Double.class)
								|| m.getReturnType().isAssignableFrom(java.lang.Long.class)
								|| m.getReturnType().isAssignableFrom(java.lang.Byte.class)
								|| m.getReturnType().isAssignableFrom(java.lang.Boolean.class)
								|| m.getReturnType().isAssignableFrom(java.sql.Timestamp.class)))
					builder.add(m.getName().substring(3) + ":" + m.getReturnType());
				// todo: method annotated with auto generated id needs to be
				// skipped
			}

		}

		return builder;
	}

	/**
	 * Gets the all tables.
	 *
	 * @return the all tables
	 */
	public Set<String> getAllTables() {
		return this.dbservice.getAllBeanNames();
	}

	/**
	 * Gets the bean for table.
	 *
	 * @param table the table
	 * @return the bean for table
	 */
	public Object getBeanForTable(String table) {
		return this.dbservice.getBean(table);
	}

	/**
	 * Persist bean.
	 *
	 * @param bean the bean
	 */
	public void persistBean(Object bean) {
		this.dbservice.processDataBean(bean);
	}
}
