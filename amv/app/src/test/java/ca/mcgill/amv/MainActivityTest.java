package ca.mcgill.amv;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testOneValueForAddition() {
        double[] result = MainActivity.vectorAddition(new double[] {1.0, 2.0}, new double[]{}, new double[]{});

        assertEquals(result.length, 2);
        assertEquals(result[0], 1.0, 0.1);
        assertEquals(result[1], 2.0, 0.1);
    }
}