package org.example;

public class SubtractionOperation implements Operation{
    @Override
    public double perform(double operand1, double operand2){
        return operand1-operand2;
    }
}
