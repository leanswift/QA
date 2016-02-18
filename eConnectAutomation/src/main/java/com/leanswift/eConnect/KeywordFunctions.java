package main.java.com.leanswift.eConnect;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.javascript.host.dom.Document;

import main.java.com.leanswift.eConnect.Constants;
import main.java.com.leanswift.eConnect.ExecutionEngine;

public class KeywordFunctions {

	static WebDriver driver;
	static WebDriverWait wait;
	
	//--Function to get locator type
	private String getLocatorType(String locatorName) {
		String[] locatorTokens = locatorName.split("_");
		return locatorTokens[0];
	}
	
	//--Function to get locator value
	private String getLocatorValue(String locatorName) {
		Properties property = new Properties();
		InputStream is = null;
		try {
			//is = new FileInputStream(ExecutionEngine.getPath("ObjectRepository")+"/OR.txt");
			is = new FileInputStream(ExecutionEngine.getPath("ObjectRepository")+"/OR.txt");
			property.load(is);
		} catch(IOException e) {
			System.out.println(e.getMessage());
		}
		return property.getProperty(locatorName);
	}
	
	//Function to get type of a WebElement
	private String getWebElmentType(String locatorName)
	{
		String[] locatorTokens = locatorName.split("_");
		return locatorTokens[3];
	}
	
	//--Function to get fully qualified locator name
	private By locatorValue(String locatorType, String locatorValue) {
		By by;
		switch (locatorType.toUpperCase()) {
		case "ID":
			by = By.id(locatorValue);
			break;
		case "NAME":
			by = By.name(locatorValue);
			break;
		case "XPATH":
			by = By.xpath(locatorValue);
			break;
		case "CSS":
			by = By.cssSelector(locatorValue);
			break;
		case "LINKTEXT":
			locatorValue = this.getDataValue(locatorValue, Constants.testDataHash);
			by = By.linkText(locatorValue);
			break;
		case "PARTIALLINKTEXT":
			locatorValue = this.getDataValue(locatorValue, Constants.testDataHash);
			by = By.partialLinkText(locatorValue);
			break;
		default:
			by = null;
			break;
		}
		return by;
	}
	
