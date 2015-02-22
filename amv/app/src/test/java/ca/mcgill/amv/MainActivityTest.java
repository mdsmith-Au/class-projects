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
    public void testOneValueForAddition() {
        double[] result = activity.vectorAddition(new double[] {1.0, 2.0}, new double[] {0.0, 0.0}, new double[] {0.0, 0.0});

        assertEquals(result.length, 2);
        assertEquals(result[0], 1.0, 0.1);
        assertEquals(result[1], 2.0, 0.1);
    }

    @Test
    public void testTwoValuesForAddition() {
        double[] result = activity.vectorAddition(new double[] {1.0, 2.0}, new double[] {3.0, 4.0}, new double[] {0.0, 0.0});

        assertEquals(result.length, 2);
        assertEquals(result[0], 4.0, 0.1);
        assertEquals(result[1], 6.0, 0.1);
    }

    @Test
    public void testThreeValuesForAddition() {
        double[] result = activity.vectorAddition(new double[] {-1.0, -2.0}, new double[] {1.0, 3.0}, new double[] {2.0, 4.0});

        assertEquals(result.length, 2);
        assertEquals(result[0], 2.0, 0.1);
        assertEquals(result[1], 5.0, 0.1);
    }

    @Test
    public void testScalarProduct() {
        double result = activity.scalarVectorProduct(new double[] {-1.0, 2.0}, new double[] {3.0, 7.5});

        assertEquals(result, 12, 0.1);
    }

    @Test
    public void testCrossProduct() {
        double result = activity.crossVectorProduct(new double[] {-1.0, 2.0}, new double[] {3.0, 7.5});

        assertEquals(result, -13.5, 0.1);
    }

    @Test
    public void testConvertPolarToCartesianCoordinates() {
        double[] result = activity.convertPolarToCartesianCoordinates(new double[] {-5.0, -50});

        assertEquals(result[0], -3.214, 0.1);
        assertEquals(result[1], 3.83, 0.1);
    }

    @Test
    public void testAdditionModeSet() {
        activity.setAdditionMode();

        assertTrue(activity.isAdditionMode);
        assertFalse(activity.isScalarProductMode);
        assertFalse(activity.isCrossProductMode);
    }

    @Test
    public void testScalarProductMode() {
        activity.setScalarProductMode();

        assertFalse(activity.isAdditionMode);
        assertTrue(activity.isScalarProductMode);
        assertFalse(activity.isCrossProductMode);
    }

    @Test
    public void testCrossProductMode() {
        activity.setCrossProductMode();

        assertFalse(activity.isAdditionMode);
        assertFalse(activity.isScalarProductMode);
        assertTrue(activity.isCrossProductMode);
    }

    @Test
     public void testPolarModeSet() {
        activity.setPolarMode();

        assertTrue(activity.inPolarMode);
    }

    @Test
    public void testCartesianMode() {
        activity.setPolarMode();
        activity.setCartesianMode();

        assertFalse(activity.inPolarMode);
    }

    @Test
     public void testVectorAdditionForOneCartesianValue() {
        activity.setCartesianMode();
        activity.setAdditionMode();
        String result = activity.calculate("1.0", "2.0", "", "", "", "");

        assertEquals("{1.0, 2.0}", result);
    }

    @Test
    public void testVectorAdditionForTwoCartesianValue() {
        activity.setCartesianMode();
        activity.setAdditionMode();
        String result = activity.calculate("1.0", "2.0", "3.0", "4.0", "", "");

        assertEquals("{4.0, 6.0}", result);
    }

    @Test
    public void testVectorAdditionForThreeCartesianValue() {
        activity.setCartesianMode();
        activity.setAdditionMode();

        String result = activity.calculate("-1.0", "-2.0", "1.0", "3.0", "2.0", "4.0");

        assertEquals("{2.0, 5.0}", result);
    }

    @Test
    public void testScalarProductForCartesianVectors() {
        activity.setCartesianMode();
        activity.setScalarProductMode();

        String result = activity.calculate("-1.0", "2.0", "3.0", "7.5", "", "");

        assertEquals("12.0", result);
    }

    @Test
    public void testCrossProductForCartesianVectors() {
        activity.setCartesianMode();
        activity.setCrossProductMode();

        String result = activity.calculate("-1.0", "2.0", "3.0", "7.5", "", "");

        assertEquals("-13.5", result);
    }

    @Test
    public void testConvertCartesianToPolarCoordinates() {
        double[] result = activity.convertCartesianToPolarCoordinates(new double[] {-5.0, 5.0});

        assertEquals(7.071, result[0], 0.01);
        assertEquals(135, result[1], 0.1);
    }

    @Test
    public void testVectorAdditionForOnePolarValue() {
        activity.setPolarMode();
        activity.setAdditionMode();
        String result = activity.calculate("5.0", "60.0", "", "", "", "");

        assertEquals("5.00∠60.00°", result);
    }

    @Test
    public void testVectorAdditionForTwoPolarValue() {
        activity.setPolarMode();
        activity.setAdditionMode();
        String result = activity.calculate("5.0", "60.0", "2.0", "-30.0", "", "");

        assertEquals("5.39∠38.20°", result);
    }

    @Test
    public void testVectorAdditionForThreePolarValue() {
        activity.setPolarMode();
        activity.setAdditionMode();
        String result = activity.calculate("5.6", "60.1", "2.11", "-30.0", "10.98", "22.1");

        assertEquals("16.78∠28.20°", result);
    }

    @Test
    public void testScalarProductForPolarVectors() {
        activity.setPolarMode();
        activity.setScalarProductMode();
        String result = activity.calculate("5.6", "60.1", "2.11", "-30.0", "", "");

        assertEquals("-0.0206", result);
    }

    @Test
    public void testCrossProductForPolarVectors() {
        activity.setPolarMode();
        activity.setCrossProductMode();
        String result = activity.calculate("5.6", "60.1", "2.11", "-30.0", "", "");

        assertEquals("-11.8160", result);
    }
}