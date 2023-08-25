package org.example;

public class Main{
    public static void main(String[] args) {
        Calculator calc=new Calculator();
        try{
            System.out.println("5+3 = "+calc.calculate("+",5,3));
            System.out.println("15-3 = "+calc.calculate("-",15,3));
            System.out.println("15*(-3) = "+calc.calculate("*",15,-3));
            System.out.println("15/0 = "+calc.calculate("/",15,0));
        }catch(CalculatorException e){
            System.out.println("Error: "+e.getMessage());
        }
    }
}