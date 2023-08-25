import org.example.Calculator;
import org.example.CalculatorException;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorTest {
    private Calculator calculator;
    @BeforeEach
    public void setUp(){
        calculator=new Calculator();
    }
    @Test
    public void testAddition() throws CalculatorException {
        double result=calculator.calculate("+",2,6);
        assertEquals(8,result,0.001);
        assertNotEquals(5,calculator.calculate("+",3,-2),0.001);
        assertEquals(5,calculator.calculate("+",2,3),0.001);
        assertTrue(-5==calculator.calculate("+",-3,-2));
    }
    @Test
    public void testSubtraction() throws CalculatorException{
        assertEquals(5,calculator.calculate("-",18,13),0.001);
        assertEquals(5,calculator.calculate("-",3,-2),0.001);
        assertNotEquals(5,calculator.calculate("-",3,2),0.001);
        assertEquals(-1,calculator.calculate("-",2,3),0.001);
        assertTrue(-1==calculator.calculate("-",-3,-2));
        assertTrue(2==calculator.calculate("-",-1,-3));
    }
    @Test
    public void testMultiplication() throws CalculatorException{
        assertEquals(0,calculator.calculate("*",14,0),0.001);
        assertEquals(15,calculator.calculate("*",-5,-3),0.001);
        assertFalse(12==calculator.calculate("*",4,-3));
        assertTrue(12==calculator.calculate("*",-4,-3));
        assertTrue(-12==calculator.calculate("*",4,-3));
    }
    @Test
    public void testDivision() throws CalculatorException{
        assertEquals(2,calculator.calculate("/",10,5),0.001);
        assertNotEquals(2,calculator.calculate("/",5,10),0.001);
        assertEquals(20.4,calculator.calculate("/",102,5),0.001);
        assertTrue(2==calculator.calculate("/",6.6,3.3));
        assertThrows(CalculatorException.class, ()->calculator.calculate("/",15,0));
    }
    @Test
    public void testInvalidOperator() throws CalculatorException{
        assertThrows(CalculatorException.class,()->calculator.calculate("$",5,2));
    }
}
