package ca.mcgill.amv;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {
    @Test
    public void testOneValueForAddition() {
        double[] result = MainActivity.vectorAddition(new double[] {1.0, 2.0}, new double[] {0.0, 0.0}, new double[] {0.0, 0.0});

        assertEquals(result.length, 2);
        assertEquals(result[0], 1.0, 0.1);
        assertEquals(result[1], 2.0, 0.1);
    }

    @Test
    public void testTwoValuesForAddition() {
        double[] result = MainActivity.vectorAddition(new double[] {1.0, 2.0}, new double[] {3.0, 4.0}, new double[] {0.0, 0.0});

        assertEquals(result.length, 2);
        assertEquals(result[0], 4.0, 0.1);
        assertEquals(result[1], 6.0, 0.1);
    }

    @Test
    public void testThreeValuesForAddition() {
        double[] result = MainActivity.vectorAddition(new double[] {-1.0, -2.0}, new double[] {1.0, 3.0}, new double[] {2.0, 4.0});

        assertEquals(result.length, 2);
        assertEquals(result[0], 2.0, 0.1);
        assertEquals(result[1], 5.0, 0.1);
    }

    @Test
    public void testScalarProduct() {
        double result = MainActivity.scalarVectorProduct(new double[] {-1.0, 2.0}, new double[] {3.0, 7.5});

        assertEquals(result, 12, 0.1);
    }

    @Test
    public void testCrossProduct() {
        double result = MainActivity.crossVectorProduct(new double[] {-1.0, 2.0}, new double[] {3.0, 7.5});

        assertEquals(result, -13.5, 0.1);
    }

    @Test
    public void testConvertPolarToCartesianCoordinates() {
        double[] result = MainActivity.convertPolarToCartesianCoordinates(new double[] {-5.0, -50});

        assertEquals(result[0], -3.214, 0.1);
        assertEquals(result[1], 3.83, 0.1);
    }

    @Test
    public void testAdditionModeSet() {
        MainActivity.setAdditionMode();

        assertTrue(MainActivity.isAdditionMode);
        assertFalse(MainActivity.isScalarProductMode);
        assertFalse(MainActivity.isCrossProductMode);
    }

    @Test
    public void testScalarProductMode() {
        MainActivity.setScalarProductMode();

        assertFalse(MainActivity.isAdditionMode);
        assertTrue(MainActivity.isScalarProductMode);
        assertFalse(MainActivity.isCrossProductMode);
    }

    @Test
    public void testCrossProductMode() {
        MainActivity.setCrossProductMode();

        assertFalse(MainActivity.isAdditionMode);
        assertFalse(MainActivity.isScalarProductMode);
        assertTrue(MainActivity.isCrossProductMode);
    }

    @Test
     public void testPolarModeSet() {
        MainActivity.setPolarMode();

        assertTrue(MainActivity.inPolarMode);
    }

    @Test
    public void testCartesianMode() {
        MainActivity.setPolarMode();
        MainActivity.setCartesianMode();

        assertFalse(MainActivity.inPolarMode);
    }

    @Test
     public void testVectorAdditionForOneCartesianValue() {
        MainActivity.setCartesianMode();
        MainActivity.setAdditionMode();
        String result = MainActivity.calculate("1.0", "2.0", "", "", "", "");

        assertEquals("{1.0, 2.0}", result);
    }

    @Test
    public void testVectorAdditionForTwoCartesianValue() {
        MainActivity.setCartesianMode();
        MainActivity.setAdditionMode();
        String result = MainActivity.calculate("1.0", "2.0", "3.0", "4.0", "", "");

        assertEquals("{4.0, 6.0}", result);
    }

    @Test
    public void testVectorAdditionForThreeCartesianValue() {
        MainActivity.setCartesianMode();
        MainActivity.setAdditionMode();

        String result = MainActivity.calculate("-1.0", "-2.0", "1.0", "3.0", "2.0", "4.0");

        assertEquals("{2.0, 5.0}", result);
    }

    @Test
    public void testScalarProductForCartesianVectors() {
        MainActivity.setCartesianMode();
        MainActivity.setScalarProductMode();

        String result = MainActivity.calculate("-1.0", "2.0", "3.0", "7.5", "", "");

        assertEquals("12.0", result);
    }

    @Test
    public void testCrossProductForCartesianVectors() {
        MainActivity.setCartesianMode();
        MainActivity.setCrossProductMode();

        String result = MainActivity.calculate("-1.0", "2.0", "3.0", "7.5", "", "");

        assertEquals("-13.5", result);
    }

    @Test
    public void testConvertCartesianToPolarCoordinates() {
        double[] result = MainActivity.convertCartesianToPolarCoordinates(new double[] {-5.0, 5.0});

        assertEquals(7.071, result[0], 0.01);
        assertEquals(135, result[1], 0.1);
    }

    @Test
    public void testVectorAdditionForOnePolarValue() {
        MainActivity.setPolarMode();
        MainActivity.setAdditionMode();
        String result = MainActivity.calculate("5.0", "60.0", "", "", "", "");

        assertEquals("5.00∠60.00°", result);
    }

    @Test
    public void testVectorAdditionForTwoPolarValue() {
        MainActivity.setPolarMode();
        MainActivity.setAdditionMode();
        String result = MainActivity.calculate("5.0", "60.0", "2.0", "-30.0", "", "");

        assertEquals("5.39∠38.20°", result);
    }

    @Test
    public void testVectorAdditionForThreePolarValue() {
        MainActivity.setPolarMode();
        MainActivity.setAdditionMode();
        String result = MainActivity.calculate("5.6", "60.1", "2.11", "-30.0", "10.98", "22.1");

        assertEquals("16.78∠28.20°", result);
    }

    @Test
    public void testScalarProductForPolarVectors() {
        MainActivity.setPolarMode();
        MainActivity.setScalarProductMode();
        String result = MainActivity.calculate("5.6", "60.1", "2.11", "-30.0", "", "");

        assertEquals("-0.0206", result);
    }

    @Test
    public void testCrossProductForPolarVectors() {
        MainActivity.setPolarMode();
        MainActivity.setCrossProductMode();
        String result = MainActivity.calculate("5.6", "60.1", "2.11", "-30.0", "", "");

        assertEquals("-11.8160", result);
    }
}