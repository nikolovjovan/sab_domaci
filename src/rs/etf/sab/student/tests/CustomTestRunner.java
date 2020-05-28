package rs.etf.sab.student.tests;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class CustomTestRunner {

    private static final int MAX_POINTS_NORMALIZED = 10;
    private static final Class<?>[] TEST_CLASSES = new Class<?>[] {
            // PROVIDED TESTS
            rs.etf.sab.tests.CityOperationsTest.class,
            rs.etf.sab.tests.AddressOperationsTest.class,
            rs.etf.sab.tests.UserOperationsTest.class,
            rs.etf.sab.tests.CourierRequestOperationTest.class,
            rs.etf.sab.tests.StockroomOperationsTest.class,
            rs.etf.sab.tests.VehicleOperationsTest.class,
//            rs.etf.sab.tests.PublicModuleTest.class,
            // CUSTOM TESTS
            AddressOperationsTest.class,
            UserOperationsTest.class,
            CourierRequestOperationsTest.class,
            CourierOperationsTest.class,
            StockroomOperationsTest.class,
            PackageOperationsTest.class
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
            double pointsCurrent = (double) numberOfSuccessfulCases / (double) numberOfAllCases;
            System.out.println("Points: " + pointsCurrent);
            points += pointsCurrent;
        }
        points = (points / TEST_CLASSES.length) * MAX_POINTS_NORMALIZED;

        System.out.println("Normalized points: " + points + " out of 10");
    }
}