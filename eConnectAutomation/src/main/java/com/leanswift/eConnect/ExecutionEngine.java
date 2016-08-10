//--Declaring the package name space
package main.java.com.leanswift.eConnect;

import java.io.BufferedInputStream;
//--Importing necessary java packages
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import main.java.com.leanswift.eConnect.Constants;
import main.java.com.leanswift.eConnect.ExcelFunctions;

//Declaring the java class
public class ExecutionEngine {

	// Declaring main method - entry point of execution
	// --Preparing test results folder
	static String testResultFolder = prepareTestOutputFolder();
	static int tcRow_tmp = 0;
	
	public static void main(String[] args) {

		// --Creating objects for helper classes
		ExecutionEngine execEng = new ExecutionEngine();
		ExcelFunctions excelFns = new ExcelFunctions();
		KeywordFunctions keyFns = new KeywordFunctions();

		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.dateFormat);

		// --Hashing global variables read from driver sheet
		setGlobalTestData();

		// --Opening the test suites sheet from driver workbook
		excelFns.openSheet(getPath("testScriptsPath"), Constants.driverWorkbookName, Constants.testSuitesSheetName);

		// --Repeating the execution for each test suite
		for (int tssRow = 1; tssRow < excelFns.getRowCount(); tssRow++) {

			// --Reading test suite name, run mode and test type from test
			// suites sheet
			String testSuiteName = excelFns.getValueFromCell(Constants.testSuiteNameCol, tssRow) + ".xls";
			String tssRunMode = excelFns.getValueFromCell(Constants.testSuiteRunCol, tssRow);
			String tssTestType = excelFns.getValueFromCell(Constants.testSuiteTypeCol, tssRow);

			// --Checking if test suite's run mode is yes and test type equals
			// to global data variable value
			if (tssRunMode.trim().equalsIgnoreCase(Constants.testSuiteRunFlagYes) && tssTestType.trim()
					.equalsIgnoreCase(keyFns.getDataValue(Constants.testType, Constants.testDataHash))) {

				// --Opening the test data sheet of particular test suite
				// workbook
				excelFns.openSheet(getPath("testScriptsPath"), testSuiteName, Constants.testDataSheetName);

				// --Creating test result array based on test data row count
				Constants.testResultArr = new String[excelFns.getRowCount() - 1][Constants.testResultArrColSize];

				// --Opening the test suite sheet from test suite workbook
				excelFns.openSheet(getPath("testScriptsPath"), testSuiteName, Constants.testSuiteSheetName);

				// --Repeating the execution for each test case
				for (int tsRow = 1; tsRow < excelFns.getRowCount(); tsRow++) {

					// --Reading test case name, Description and test run mode
					// from test suite sheet
					String testCaseName = excelFns.getValueFromCell(Constants.testCasesNameCol, tsRow);
					String testCaseDesc = excelFns.getValueFromCell(Constants.testCasesDescCol, tsRow);
					String tsRunMode = excelFns.getValueFromCell(Constants.testCasesRunCol, tsRow);

					// --Checking if test case run mode is yes
					if (tsRunMode.trim().equalsIgnoreCase("Y")) {

						// --Opening the test data sheet again of particular
						// test suite workbook
						excelFns.openSheet(getPath("testScriptsPath"), testSuiteName, Constants.testDataSheetName);

						// --Repeating the test run for each test data row
						for (int tdRow = 1; tdRow < excelFns.getRowCount(); tdRow++) {

							// --Checking if test data row equals to any of test
							// case sheets in test suite workbook
							if (excelFns.getValueFromCell(Constants.testCasesNameCol, tdRow)
									.equalsIgnoreCase(testCaseName)) {
								System.out.println("******************************START TEST - " + testSuiteName + " / "
										+ testCaseName + "******************************");

								// --Initializing test proceed flag to true
								Constants.isProceed = true;

								// --Preparing hash map for test data row
								Constants.testDataHash = excelFns.setTestData(tdRow);

								// --Opening the test case sheet from test suite
								// workbook
								excelFns.openSheet(getPath("testScriptsPath"), testSuiteName, testCaseName);

								// --Repeating the execution for each test step
								for (int tcRow = 1; tcRow < excelFns.getRowCount(); tcRow++) {
									List<Object> paramList = new ArrayList<Object>();

									// --Reading the keyword function name from
									// each test step
									String methodName = excelFns.getValueFromCell(Constants.testMethodCol, tcRow);
									for (int tcCol = 2; tcCol < excelFns.getColumnCount(); tcCol++) {

										// --Reading object name and test data
										// for particular keyword function
										if (!excelFns.getValueFromCell(tcCol, tcRow).isEmpty() & !excelFns
												.getValueFromCell(tcCol, tcRow).equals(Constants.nullValue)) {
											paramList.add(excelFns.getValueFromCell(tcCol, tcRow));
										}
									}

									// Converting object name and test data list
									// into array for easy access
									Object[] paramListObj = new String[paramList.size()];
									paramListObj = paramList.toArray(paramListObj);

									// --This is a special case where you want
									// to run certain
									// number of steps for certain number of
									// times
									if (methodName.equalsIgnoreCase("startLoop")) {
										tcRow_tmp = tcRow;
									}
									
									// --Executing the keyword function if test
									// proceed flag is true
									if (Constants.isProceed){
										
										if (methodName.equalsIgnoreCase("endLoop")
												& Constants.incrementer < Constants.loopCount - 1) {
											tcRow = tcRow_tmp;
											Constants.incrementer++;
										} else {
										// Inspecting the test method in keyword
										// functions class file and executing
										// accordingly
										execEng.executeReflectionMethod(Constants.keywordFnsClassQualifier, methodName,
												paramListObj);
										}
									}
								}

								/*
								 * Ignore the Test Suites which contains
								 * 'FormFilling' as part of it's name and test
								 * Case which contains 'Setup" as it's name. As
								 * the files with this naming conventions has
								 * Test cases for Initial Configurations. These
								 * Test Cases/Test Suites will be ignored while
								 * publishing Test Results.
								 */
								if (!(testSuiteName.contains("FormFilling")))
									if (!(testCaseName.contains("Setup"))) {
										// --Writing test case name,
										// description, time stamp, test result,
										// screenshot path for tests
										Constants.testResultArr[tdRow
												- 1][Constants.resultArrTestCaseNameCol] = testCaseName;
										Constants.testResultArr[tdRow
												- 1][Constants.resultArrTestCaseDescCol] = testCaseDesc;
										Constants.testResultArr[tdRow - 1][Constants.resultArrTimeStampCol] = dateFormat
												.format(new Date());
										if (Constants.isProceed) {
											// --Setting blank value to
											// screenshot column if test run
											// passed
											Constants.testResultArr[tdRow
													- 1][Constants.resultArrTestResultCol] = Constants.testResultPass;
											Constants.testResultArr[tdRow - 1][Constants.resultArrScreenShotCol] = "";
										} else {
											// --Making a call to capture sheet
											// shot function if test run failed
											Constants.testResultArr[tdRow
													- 1][Constants.resultArrTestResultCol] = Constants.testResultFail;
											Constants.testResultArr[tdRow
													- 1][Constants.resultArrScreenShotCol] = captureScreenShot(
															testCaseName, testResultFolder);
										}
									}
								System.out.println("------------------------------END TEST - " + testSuiteName + " / "
										+ testCaseName + "------------------------------");
								System.out.println("\n\n");
							}
							excelFns.openSheet(getPath("testScriptsPath"), testSuiteName, Constants.testDataSheetName);
						}
					}
					excelFns.openSheet(getPath("testScriptsPath"), testSuiteName, Constants.testSuiteSheetName);
				}

				// --Removing null rows from test result array
				List<String[]> testResultList = new ArrayList<String[]>(Arrays.asList(Constants.testResultArr));
				for (int i = testResultList.size() - 1; i >= 0; i--) {
					if (Constants.testResultArr[i][0] == null)
						testResultList.remove(i);
				}

				// Writing cumulative test results to html result array list
				Constants.htmlResultArrList.addAll(testResultList);

				// --Writing test results in results workbook
				String[][] tunedTestResultArr = testResultList.toArray(new String[][] {});
				// Ignore Test Suites which contains 'FormFilling' as part of
				// it's name from publishing test results in excel.
				if (!(testSuiteName.contains("FormFilling")))
					excelFns.writeTestResult(testResultFolder, testSuiteName, tunedTestResultArr);
			}
			excelFns.openSheet(getPath("testScriptsPath"), Constants.driverWorkbookName, Constants.testSuitesSheetName);
		}

