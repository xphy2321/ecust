package com.binary.os.kernel;

import java.util.LinkedList;
import java.util.Queue;

public class GlobalStaticVar {

	public static int PSW = 0;//1:���ж�, 0���ж�
	public static String IR = null;//ָ��Ĵ�������һ����Ҫִ�е�ָ��
	public static int PC = 0;//���������
	public static Queue<String> DR = new LinkedList<String>();//���ݻ���Ĵ���
	
	public static int PCB_IDENTIFY = 0;
	
	public static int PCB_EMPTY = 0;
	public static int PCB_READY = 1;
	public static int PCB_BLOCK = 2;
}
