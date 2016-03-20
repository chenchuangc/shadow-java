package com.weinong.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by yoke on 2015-10-19.
 */
public class WNDecimal {

    /**
     * 乘法
     *
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    /**
     * 除法
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal divide(BigDecimal a, BigDecimal b) {
        return a.divide(b, 4, BigDecimal.ROUND_HALF_UP);
    }

    private static DecimalFormat moneyFormat = new DecimalFormat(",###,###,##0.00");
    private static DecimalFormat otherFormat = new DecimalFormat(",###,###,###.##");
    private static DecimalFormat calculateFormat = new DecimalFormat("##############0.00");

    public static String formatMoney(BigDecimal a){
        return moneyFormat.format(a);
    }
    public static String format(BigDecimal a){
        return otherFormat.format(a);
    }

    public static String formatCalculate(BigDecimal a){
        return calculateFormat.format(a);
    }

    public static void main(String[] args) {
		 System.out.println(formatMoney(BigDecimal.valueOf(88889.8998)));
		 System.out.println(format(BigDecimal.valueOf(88889.8998)));
		 System.out.println(formatCalculate(BigDecimal.valueOf(8889.8998)));
		 System.out.println();
		 System.out.println(formatMoney(BigDecimal.valueOf(8889.999)));
		 System.out.println(format(BigDecimal.valueOf(88889.999)));
		 System.out.println(formatCalculate(BigDecimal.valueOf(8889.999)));
		 
//		 BigDecimal dd=  new BigDecimal(formatMoney(BigDecimal.valueOf(88889.8998)));
//		 System.out.println(dd);
	}
}
