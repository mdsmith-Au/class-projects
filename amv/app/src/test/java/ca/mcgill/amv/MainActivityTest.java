package ca.mcgill.amv;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    MainActivity activity;

    @Before
    public void setUp() {
        activity = new MainActivity();
    }

    @Test
    // Test 1
    public void testOneValueForAddition() {
        double[] result = activity.vectorAddition(new double[] {1.0, 2.0}, new double[] {0.0, 0.0}, new double[] {0.0, 0.0});

        assertEquals(result.length, 2);
        assertEquals(result[0], 1.0, 0.1);
        assertEquals(result[1], 2.0, 0.1);
    }

    @Test
    // Test 2
    public void testTwoValuesForAddition() {
        double[] result = activity.vectorAddition(new double[] {1.0, 2.0}, new double[] {3.0, 4.0}, new double[] {0.0, 0.0});

        assertEquals(result.length, 2);
        assertEquals(result[0], 4.0, 0.1);
        assertEquals(result[1], 6.0, 0.1);
    }

    @Test
    // Test 3
    public void testThreeValuesForAddition() {
        double[] result = activity.vectorAddition(new double[] {-1.0, -2.0}, new double[] {1.0, 3.0}, new double[] {2.0, 4.0});

        assertEquals(result.length, 2);
        assertEquals(result[0], 2.0, 0.1);
        assertEquals(result[1], 5.0, 0.1);
    }

    @Test
    // Test 4
    public void testScalarProduct() {
        double result = activity.scalarVectorProduct(new double[] {-1.0, 2.0}, new double[] {3.0, 7.5});

        assertEquals(result, 12, 0.1);
    }

    @Test
    // Test 5
    public void testCrossProduct() {
        double result = activity.crossVectorProduct(new double[] {-1.0, 2.0}, new double[] {3.0, 7.5});

        assertEquals(result, -13.5, 0.1);
    }

    @Test
    // Test 6
    public void testConvertPolarToCartesianCoordinates() {
        double[] result = activity.convertPolarToCartesianCoordinates(new double[] {-5.0, -50});

        assertEquals(result[0], -3.214, 0.1);
        assertEquals(result[1], 3.83, 0.1);
    }

    @Test
    // Test 7
    public void testAdditionModeSet() {
        activity.setAdditionMode();

        assertTrue(activity.isAdditionMode);
        assertFalse(activity.isScalarProductMode);
        assertFalse(activity.isCrossProductMode);
    }

    @Test
    // Test 8
    public void testScalarProductMode() {
        activity.setScalarProductMode();

        assertFalse(activity.isAdditionMode);
        assertTrue(activity.isScalarProductMode);
        assertFalse(activity.isCrossProductMode);
    }

    @Test
    // Test 9
    public void testCrossProductMode() {
        activity.setCrossProductMode();

        assertFalse(activity.isAdditionMode);
        assertFalse(activity.isScalarProductMode);
        assertTrue(activity.isCrossProductMode);
    }

    @Test
    // Test 10
     public void testPolarModeSet() {
        activity.setPolarMode();

        assertTrue(activity.inPolarMode);
    }

    @Test
    // Test 11
    public void testCartesianMode() {
        activity.setPolarMode();
        activity.setCartesianMode();

        assertFalse(activity.inPolarMode);
    }

    @Test
    // Test 12
     public void testVectorAdditionForOneCartesianValue() {
        activity.setCartesianMode();
        activity.setAdditionMode();
        String result = activity.calculate("1.0", "2.0", "", "", "", "");

        assertEquals("{1.0, 2.0}", result);
    }

    @Test
    // Test 13
    public void testVectorAdditionForTwoCartesianValue() {
        activity.setCartesianMode();
        activity.setAdditionMode();
        String result = activity.calculate("1.0", "2.0", "3.0", "4.0", "", "");

        assertEquals("{4.0, 6.0}", result);
    }

    @Test
    // Test 14
    public void testVectorAdditionForThreeCartesianValue() {
        activity.setCartesianMode();
        activity.setAdditionMode();

        String result = activity.calculate("-1.0", "-2.0", "1.0", "3.0", "2.0", "4.0");

        assertEquals("{2.0, 5.0}", result);
    }

    @Test
    // Test 15
    public void testScalarProductForCartesianVectors() {
        activity.setCartesianMode();
        activity.setScalarProductMode();

        String result = activity.calculate("-1.0", "2.0", "3.0", "7.5", "", "");

        assertEquals("12.0", result);
    }

    @Test
    // Test 16
    public void testCrossProductForCartesianVectors() {
        activity.setCartesianMode();
        activity.setCrossProductMode();

        String result = activity.calculate("-1.0", "2.0", "3.0", "7.5", "", "");

        assertEquals("-13.5", result);
    }

    @Test
    // Test 17
    public void testConvertCartesianToPolarCoordinates() {
        double[] result = activity.convertCartesianToPolarCoordinates(new double[] {-5.0, 5.0});

        assertEquals(7.071, result[0], 0.01);
        assertEquals(135, result[1], 0.1);
    }

    @Test
    // Test 18
    public void testVectorAdditionForOnePolarValue() {
        activity.setPolarMode();
        activity.setAdditionMode();
        String result = activity.calculate("5.0", "60.0", "", "", "", "");

        assertEquals("5.00∠60.00°", result);
    }

    @Test
    // Test 19
    public void testVectorAdditionForTwoPolarValue() {
        activity.setPolarMode();
        activity.setAdditionMode();
        String result = activity.calculate("5.0", "60.0", "2.0", "-30.0", "", "");

        assertEquals("5.39∠38.20°", result);
    }

    @Test
    // Test 20
    public void testVectorAdditionForThreePolarValue() {
        activity.setPolarMode();
        activity.setAdditionMode();
        String result = activity.calculate("5.6", "60.1", "2.11", "-30.0", "10.98", "22.1");

        assertEquals("16.78∠28.20°", result);
    }

    @Test
    // Test 21
    public void testScalarProductForPolarVectors() {
        activity.setPolarMode();
        activity.setScalarProductMode();
        String result = activity.calculate("5.6", "60.1", "2.11", "-30.0", "", "");

        assertEquals("-0.0206", result);
    }

    @Test
    // Test 22
    public void testCrossProductForPolarVectors() {
        activity.setPolarMode();
        activity.setCrossProductMode();
        String result = activity.calculate("5.6", "60.1", "2.11", "-30.0", "", "");

        assertEquals("-11.8160", result);
    }

    @Test
    // Test 28
    public void testInvalidInput() {

    }
}