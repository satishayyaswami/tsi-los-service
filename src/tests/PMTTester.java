package tests;

public class PMTTester {

    public static void main(String[] args){
        long loanAmt = 160000;
        long roi = 9;
        long timePeriod = 24;

        double loanAmtD = loanAmt;
        double roiD = roi;
        int timePeriodI = (int) timePeriod;

        double emi = (loanAmtD * (roiD/12)/100 * Math.pow((1+(roiD/12)/100),timePeriodI))/(Math.pow(1+(roiD/12)/100, timePeriodI)-1);
        System.out.println("emi:"+Math.ceil(emi));
        double interest = Math.round(100000*(31/365)*(18/100));
        System.out.println("interest:"+interest);
    }
}
