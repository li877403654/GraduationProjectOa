package cn.feie.oa.test;

public class Test01 extends Test02 {
	
	 int a=1;
	 double d=3.0;
	
	 void show () {
		 System.out.println("a = "+a+" d = "+d);
	 }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		  //继承
		  Test02 p =new Test01();
//		  p. show ();
//		  System.out.println(p.d);
		  
		  //byte
		  byte b1 =1,b2 =2,b3,b6;
		  final byte b4=4,b5=6;
		  b6 = b4+b5;
		  //b3 = b1+b2; 字符报错
		  
		  //StringBuffer
		  StringBuffer a=  new StringBuffer("A");
		  StringBuffer b=  new StringBuffer("B");
		  a.append(b);
		  System.out.println(a+" "+b);
	}

}
