/*
 * copyright (C) reserved to CaprusIT (I) Pvt. Ltd. 2018 - 2018 All rights reserved.
 *
 * This Software is licensed under the CaprusIT private license version 1.0
 * any breach or unauthorized reutilization of this will be strictly prohibited and may leads to leagal issue.  
 */
package com.caprus.cupcake.upload.utils;

import org.apache.poi.ss.usermodel.Cell;

// TODO: Auto-generated Javadoc
/**
 * The Class DataFormatUtils.
 * @author - VijayaSaradhi R
 */
public class DataFormatUtils {

	/**
	 * Format int.
	 *
	 * @param cell the cell
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object formatInt(Cell cell) throws Exception {
		try {
			return Integer.valueOf((int) cell.getNumericCellValue()).intValue();
		} catch (Exception e) {
			throw new Exception("Invalid data for integer type");
		}

	}

	/**
	 * Format double.
	 *
	 * @param cell the cell
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object formatDouble(Cell cell) throws Exception {
		try {
			return Double.valueOf((double) cell.getNumericCellValue()).doubleValue();
		} catch (Exception e) {
			throw new Exception("Invalid data for double type");
		}

	}

	/**
	 * Format long.
	 *
	 * @param cell the cell
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object formatLong(Cell cell) throws Exception {
		try {
			return Long.valueOf((long) cell.getNumericCellValue()).longValue();
		} catch (Exception e) {
			throw new Exception("Invalid data for long type");
		}

	}

	/**
	 * Format float.
	 *
	 * @param cell the cell
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object formatFloat(Cell cell) throws Exception {
		try {
			return Float.valueOf((float) cell.getNumericCellValue()).floatValue();
		} catch (Exception e) {
			throw new Exception("Invalid data for float type");
		}

	}

	/**
	 * Format byte.
	 *
	 * @param cell the cell
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object formatByte(Cell cell) throws Exception {
		try {
			return (byte) (int) cell.getNumericCellValue();
		} catch (Exception e) {
			throw new Exception("Invalid data for byte type");
		}

	}

	/**
	 * Format SQL date.
	 *
	 * @param cell the cell
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object formatSQLDate(Cell cell) throws Exception {
		try {
			return new java.sql.Date(cell.getDateCellValue().getTime());
		} catch (Exception e) {
			throw new Exception("Invalid data for SQL Date type");
		}

	}

	/**
	 * Format date.
	 *
	 * @param cell the cell
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object formatDate(Cell cell) throws Exception {
		try {
			return cell.getDateCellValue();
		} catch (Exception e) {
			throw new Exception("Invalid data for Date type");
		}

	}

	/**
	 * Format timestamp.
	 *
	 * @param cell the cell
	 * @return the object
	 * @throws Exception the exception
	 */
	public static Object formatTimestamp(Cell cell) throws Exception {
		try {
			return new java.sql.Timestamp(cell.getDateCellValue().getTime());
		} catch (Exception e) {
			throw new Exception("Invalid data for Timestamp type");
		}

	}

}
