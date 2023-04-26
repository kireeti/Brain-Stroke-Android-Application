package com;
public class test {
	public static void main(String args[]) {
		String str[] = {"1.0", "47.0", "0.0", "0.0", "75.3", "25.0", "1.0"};
		float value[][][][] = new float[1][7][1][1];
		value[0][0][0][0] = Float.parseFloat(str[0]);
		value[0][1][0][0] = Float.parseFloat(str[1]);
		value[0][2][0][0] = Float.parseFloat(str[2]);
		value[0][3][0][0] = Float.parseFloat(str[3]);
		value[0][4][0][0] = Float.parseFloat(str[4]);
		value[0][5][0][0] = Float.parseFloat(str[5]);
		value[0][6][0][0] = Float.parseFloat(str[6]);
		System.out.println(value);
	
}
}