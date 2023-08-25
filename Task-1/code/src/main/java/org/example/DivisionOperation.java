package org.example;

public class DivisionOperation implements Operation{
    @Override
    public double perform(double operand1, double operand2) throws CalculatorException{
        if(operand2==0){
            throw new CalculatorException("Division is not possible by zero.");
        }return operand1/operand2;
    }
}