		// Publishing HTML report for test execution
		publishHTMLReport(Constants.htmlResultArrList.toArray(new String[][] {}));

		// --Displaying test execution completion message dialog
		//JOptionPane.showMessageDialog(null, "Test Execution Completed");
	}

	public void executeReflectionMethod(String strClassName, String strMethodName, Object... paramList) {

		// --Preparing parameters for test method as String class
		Class<?> params[] = new Class[paramList.length];
		for (int i = 0; i < paramList.length; i++) {
			if (paramList[i] instanceof String) {
				params[i] = String.class;
			}
		}
		try {

			// --Identifying the class for inspection based on first parameter
			// of reflection method
			Class<?> classType = Class.forName(strClassName);

			// --Creating an object for identified class file
			Object objInstance = classType.newInstance();

			// --Inspecting the class file for matching method based on second
			// and third parameters of reflection method
			Method methodName = classType.getDeclaredMethod(strMethodName, params);

			// --Invoking the keyword function based on its signature (method
			// name, type and number of parameters)
			methodName.invoke(objInstance, paramList);

		}
		// --Declaring catch block for possible exceptions
		catch (ClassNotFoundException e) {
			System.err.format(strClassName + ": Class not found %n");
		} catch (InstantiationException e) {
			System.err.format("Object can't be instantiated for specified class using newInstance method %n");
		} catch (IllegalArgumentException e) {
			System.err.format("Method invoked with wrong number of parameters %n");
		} catch (NoSuchMethodException e) {
			System.err.format("Class " + strClassName + ": does not contain the method :" + strMethodName + " %n");
		} catch (InvocationTargetException e) {
			System.err.format("Exception thrown by an invoked method %n");
			System.out.println(e.getTargetException());
		} catch (IllegalAccessException e) {
			System.err.format("Can't access a member of class with modifiers private %n");
			e.printStackTrace();
		}
	}

	public static void setGlobalTestData() {
		ExcelFunctions excelFns = new ExcelFunctions();

		// --Opening the global variables sheet from driver workbook
		// excelFns.openSheet(getPath("testScriptsPath"),
		// Constants.driverWorkbookName, Constants.globalVariablesSheetName);
		excelFns.openSheet(getPath("testScriptsPath"), Constants.driverWorkbookName,
				Constants.globalVariablesSheetName);
		// Hashing each row present in global variables sheet
		for (int tstRow = 1; tstRow < excelFns.getRowCount(); tstRow++) {
			Constants.testDataHash.put(excelFns.getValueFromCell(0, tstRow), excelFns.getValueFromCell(1, tstRow));
		}
	}

	public static String captureScreenShot(String testCaseName, String testResultFolderName) {
		String screenShotName = null, screenShotLocation = null;
		try {

			// --Capturing screenshot and converting it into a file
			SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.dateFormat);
			File scrFile = ((TakesScreenshot) KeywordFunctions.driver).getScreenshotAs(OutputType.FILE);

			// --Naming the screenshot captured and copying it to test output
			// folder
			screenShotName = testCaseName + "[" + dateFormat.format(new Date()) + "].jpg";
			screenShotLocation = testResultFolderName + "/" + screenShotName;
			FileUtils.copyFile(scrFile, new File(screenShotLocation));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return screenShotLocation;
	}

	public static String getPath(String path) {
		Properties prop = new Properties();

		try {
			File file = new java.io.File("App_Test/Resources/config.properties");
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			prop.load(bufferedInputStream);
			String appTestPath = prop.getProperty("appTest") + "/App_Test";
			String project = prop.getProperty("project");

			// Check the input path request and return the path accordingly
			if (path.equals("testScriptsPath"))
				path = appTestPath + "/Test_Scripts/" + project;
			else if (path.equals("testResultsPath"))
				path = appTestPath ;
			else if (path.equals("ObjectRepository"))
				path = appTestPath + "/Object_Repository/" + project;
			else if (path.equals("webDriverServerPath"))
				path = appTestPath + "/Resources/Driver_Servers";
			else
				path = appTestPath;
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return path;
	}

	public static String prepareTestOutputFolder() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.dateFormat);
		// delete all the TestOutput Folders present inside Test Results folder
		deleteTestOutputFolders();
		
		// --Creating a test result folder for storing output files
		String testResultFolder = getPath("testResultsPath") + "/Test_Results"; 
		new File(testResultFolder).mkdir();
		
		// -- Creating a test result folder based on the time stamp
		String testResultFolder_ts = testResultFolder + "/TestResult_" + dateFormat.format(new Date());
		new File(testResultFolder_ts).mkdir();
		try {
			// --Redirecting console output to a file in specified location
			System.setOut(new PrintStream(new FileOutputStream(testResultFolder_ts + "/Console_Output.log")));
		} catch (FileNotFoundException e) {
			/*JOptionPane.showMessageDialog(null,
					"Invalid Path.Enter the Path where eConnectAutomation Folder is placed or File Seperator should be '/' ");*/
			e.printStackTrace();
		}

		return testResultFolder_ts;
	}

	public static void deleteTestOutputFolders() {
		try {
			File path =  new File(getPath("testResultsPath") + "/Test_Results");
			// clean files and folders inside Test_Results
			if(path.exists())
			FileUtils.cleanDirectory(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void publishHTMLReport(String[][] testResultArr) {
		String htmlHeader = null, htmlBody = null, htmlFooter = null;
		OutputStream htmlfile = null;
		try {

			// --Creating a HTML file for displaying test execution report
			String executionReportFileName = testResultFolder + "/" + Constants.testReportHTMLName;
			htmlfile = new FileOutputStream(new File(executionReportFileName));
			PrintStream printhtml = new PrintStream(htmlfile);

			// --Setting up HTML header content
			htmlHeader = "<html><head><meta charset='utf-8'><title>Test Execution Report</title></head>";

			// --Setting up HTML body content
			htmlBody = "<body><h1 align='center'>eConnect Automation Report</h1><table align='center' border='1' style='width:75%'><thead><tr><th>SNo.</th><th>Test Case Name</th><th>Test Case Description</th><th>Time Stamp</th><th>Status</th><th>Failure Screenshot Location</th></thead>";
			htmlFooter = "</body></html>";
			for (int arrRow = 0; arrRow < testResultArr.length; arrRow++) {
				int printarrRow = arrRow + 1;
				htmlBody += "<tr><td align='center'>" + printarrRow + "</td>";
				for (int arrCol = 0; arrCol < testResultArr[0].length; arrCol++) {

					// --Setting up HTML content for 'PASS' condition
					if (testResultArr[arrRow][arrCol].equalsIgnoreCase(Constants.testResultPass))
						htmlBody += "<td align='center' bgcolor='#00FF00'>" + testResultArr[arrRow][arrCol] + "</td>";

					// --Setting up HTML content for 'FAIL' condition
					else if (testResultArr[arrRow][arrCol].equalsIgnoreCase(Constants.testResultFail))
						htmlBody += "<td align='center' bgcolor='#FF0000'>" + testResultArr[arrRow][arrCol] + "</td>";

					// --Setting up HTML content for failure screenshot
					// condition
					else if (testResultArr[arrRow][arrCol].endsWith(".jpg")) {
						String formattedLink = "file:///" + testResultArr[arrRow][arrCol];
						htmlBody += "<td align='center'><a href='" + formattedLink + "'>Failure Screenshot</a></td>";
					}

					// --Setting up HTML content for other condition
					else
						htmlBody += "<td align='center'>" + testResultArr[arrRow][arrCol] + "</td>";
				}
				htmlBody += "</tr>";
			}
			htmlBody += "</table>";

			// --Printing HTML content in file
			printhtml.println(htmlHeader + htmlBody + htmlFooter);
			printhtml.close();
			htmlfile.close();

			// --Mailing the report
			if (new KeywordFunctions().getDataValue(Constants.sendMail, Constants.testDataHash)
					.equalsIgnoreCase(Constants.sendMailYes))
				sendEmailReport(executionReportFileName);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendEmailReport(String testReportName) {

		// --Initializing mailer properties
		Properties props = new Properties();
		props.put(Constants.mailUser, Constants.mailUserName);
		props.put(Constants.mailHost, Constants.host);
		props.put(Constants.mailPort, Constants.port);
		props.put(Constants.mailStarttls, Constants.starttls);
		props.put(Constants.mailAuth, Constants.auth);
		props.put(Constants.mailDebug, Constants.debug);
		props.put(Constants.mailSocketPort, Constants.port);
		props.put(Constants.mailSocketClass, Constants.socketFactoryClass);
		props.put(Constants.mailFallBack, Constants.fallBack);

		// --Setting up mailer properties in session object
		Session session = Session.getDefaultInstance(props, null);

		// --Setting up debug mode
		session.setDebug(Constants.debug);

		try {

			// --Creating a message based on session object
			MimeMessage msg = new MimeMessage(session);

			// --Setting up 'From' address
			KeywordFunctions keyFns = new KeywordFunctions();
			msg.setFrom(new InternetAddress(keyFns.getDataValue(Constants.mailUserName, Constants.testDataHash)));

			// --Setting up 'To', 'Cc' and 'Bcc' addresses
			String toAddr = keyFns.getDataValue(Constants.mailTo, Constants.testDataHash);
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddr));

			String ccAddr = keyFns.getDataValue(Constants.mailCc, Constants.testDataHash);
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccAddr));

			String bccAddr = keyFns.getDataValue(Constants.mailBcc, Constants.testDataHash);
			msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccAddr));

			// --Setting up mailer subject
			msg.setSubject(keyFns.getDataValue(Constants.subjectLine, Constants.testDataHash));

			// --Setting up mailer body part
			BodyPart messageBodyPart = new MimeBodyPart();

			// --Setting up mailer body text content
			messageBodyPart.setText(keyFns.getDataValue(Constants.mailBodyText, Constants.testDataHash));
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			messageBodyPart = new MimeBodyPart();

			// --Setting up test execution report HTML file as an attachment
			DataSource source = new FileDataSource(testReportName);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(Constants.testReportHTMLName);
			multipart.addBodyPart(messageBodyPart);
			msg.setContent(multipart);
			msg.saveChanges();

			// --Setting up mailer protocol
			Transport transport = session.getTransport(Constants.mailProtocol);

			// --Authenticating SMTP mail account
			transport.connect(Constants.host, keyFns.getDataValue(Constants.mailUserName, Constants.testDataHash),
					keyFns.getDataValue(Constants.mailPassWord, Constants.testDataHash));

			// --Sending the test execution report mail
			transport.sendMessage(msg, msg.getAllRecipients());
			transport.close();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
