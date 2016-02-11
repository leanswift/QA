package test.java.com.leanswift.eConnect;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for driver script in Framework.
 */
public class ExecutionEngineTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ExecutionEngineTest(String testName)
    {
        super(testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(ExecutionEngineTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue(true);
    }
}
