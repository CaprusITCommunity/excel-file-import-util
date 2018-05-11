# excel-file-import-util

Project : Caprus MS-Excel import utility
Description : This utility helps import data from Microsoft excel to a target database.
Once packaged in any application, this utility exposes various REST APIs.

Pre-requisites: 
	Spring boot and JPA as maven dependency.
	application.properties for configuring the DB details
	Note: Applications which are built using Hibernate for persistence should register EntityManager in the spring context.

How to install/integrate:
	Build the maven project and copy the jar file "db-file-import-0.0.1-FINAL.jar" to the application classpath.
	Configure spring boot startup class to scan the package "com.caprus"

REST APIs :
	
	Get list of tables : 
			Request URL : http://localhost:8080/file/upload/tables
			Method : GET
			Request Headers
			{
			  "Accept": "application/json"
			}

	Get table headers  : 
			Request URL : http://localhost:8080/file/upload/<tableName>/headers
			Method : GET
			Request Headers
			{
			  "Accept": "application/json"
			}

	Import excel file  : 
			Request URL : http://localhost:8080/file/upload
			Method : POST

			Parameters :
			1.Name: file       Type:formData Data type:file
			2.Name: table      Type:query    Data type:string

	Note: All APIs report results in JSON format.


STEPS to import MS excel file to DB:
	* List table names in your application
	* List the valid table headers to be used in excel file
	* Import the excel file for given table
