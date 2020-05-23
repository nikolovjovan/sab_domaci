package rs.etf.sab.student.tests;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import rs.etf.sab.tests.CityOperationsTest;

public class CustomTestRunner {

    private static final int MAX_POINTS_NORMALIZED = 10;
    private static final Class<?>[] TEST_CLASSES = new Class<?>[] {
            // Original tests
            CityOperationsTest.class,
            // This test is bad since the insertUser does not set idAddress therefore it is fixed below...
            // UserOperationsTest.class,
            // Custom tests
            AddressOperationsTest.class,
            StockroomOperationsTest.class,
            // This is a modified copy of the original test which fixes interface bugs...
            UserOperationsTest.class
    };

    public static void runTests() {
        double points = 0;
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(new RunListener() {
            boolean failed;

            public void testStarted(Description description) {
                System.out.println("\nStarting test: " + description.getMethodName());
                failed = false;
            }

            public void testFinished(Description description) {
                if (!failed) {
                    System.out.println("Test PASSED!");
                }
            }

            public void testFailure(Failure failure) {
                System.out.println("Test FAILED!");
                failed = true;
            }
        });

        for (int i = 0; i < TEST_CLASSES.length; i++) {
            if (i > 0) System.out.println();
            System.out.println(TEST_CLASSES[i].getName());
            Request request = Request.aClass(TEST_CLASSES[i]);
            Result result = jUnitCore.run(request);
            int numberOfAllCases = result.getRunCount();
            int numberOfSuccessfulCases = numberOfAllCases - result.getFailureCount();
            System.out.println("Successful: " + numberOfSuccessfulCases);
            System.out.println("All: " + numberOfAllCases);
            points += (double) numberOfSuccessfulCases / (double) numberOfAllCases;
        }
        points = (points / TEST_CLASSES.length) * MAX_POINTS_NORMALIZED;

        System.out.println("Normalized points: " + points + " out of 10");
    }
}