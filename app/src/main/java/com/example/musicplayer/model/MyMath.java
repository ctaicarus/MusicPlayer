package com.example.musicplayer.model;

import java.util.Random;

public class MyMath {
    private int first; // so 1
    private int second; // so 2
    private String operator;
    private float result;
    public MyMath(int first, int second, String operator) {
        this.first = first;
        this.second = second;
        this.operator = operator;
        if(operator.equals("+")) result=first+second;
        if(operator.equals("-")) result=first-second;
        if(operator.equals("*")) result=first*second;
        if(operator.equals("/")) result=first/second;
        if(operator.equals("^")) result= (float) Math.pow(first,second);
    }

    @Override
    public String toString() {
        return first+" "+operator+" "+second;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }

    public static MyMath autoGenerateExpressionTwoNumber(int MIN, int MAX, String operators){
        Random random=new Random();
        int first=random.nextInt(MAX-MIN)+MIN;
        int second=random.nextInt(MAX-MIN)+MIN;
        String operator=operators.charAt(random.nextInt(operators.length()))+"";
        return new MyMath(first,second,operator);
    }
}