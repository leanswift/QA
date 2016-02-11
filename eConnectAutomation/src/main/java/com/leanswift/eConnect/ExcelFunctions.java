package main.java.com.leanswift.eConnect;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelFunctions {

	Workbook rWorkbook;
	Sheet rSheet;
	WritableSheet wSheet;
	WritableWorkbook wWorkbook;

	public void openSheet(String filePath, String workbookName, String sheetName) {
		FileInputStream fs;
		try {
			
			//--Open an excel workbook and give handle for a sheet in it
			fs = new FileInputStream(filePath+"/"+workbookName);
			rWorkbook = Workbook.getWorkbook(fs);
			rSheet = rWorkbook.getSheet(sheetName);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getValueFromCell(int iColNumber, int iRowNumber) {
		
		//--Returns cell value based on column and row numbers called in opened sheet
		return rSheet.getCell(iColNumber, iRowNumber).getContents();
	}

	public int getRowCount() {
		
		//--Returns # rows containing values in opened sheet
		return rSheet.getRows();
	}

	public int getColumnCount() {
		
		//--Returns # columns containing values in opened sheet
		return rSheet.getColumns();
	}
	
	public HashMap<String, String> setTestData(int testDataRow) {
		
		//--Parsing test data and storing it as key-value pair in hash map
		//HashMap<String, String> hm = new HashMap<String, String>();
		Cell[] rowValues=rSheet.getRow(testDataRow);
		for(int i = 1; i < rowValues.length; i++) {
			String[] parseTest = rowValues[i].getContents().split(Constants.testDataDelimiter);
			if(parseTest.length == 2)
				Constants.testDataHash.put(parseTest[0], parseTest[1]);
			else {
				for(int count=1; count < parseTest.length; count++)
					parseTest[1] = parseTest[1] + parseTest[count];
				Constants.testDataHash.put(parseTest[0], parseTest[1]);
			}
		}
		return Constants.testDataHash;
	}
	
	public void writeTestResult(String testResultFolder, String testSuiteName, String[][] testResultArr) {
		
		//--Writing test result as separate sheet for each test suite in an excel workbook
		String testResultWorkbook = testResultFolder+"/"+Constants.testResultWorkbookName;
		try {
			File testResultExcel = new File(testResultWorkbook);
			if(!testResultExcel.exists()) {
				wWorkbook = Workbook.createWorkbook(new File(testResultWorkbook));
			}
			else {
				rWorkbook = Workbook.getWorkbook(new File(testResultWorkbook));
				wWorkbook = Workbook.createWorkbook(new File(testResultWorkbook), rWorkbook);
			}
			//--Creating new test result sheet for individual modules
			int numSheet = wWorkbook.getNumberOfSheets();
			wSheet = wWorkbook.createSheet(testSuiteName, numSheet+1);
			//--Creating column header name and view for test result sheet
			Label testCaseNameHeader = new Label(0,0,Constants.testResultHeaderColumn1);
			wSheet.setColumnView(0, 16);
			Label timeStampHeader = new Label(1,0,Constants.testResultHeaderColumn2);
			wSheet.setColumnView(1, 26);
			Label testStatusHeader = new Label(2,0,Constants.testResultHeaderColumn3);
			wSheet.setColumnView(2, 12);
			Label failScreenShotHeader = new Label(3,0,Constants.testResultHeaderColumn4);
			wSheet.setColumnView(3, 130);
			try {
				//--Writing column header name with view settings in test result sheet
				wSheet.addCell(testCaseNameHeader);
				wSheet.addCell(timeStampHeader);
				wSheet.addCell(testStatusHeader);
				wSheet.addCell(failScreenShotHeader);
				//--Writing test result array value in test result sheet
				for(int arrRow=0; arrRow<testResultArr.length; arrRow++) {
					for(int arrCol=0; arrCol<testResultArr[0].length; arrCol++)
					{
						//Applying cell formatter based on test result
						Label arrData = new Label(arrCol, arrRow+1, testResultArr[arrRow][arrCol],getCellFormat(testResultArr[arrRow][arrCol]));
						wSheet.addCell(arrData);
					}
				}
			} catch (WriteException e) {
				e.printStackTrace();
			}
			wWorkbook.write();
			wWorkbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (BiffException e1) {
			e1.printStackTrace();
		} 
	}
	
	public WritableCellFormat getCellFormat(String arr) throws WriteException {
		
		//--Formatting test result cell for PASS/FAIL condition
		WritableCellFormat cellFormat = new WritableCellFormat();
		if(arr.equalsIgnoreCase(Constants.testResultPass))
			cellFormat.setBackground(Colour.GREEN);
		else if(arr.equalsIgnoreCase(Constants.testResultFail))
			cellFormat.setBackground(Colour.RED);
		else
			cellFormat.setBackground(Colour.WHITE);
		cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.GRAY_25);
		return cellFormat;
	}

}