package com.binary.os.kernel;

import java.util.LinkedList;
import java.util.Queue;

public class GlobalStaticVar {

	public static byte PSW = 0;//1:���ж�, 0���ж�
	public static String IR = null;//ָ��Ĵ�������һ����Ҫִ�е�ָ��
	public static int PC = 0;//���������
	public static Queue<String> DR = new LinkedList<String>();//���ݻ���Ĵ���   		4*4 
	
	public static byte[] PIDS= {0,0,0,0,0,0,0,0,0,0};
	public static byte PID_NUM = 0;
	public static byte PID_NOW = -1;
	
	public static byte PCB_EMPTY = 0;
	public static byte PCB_READY = 1;
	public static byte PCB_BLOCK = 2;
	
	public static byte Result = 0;
}
