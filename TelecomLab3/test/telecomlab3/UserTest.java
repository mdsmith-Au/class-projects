package telecomlab3;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class UserTest {
    
    public UserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getUsername and setUsername methods, of class User.
     */
    @Test
    public void testGetAndSetUsername() {

        User instance = new User();
        instance.setUsername("Bob");
        String expResult = "Bob";
        String result = instance.getUsername();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPassword and getPassword methods, of class User.
     */
    @Test
    public void testGetAndSetPassword() {
        User instance = new User();
        String expResult = "thisIsAPassword";
        instance.setPassword("thisIsAPassword");
        String result = instance.getPassword();
        assertEquals(expResult, result);

    }

    /**
     * Test of setLogin and getLogin methods, of class User.
     */
    @Test
    public void testGetAndSetLogin() {

        boolean state = true;
        User instance = new User();
        instance.setLogin(state);
        assertEquals(state, instance.getLoginState());

    }
}
