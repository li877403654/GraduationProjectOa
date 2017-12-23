package cn.feie.oa.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

import org.junit.Test;

public class ActivitiTest {
	
	@Test
	public void test01(){
//		int[] num = new int[10];
//		number = "".strip().split();
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		TreeSet<Integer> tr = new TreeSet<>();
		for (int i = 0; i < n; i++) {
			tr.add(sc.nextInt());
		}
		Iterator<Integer> it = tr.iterator();
		int i = 0;
		while (it.hasNext()) {
			i++;
			if (i==3) {
				System.err.println(it.next());
				break;
			}else{
				System.out.println(it.next());
			}
		}
		if (i!=3) {
			System.out.println(-1);
		}
	}
	@Test
	public void test02(){
		
	}

}
			
