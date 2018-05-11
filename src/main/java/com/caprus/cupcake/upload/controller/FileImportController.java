/*
 * copyright (C) reserved to CaprusIT (I) Pvt. Ltd. 2018 - 2018 All rights reserved.
 *
 * This Software is licensed under the CaprusIT private license version 1.0
 * any breach or unauthorized reutilization of this will be strictly prohibited and may leads to leagal issue.  
 */
package com.caprus.cupcake.upload.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.caprus.ImportResult;
import com.caprus.cupcake.service.ImportService;

// TODO: Auto-generated Javadoc
/**
 * The Class FileImportController.
 * @author - VijayaSaradhi R
 */
@RestController
public class FileImportController {

	/** The dbservice. */
	@Autowired
	ImportService dbservice;

	/**
	 * Process excel sheet.
	 *
	 * @param multipartFile the multipart file
	 * @param table the table
	 * @param suppressPKErrorReporting the suppress PK error reporting
	 * @param respose the respose
	 * @return the response entity
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/file/upload", method = RequestMethod.POST, produces = "application/json")
	ResponseEntity<?> processExcelSheet(@RequestParam(value = "file") MultipartFile multipartFile,
			@RequestParam(value = "table") String table,
			HttpServletResponse respose) {
		ImportResult result = null;
		try {
			if (table != null)
				result = dbservice.processXML(multipartFile.getInputStream(), table);
			else
				return new ResponseEntity("Invalid database table " + table + " " + multipartFile.getOriginalFilename(),
						new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity(e.getMessage() + " " + multipartFile.getOriginalFilename(), new HttpHeaders(),
					HttpStatus.OK);
		}

		return new ResponseEntity(result, new HttpHeaders(), HttpStatus.OK);
	}

	/**
	 * Gets the table headers.
	 *
	 * @param tableName the table name
	 * @param respose the respose
	 * @return the table headers
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/file/upload/{tableName}/headers", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<?> getTableHeaders(@PathVariable("tableName") String tableName, HttpServletResponse respose) {
		Set<String> tableNamesStr = dbservice.getBeanHeaders(tableName);

		return new ResponseEntity<Set>(tableNamesStr, new HttpHeaders(), HttpStatus.OK);

	}

	/**
	 * Tables list.
	 *
	 * @param respose the respose
	 * @return the response entity
	 */
	@RequestMapping(value = "/file/upload/tables", method = RequestMethod.GET, produces = "application/json")
	ResponseEntity<?> tablesList(HttpServletResponse respose) {
		Set<String> tables = dbservice.getAllTables();
		return new ResponseEntity<Set>(tables, new HttpHeaders(), HttpStatus.OK);

	}

	/**
	 * Download excel template.
	 *
	 * @param table the table
	 * @param respose the respose
	 * @return the response entity
	 */
	@RequestMapping(value = "/file/download/{table}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.shee")
	ResponseEntity<?> downloadExcelTemplate(@PathVariable(value = "table") String table, HttpServletResponse respose) {

		String fileResource = table + ".xlsx";
		File f = null;
		try {
			f = new ClassPathResource(fileResource).getFile();
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileResource);
			Path path = Paths.get(f.getAbsolutePath());
			ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

			return ResponseEntity.ok().headers(headers).contentLength(f.length())
					.contentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
					.body(resource);
		} catch (IOException e) {
			if (f == null)
				return new ResponseEntity("Unsupported table " + table + " for import", new HttpHeaders(),
						HttpStatus.OK);
			return new ResponseEntity("Error loading excel file " + fileResource, new HttpHeaders(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity("Unknown error while performing request", new HttpHeaders(), HttpStatus.OK);
		}

	}

	/**
	 * Process json data.
	 *
	 * @param obj the obj
	 * @param table the table
	 * @param respose the respose
	 * @return the response entity
	 */
	@RequestMapping(value = "/file/upload/saveJsonBean/{table}", method = RequestMethod.POST)
	ResponseEntity<?> processJsonData(@RequestBody List<Object> obj, @PathVariable String table,
			HttpServletResponse respose) {
		ImportResult result = null;
		try {
			result = dbservice.processJson(obj, table);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), new HttpHeaders(), HttpStatus.OK);
		}

		return new ResponseEntity(result, new HttpHeaders(), HttpStatus.OK);
	}
}
