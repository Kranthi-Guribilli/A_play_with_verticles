package org.example;

import java.util.HashMap;
import java.util.Map;

public class Calculator {
    public Map<String,Operation> operations;
    public Calculator(){
        operations=new HashMap<>();
        operations.put("+",new AdditionOperation());
        operations.put("-",new SubtractionOperation());
        operations.put("*",new MultiplicationOperation());
        operations.put("/",new DivisionOperation());
    }
    public double calculate(String operator, double operand1, double operand2) throws CalculatorException {
        Operation operation=operations.get(operator);
        if(operation==null){
            throw new CalculatorException("Invalid Operator: "+operator);
        }
        return operation.perform(operand1,operand2);
    }
}