	//--Function to get value of the data name
	public String getDataValue(String testData, HashMap<String, String> testDataHash) {
		String value = null;
		Set<String> setOfKeys = testDataHash.keySet();
		Iterator<String> iterator = setOfKeys.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if(key.equalsIgnoreCase(testData)) {
				value = testDataHash.get(key);
			}
		}
		return value;
	}
	
	//--Function to open a browser
	public void openBrowser(String browserName) {
		browserName = this.getDataValue(browserName, Constants.testDataHash);
		System.out.println("Opening the " +browserName+" driver");
		try {
			if (browserName.equalsIgnoreCase("Firefox")) {
				driver = new FirefoxDriver();
			} else if (browserName.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver", ExecutionEngine.getPath("webDriverServerPath")+"/chromedriver.exe");
				driver = new ChromeDriver();
			} else if (browserName.equalsIgnoreCase("IE")) {
				/*Prerequisite - Go to IE browser -> Settings -> Internet Options -> Security tab -> 
				Check/Uncheck protected mode uniformly for all zones*/
				System.setProperty("webdriver.ie.driver", ExecutionEngine.getPath("webDriverServerPath")+"/IEDriverServer.exe");
				DesiredCapabilities dc = new DesiredCapabilities();
				//dc.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				dc.setCapability("ie.ensureCleanSession", true);
				driver = new InternetExplorerDriver(dc);
			}
			this.cleanUpVariables();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}

	//--Function to navigate to specified URL
	public void navigateToURL(String URL) {
		URL = this.getDataValue(URL, Constants.testDataHash);
		System.out.println("Navigating to the URL: " +URL);
		try {
			driver.navigate().to(URL);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}

	//--Function to click an object
	public void clickObject(String locatorName) {
		System.out.println("Clicking the object:" +locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			//new Actions(driver).moveToElement(element).perform();
			element.click();
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}

	}
	
	//--Function to clear the text box value
	public void clearText(String locatorName) {
		System.out.println("Clearing the text field:" +locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			element.clear();
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}

	}
	
	//--Function to enter value in text box
	public void inputText(String locatorName, String textValue) {
		textValue = this.getDataValue(textValue, Constants.testDataHash);
		System.out.println("Entering the text: "+textValue+" in the textbox: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			element.sendKeys(textValue);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to quit browser
	public void quitBrowser() {
		try {
			this.waits();
			System.out.println("Closing the browser driver");
			driver.quit();
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to verify element text with expected value
	public void verifyElementText(String locatorName, String expectedValue) {
		expectedValue = this.getDataValue(expectedValue, Constants.testDataHash);
		System.out.println("Verifying the text of element: "+locatorName+" with expected value: "+expectedValue);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			if(element.getText().equalsIgnoreCase(expectedValue.trim()))
				System.out.println("Element's text: "+element.getText().trim()+" is matched with expected value: "+expectedValue);
			else {
				Constants.isProceed = false;
				System.out.println("Element's text: "+element.getText().trim()+" is not matched with expected value: "+expectedValue);
			}
			Thread.sleep(3000);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to verify element text with expected value. This method uses getAttribute('value') to fetch value of text box.
	// This method can be used when the text box is grayed out and when getText method returns no value or fails
	public void verifyElementTextUsingGetAttribute(String locatorName, String expectedValue)
	{
		expectedValue = this.getDataValue(expectedValue, Constants.testDataHash);
		System.out.println("Verifying the text of element: "+locatorName+" with expected value: "+expectedValue);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			if(element.getAttribute("value").equalsIgnoreCase(expectedValue.trim()))
				System.out.println("Element's text: "+element.getAttribute("value").trim()+" is matched with expected value: "+expectedValue);
			else {
				Constants.isProceed = false;
				System.out.println("Element's text: "+element.getAttribute("value").trim()+" is not matched with expected value: "+expectedValue);
			}
			Thread.sleep(3000);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	
	//--Function to verify the element text is not equal to expected value
	public void verifyElementTextNotEquals(String locatorName, String expectedValue) {
		expectedValue = this.getDataValue(expectedValue, Constants.testDataHash);
		System.out.println("Verifying the text of element: "+locatorName+" not matching with value: "+expectedValue);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			if(!element.getText().equalsIgnoreCase(expectedValue.trim()))
				System.out.println("Element's text: "+element.getText().trim()+" is not matched with value: "+expectedValue+" as expected");
			else {
				Constants.isProceed = false;
				System.out.println("Element's text: "+element.getText().trim()+" is matched with expected value: "+expectedValue+" not as expected");
			}
			Thread.sleep(3000);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to verify whether element text contains expected value or vice versa
	public void verifyElementContainsText(String locatorName, String expectedValue) {
		expectedValue = this.getDataValue(expectedValue, Constants.testDataHash);
		System.out.println("Verifying the text of element: "+locatorName+" contains expected value: "+expectedValue);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			if(element.getText().trim().contains(expectedValue.trim()) || expectedValue.trim().contains(element.getText().trim()))
				System.out.println("Element's text contains the expected value: "+expectedValue);
			else {
				Constants.isProceed = false;
				System.out.println("Element's text does not contain the expected value: "+expectedValue);
			}
			Thread.sleep(3000);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to verify element's attribute value
	public void verifyElementAttribute(String locatorName, String expectedValue) {
		expectedValue = this.getDataValue(expectedValue, Constants.testDataHash);
		String[] expectedValues = expectedValue.split(":");
		System.out.println("Verifying the attribute: "+expectedValues[0]+" of element: "+locatorName+" with expected value: "+expectedValues[1]);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			if(element.getAttribute(expectedValues[0]).contains(expectedValues[1].trim()))
				System.out.println("Element's attribute value is matched with expected value");
			else {
				Constants.isProceed = false;
				System.out.println("Element's attribute value is not matched with expected value");
			}
			Thread.sleep(3000);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	

	//--Function to store element's text in temp variable
	public void storeElementText(String locatorName) {
		System.out.println("Storing the text of element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			Constants.tempVar = element.getText().trim();
			System.out.println("Element(s) text stored in temporary value is/are: \n"+Constants.tempVar);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to store element's text in temp variable using JavaScript
	public void storeElementTextJS(String locatorName) {
		System.out.println("Storing the text of element: "+locatorName);
		try {
			String JSScript = "document.getElementById('"+this.getLocatorValue(locatorName)+"').value;";
			Object elementText =((JavascriptExecutor)driver).executeScript(JSScript);
			Constants.tempVar = elementText.toString();
			System.out.println("Element text stored in temporary value is: "+Constants.tempVar);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to input temp variable's value in the text box
	public void inputStoredValue(String locatorName) {
		System.out.println("Entering stored value: "+Constants.tempVar+" in the element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			element.sendKeys(Constants.tempVar);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to compare temp variable's value with element's text
	public void compareWithStoredValue(String locatorName) {
		System.out.println("Verifying stored value: "+Constants.tempVar+" against text of the element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			if(element.getText().trim().equalsIgnoreCase(Constants.tempVar))
				System.out.println("Element's text: "+element.getText().trim()+" matched with expected value: "+Constants.tempVar);
			else {
				Constants.isProceed = false;
				System.out.println("Element's text: "+element.getText().trim()+" does not matched with expected value: "+Constants.tempVar);
			}
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to verify whether element's text contains temp variable value or vice versa
	public void containsStoredValue(String locatorName) {
		System.out.println("Verifying stored value: "+Constants.tempVar+" against text of the element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			if(element.getText().trim().contains(Constants.tempVar) || Constants.tempVar.contains(element.getText().trim()))
				System.out.println("Element's expected text is subset of the stored value:\n"+Constants.tempVar+"\nor vice versa as expected");
			else {
				Constants.isProceed = false;
				System.out.println("Element's expected text is not subset of the stored value:\n"+Constants.tempVar+"\nor vice versa not as expected");
			}
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to perform enter keyboard action
	public void performKeyboardEnter(String locatorName) {
		System.out.println("Performing enter keyboard action on element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			element.sendKeys(Keys.ENTER);
			Thread.sleep(3000);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to wait for element visibility
	public void waitForElementVisibility(String locatorName) {
		System.out.println("Waiting for element to be located: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebDriverWait wait = new WebDriverWait(driver, 15);
			if(wait.until(ExpectedConditions.visibilityOfElementLocated(locator))!=null)
				System.out.println("Wait until element presence located is successful");
			else {
				Constants.isProceed = false;
				System.out.println("Wait until element presence located is unsuccessful");
			}
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to select a drop down value
	public void selectInput(String locatorName, String value) {
		value = this.getDataValue(value, Constants.testDataHash);
		System.out.println("Selecting the input: "+value+" in the element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			Select sel = new Select(driver.findElement(locator));
			sel.selectByVisibleText(value);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to switch to frame
	public void switchToFrame(String locatorName) {
		System.out.println("Switching to frame element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			driver.switchTo().frame(element);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to switch back to main window
	public void backToWindow() {
		System.out.println("Switching back to main window");
		try {
			driver.switchTo().defaultContent();
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to wait for 5 secs
	public void waits() throws InterruptedException {
		System.out.println("Including hard delay");
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to open a new tab and switch to it
	public void openANewTab(String browserName) {
		browserName = this.getDataValue(browserName, Constants.testDataHash);
		System.out.println("Opening a new tab in the browser: "+browserName);
		try {
			driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL+"t");
			ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
			if(browserName.equalsIgnoreCase("Firefox"))
				driver.switchTo().window(tabs.get(0));
			else if(browserName.equalsIgnoreCase("Chrome"))
				driver.switchTo().window(tabs.get(1));
			else if(browserName.equalsIgnoreCase("IE")) {
				driver.switchTo().window(driver.getWindowHandle());
			}
			else
				System.out.println("Unsupported browser");
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to switch to next tab
	public void switchToNextTab(String browserName) {
		browserName = this.getDataValue(browserName, Constants.testDataHash);
		System.out.println("Switching to next tab in the browser: "+browserName);
		try {
			driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"\t");
			ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
			if(browserName.equalsIgnoreCase("Firefox"))
				driver.switchTo().window(tabs.get(0));
			else if(browserName.equalsIgnoreCase("Chrome"))
				driver.switchTo().window(tabs.get(1));
			else if(browserName.equalsIgnoreCase("IE")) {
				//--IE tab switching code comes here
			}
			else
				System.out.println("Unsupported browser");
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to return to previous tab
	public void returnToPreviousTab(String browserName) {
		browserName = this.getDataValue(browserName, Constants.testDataHash);
		System.out.println("Returning to previous tab in the browser: "+browserName);
		try {
			//driver.switchTo().window(Constants.windowHandle);
	        driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL +"\t");
	        ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
	        if(browserName.equalsIgnoreCase("Firefox"))
	        	driver.switchTo().defaultContent();
	        else if(browserName.equalsIgnoreCase("Chrome"))
	        	driver.switchTo().window(tabs.get(0));
	        else if(browserName.equalsIgnoreCase("IE")) {
	        	//--IE tab switching code comes here
	        }
	        else
				System.out.println("Unsupported browser");
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}

	//--Function to perform double click action
	public void performDoubleClick(String locatorName) {
		System.out.println("Performing double click on the element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			Actions act = new Actions(driver);
			act.doubleClick(element).build().perform();
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to maximize window
	public void maximizeWindow() {
		System.out.println("Maximizing the window");
		try {
			driver.manage().window().maximize();
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}	
	}
	
	//--Function to store present window handle
	public void storeWindowHandle() {
		System.out.println("Storing handle of present window");
		try {
			Constants.windowHandle = driver.getWindowHandle();
			System.out.println("Window handle of present window captured is: "+Constants.windowHandle);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to print out as message
	public void displayMessage(String messageText) {
		messageText = this.getDataValue(messageText, Constants.testDataHash);
		try {
			System.out.println("----------"+messageText+"----------");
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to switch window by handle
	public void switchToWindowByHandle() {
		System.out.println("Switching to new window by handle stored");
		try {
			driver.switchTo().window(Constants.windowHandle);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to switch to window by index
	public void switchToWindowByIndex(String indexNum) {
		indexNum = this.getDataValue(indexNum, Constants.testDataHash);
		int index = Integer.parseInt(indexNum);
		System.out.println("Switching to new window by index: "+indexNum);
		try {
			ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
			driver.switchTo().window(tabs.get(index));
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to switch to parent window
	public void backToParentWindow() {
		System.out.println("Switching to new window");
		try {
			driver.switchTo().defaultContent();
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to store element's value in temporary list variable
	public void storeValuesInTempList(String locatorName) {
		System.out.println("Storing the value of the element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			Constants.tempList.add(element.getAttribute("value"));
			System.out.println(Constants.tempList.get(Constants.tempList.size()-1));
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to store element's text in temporary list variable
	public void storeTextInTempList(String locatorName) {
		System.out.println("Storing the text of the element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			Constants.tempList.add(element.getText());
			System.out.println(Constants.tempList.get(Constants.tempList.size()-1));
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to input store temp list value in text box based on its input position
	public void inputTempListValues(String locatorName, String posList) {
		posList = this.getDataValue(posList, Constants.testDataHash);
		System.out.println("Input the temp list value at: "+posList+" in the element: "+locatorName);
		int posListNum = Integer.parseInt(posList);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			element.sendKeys(Constants.tempList.get(posListNum));
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to perform move to element action
	public void performMoveToElement(String locatorName) {
		System.out.println("Performing mouse hover on the element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			Actions actn = new Actions(driver);
			actn.moveToElement(element).build().perform();
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to clear temporary values
	private void cleanUpVariables() {
		System.out.println("Clearing temp values");
		try {
			Constants.tempList.clear();
			Constants.tempVar="";
			Constants.appendVar="";
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to verify temporary list variable against element's value based on its list position
	public void verifyTempListValues(String locatorName, String posList) {
		System.out.println("Verifying the value of the element: "+locatorName);
		posList = this.getDataValue(posList, Constants.testDataHash);
		int posListNum = Integer.parseInt(posList);
		System.out.println("Verifying the value present at position: "+posList+" in the tempList");
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			if(element.getAttribute("value").trim().equalsIgnoreCase(Constants.tempList.get(posListNum)))
				System.out.println("Element value: "+element.getAttribute("value").trim()+" matches with stored value: "+Constants.tempList.get(posListNum));
			else {
				Constants.isProceed = false;
				System.out.println("Element value: "+element.getAttribute("value").trim()+" does not match with stored value: "+Constants.tempList.get(posListNum));
			}
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//--Function to verify temporary list variable against element's text based on its list position
	public void verifyTempListTexts(String locatorName, String posList) {
		System.out.println("Verifying the value of the element: "+locatorName);
		posList = this.getDataValue(posList, Constants.testDataHash);
		int posListNum = Integer.parseInt(posList);
		System.out.println("Verifying the value present at position: "+posList+" in the tempList");
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			if(element.getText().trim().contains(Constants.tempList.get(posListNum)) || Constants.tempList.get(posListNum).contains(element.getText().trim()))
				System.out.println("Element value: "+element.getText().trim()+" is subset of the stored value: "+Constants.tempList.get(posListNum)+" or vice versa as expected");
			else {
				Constants.isProceed = false;
				System.out.println("Element value: "+element.getText().trim()+" is not subset of the stored value: "+Constants.tempList.get(posListNum)+" or vice versa not as expected");
			}
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	//Function to check a webElement is not present in a webpage
	public void elementPresent(String locatorName,String expectedValue)
	{
		By locator;
		expectedValue = this.getDataValue(expectedValue, Constants.testDataHash);
		System.out.println("Checking whether "+locatorName+" is present");
	    locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
	     Boolean iselementpresent = (driver.findElements(locator)).size()!= 0;
			if(Boolean.toString(iselementpresent).equals(expectedValue))
				System.out.println("Visibility of Element: "+locatorName+" matches with expected value");
			else
			{
				Constants.isProceed = false;
				System.out.println("Visibility of Element: "+locatorName+" do not match with expected value");
			}		
	}
	
	//Function to highlight a Webelement in a webpage. Mostly used for debugging purpose
    public void elementHighlight(String locatorName) {
    	By locator;
    	locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
    	WebElement element = driver.findElement(locator);
    	for (int i = 0; i < 2; i++) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].setAttribute('style', arguments[1]);",element, "color: yellow; border: 3px solid yellow;");
			js.executeScript("arguments[0].setAttribute('style', arguments[1]);",element, "");
			}
    	}
    

   
    //Function to get Text from Text box/Labels field and append the values in to a single String
   public void getAndAppendValues(String locatorName)
   {
	   String[] locatorNames=locatorName.split(",");
		By locators[] = new By[locatorNames.length];
		
   	try{
   		//get values and store it as a single string
   		for(int i=0;i<locatorNames.length;i++){
   			locators[i] =locatorValue(this.getLocatorType(locatorNames[i]), this.getLocatorValue(locatorNames[i]));
   			if(getWebElmentType(locatorNames[i]).equals("Label"))
   				Constants.appendVar += driver.findElement(locators[i]).getText();
   			else
   				Constants.appendVar += driver.findElement(locators[i]).getAttribute("value");
   		}
   	}catch(Exception e)
    	{
    		Constants.isProceed = false;
			System.out.println(e.getMessage());
    	}
   }
   
   //Set the appendVar to empty string
   public void cleanUpAppendValues()
   {
	   Constants.appendVar=""; 
   }
   
   //Function to store the value present in tempList at certain position to temporary variable
   public void storeValueFromTempList(String posList)
   {
	   try{
	   posList = this.getDataValue(posList, Constants.testDataHash);
		System.out.println("Store the value present in position: "+posList+" to a temporary variable");
		int posListNum = Integer.parseInt(posList);
		Constants.tempVar = Constants.tempList.get(posListNum);
		System.out.println("Value Stored in Temporary Variable: "+Constants.tempVar);
	   }catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
	   }
   }
   
   //refresh the page
   public void refreshPage()
   {
	   driver.navigate().refresh();
   }
   
   //click ok button in the alert window thrown by a browser
   public void acceptAlert() {
	    try {
	        wait.until(ExpectedConditions.alertIsPresent());
	        System.out.println("Clicking Ok on the alert window");
	     driver.switchTo().alert().accept();
	    } catch (Exception e) {
	    	driver.switchTo().alert().accept();
			System.out.println(e.getMessage());
	    }
	}
   
    //Application specific functions
	//******************************
	
	/*This function is M3H5 client specific to perform contextual click action - right click + keyboard operation such as Control+<numeric>
	 * instead of selecting option from contextual menu displayed. It is used in all contextual click + action operation in M3H5.
	 * */
	public void performContextualAction(String locatorName, String actionCode) {
		actionCode = this.getDataValue(actionCode, Constants.testDataHash);
		System.out.println("Performing the action 'Ctrl+:"+actionCode+"' on the element: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			Actions builder = new Actions(driver);
			builder.contextClick(element)
			.sendKeys(Keys.RIGHT)
			.keyDown(Keys.CONTROL).sendKeys(actionCode)
			.keyUp(Keys.CONTROL)
			.build().perform();
			Thread.sleep(3000);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	 /*Function to compare Appended String values and values stored in tmp variable by removing white spaces and ",". 
	  * Specifically used to compare M3 Address with Magento*/
    public void CompareAppendedValueWithStoredValue(String value)
    {
    	Constants.appendVar = this.getDataValue(value, Constants.testDataHash) + Constants.appendVar;
    	try{
    		//removing White spaces and "," 
    		Constants.tempVar = Constants.tempVar.replaceAll("\\s","").replaceAll("," , "").replace("T:", "");
    		Constants.appendVar = Constants.appendVar.replaceAll("\\s","").replaceAll("," , "");
    		//Compare values
    		if(Constants.appendVar.trim().equals(Constants.tempVar.trim()))
    			System.out.println("Element's text: "+Constants.appendVar.trim()+" matches with expected value: "+Constants.tempVar);
    		else{
    			Constants.isProceed = false;
    			System.out.println("Element's text: "+Constants.appendVar.trim()+" do not matches with expected value: "+Constants.tempVar);
    		}
    	}catch(Exception e)
    	{
    		Constants.isProceed = false;
			System.out.println(e.getMessage());
    	}
    }
    
	/*This function is Magento admin specific that delete all existing records under specific table like locator to add new records thereafter.
	 * It is used for eConnect form filling scenarios. Button position indicate the column number in which delete button exist.
	 */
	public void cleanUpRecords(String locatorName, String buttonPosition) {
		buttonPosition = this.getDataValue(buttonPosition, Constants.testDataHash);
		System.out.println("Cleaning up of records matching the elements: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			List<WebElement> element = driver.findElements(locator);
			for(int i=2; i < element.size(); i++) {
				String locatorValue = this.getLocatorValue(locatorName)+"[*]/td["+buttonPosition+"]/button";
				System.out.println(locatorValue);
				WebElement locator_element = driver.findElement(By.xpath(locatorValue));
				if(locator_element.isDisplayed()){
					locator_element.sendKeys("");
					locator_element.click();
					}
			}
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}

	
    //Function to Expand the head nodes if it is in collapsed state. Magento Admin specific function.
    public void expandHeadNode(String locatorName)
    {
		try {
			By locator1,locator2;
			String locatorName1,locatorName2;
			//Split and Store 2 locator Names in each variable
			String[] locators = locatorName.split(",");
			locatorName1=locators[0];
			locatorName2=locators[1];
			System.out.println("Checking if Head "+locatorName1+" is expanded");
			
			locator1 = locatorValue(this.getLocatorType(locatorName1), this.getLocatorValue(locatorName1));
			locator2 = locatorValue(this.getLocatorType(locatorName2), this.getLocatorValue(locatorName2));
			WebElement locator_element = driver.findElement(locator2);
			//check whether first element under Head Node is displayed
			if(!locator_element.isDisplayed()){
				driver.findElement(locator1).click();
				System.out.println("Expanding Head Node "+locatorName1);
			}
			else
				System.out.println("Head Node "+locatorName1+ " is already expanded");
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
    }
	
	/*This function is used to append a 4 digit random number to a static email address to represent an unique email id during new customer registration
	 * in Magento user portal. It generates a random email address and input in text field object.
	 */
	public void inputRandomEmail(String locatorName, String emailAddr) {
		emailAddr = this.getDataValue(emailAddr, Constants.testDataHash);
		System.out.println("Enter random generated email address in the textbox: "+locatorName);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			Random rnd = new Random();
			int n = 1000 + rnd.nextInt(9999);
			emailAddr = emailAddr+n+"@gmail.com";
			element.sendKeys(emailAddr);

		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	/*This function will increment the final order number fetched from Magento admin and input the incremented final order number in customer order
	 * search tool box in M3H5 client. It is used in case of exchange scenarios. posList indicates position of final order # in temp list variable.
	 */
	public void inputTempListNextValue(String locatorName, String posList) {
		posList = this.getDataValue(posList, Constants.testDataHash);
		System.out.println("Increment and input the temp list value at: "+posList+" in the element: "+locatorName);
		int posListNum = Integer.parseInt(posList);
		try {
			By locator;
			locator = locatorValue(this.getLocatorType(locatorName), this.getLocatorValue(locatorName));
			WebElement element = driver.findElement(locator);
			int nextValue = Integer.parseInt(Constants.tempList.get(posListNum))+1;
			String incrementedOrderNum = "000"+Integer.toString(nextValue);
			element.sendKeys(incrementedOrderNum);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}
	
	/*This function converts the ATP date format fetched from Magento user portal into M3H5 specific date format used in Material Plan scenario.
	 * posList indicates position of final order # in temp list variable.
	 */
	public void convertM3DateFormat(String posList) {
		posList = this.getDataValue(posList, Constants.testDataHash);
		System.out.println("Convert to M3 date format in temp list value at: "+posList);
		int posListNum = Integer.parseInt(posList);
		try {
			String dateValue = Constants.tempList.get(posListNum);
			String[] dateValues = dateValue.split("/");
			dateValues[2]=dateValues[2].substring(2);
			Constants.tempList.set(posListNum, dateValues[1]+dateValues[0]+dateValues[2]);
		} catch (Exception e) {
			Constants.isProceed = false;
			System.out.println(e.getMessage());
		}
	}

	
}