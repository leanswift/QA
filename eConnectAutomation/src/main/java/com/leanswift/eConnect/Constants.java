package main.java.com.leanswift.eConnect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.File;

public class Constants {
	
	//--Declaring static values used in Framework
	public static String keywordFnsClassQualifier = "main.java.com.leanswift.eConnect.KeywordFunctions";
	public static String driverWorkbookName = "Driver.xls";
	public static String testSuitesSheetName = "Test Suites";
	public static String globalVariablesSheetName = "Global Variables";
	public static int testSuiteNameCol = 0;
	public static int testSuiteRunCol = 2;
	public static int testSuiteTypeCol = 3;
	public static String testSuiteRunFlagYes = "Y";
	public static String testDataSheetName = "Test Data";
	public static int testResultArrColSize = 5;
	public static String testSuiteSheetName = "Test Suite";
	public static int testCasesNameCol = 0;
	public static int testCasesDescCol = 1;
	public static int testCasesRunCol = 2;
	public static int testMethodCol = 1;
	public static int resultArrTestCaseNameCol = 0;
	public static int resultArrTestCaseDescCol = 1;
	public static int resultArrTimeStampCol = 2;
	public static int resultArrTestResultCol = 3;
	public static int resultArrScreenShotCol = 4;
	public static String testResultPass = "Pass";
	public static String testResultFail = "Fail";
	public static String dateFormat = "yyyy-MM-dd_HH.mm.ss";
	public static String testDataDelimiter = "=";
	public static String testResultWorkbookName = "Test Suites Result.xls";
	public static String testResultHeaderColumn1 = "Test Case Name";
	public static String testResultHeaderColumn2 = "Test Case Description";
	public static String testResultHeaderColumn3 = "Execution Start Time Stamp";
	public static String testResultHeaderColumn4 = "Test Status";
	public static String testResultHeaderColumn5 = "Failure Screenshot Path";
	public static String nullValue = "null";
	public static HashMap<String, String> testDataHash = new HashMap<String, String>();
	public static boolean isProceed;
	public static String[][] testResultArr;
	public static String tempVar;
	public static String appendVar="";
	public static String windowHandle;
	public static List<String> tempList = new ArrayList<String>();
	public static String testType = "testType";
	public static List<String[]> htmlResultArrList = new ArrayList<String[]>();
	public static String testReportHTMLName = "TestExecutionReport.html";
	public static String sendMail = "sendMail";
	public static String sendMailYes = "Y";
	public static String mailUserName = "mailUserName";
	public static String mailPassWord= "mailPassWord";
	public static String host = "smtp.gmail.com";
	public static String port = "465";
	public static String starttls = "true";
	public static String auth = "true";
	public static boolean debug = false;
	public static String socketFactoryClass = "javax.net.ssl.SSLSocketFactory";
	public static String fallBack = "false";
	public static String mailTo = "mailTo";
	public static String mailCc = "mailCc";
	public static String mailBcc = "mailBcc";
	public static String subjectLine = "subjectLine";
	public static String mailBodyText = "mailBodyText";
	public static String mailUser = "mail.smtp.user";
	public static String mailHost = "mail.smtp.host";
	public static String mailPort = "mail.smtp.port";
	public static String mailStarttls = "mail.smtp.starttls.enable";
	public static String mailAuth = "mail.smtp.auth";
	public static String mailDebug = "mail.smtp.debug";
	public static String mailSocketPort = "mail.smtp.socketFactory.port";
	public static String mailSocketClass = "mail.smtp.socketFactory.class";
	public static String mailFallBack = "mail.smtp.socketFactory.fallback";
	public static String mailProtocol = "smtp";

}