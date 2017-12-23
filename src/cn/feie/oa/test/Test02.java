package cn.feie.oa.test;

import java.util.Scanner;


public class Test02 {
	static int i  = 10;
	public static void main(String[] args) {
		System.out.println(i);
//		int[] i  = {1,2,3,4,5};
//		int[] k = {6,7,8,9,10};
//			System.arraycopy(i,0,k,1,4);
//		for (int a = 0;a<i.length;a++) {
//			System.out.println(i[a]+" "+k[a]);		
//		}
//		Scanner s= new Scanner(System.in);
//	}
//	int a=1;
//	double d=2.0;
//	void show () {
//		System.out.println("a = "+a+" d = "+d);
//		
//	Dept d1 = new Dept();
//	Dept d2 = new Dept(1,"名字");
//	
//	System.out.println(d1.a);
//	System.out.println(d2.a);
//}
	int num = 12345;
	Scanner s = new Scanner(System.in);
	int a = s.nextInt();
	int b = s.nextInt();
	int[] ary = new int[(a+"").length()];
	int[] bry = new int[(b+"").length()];
	for(int i = ary.length-1;i>=0;i--){
		ary[i] = a%10;
		a /= 10;
	}
	for(int i = bry.length-1;i>=0;i--){
		bry[i] = b%10;
		b /= 10;
	}
	
	
	
}
}
