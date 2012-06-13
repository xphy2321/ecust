package com.binary.os.kernel;

import java.util.LinkedList;

import com.binary.os.filesys.manager.FileManager;

public class GlobalStaticVar {

	public static byte PSW = 0;//1:���ж�, 0���ж�
	public static String IR = null;//ָ��Ĵ�������һ����Ҫִ�е�ָ��
	public static byte PC = 0;//���������
	public static LinkedList<String> DR = new LinkedList<String>();//���ݻ���Ĵ���   		4*4 
	
	public static byte[] PIDS= {0,0,0,0,0,0,0,0,0,0};
	public static byte PID_NUM = 0;
	public static byte PID_NOW = -1;
	
	public static byte PCB_EMPTY = 0;
	public static byte PCB_READY = 1;
	public static byte PCB_BLOCK = 2;
	
	public static byte Result = 0;
	
	public static FileManager fm = null;
	
	public static byte ProcessStartNo = 0;//ҳ����
	public static byte ProcessOffSet = 0;//ռ���ڴ����
	public static byte ProcessPagePeek = 0;//ҳ���α�
	
	public static void ResetGlobalStaticVar(){
		ProcessStartNo = 0;
		ProcessOffSet = 0;
		ProcessPagePeek = 0;
	}
	
	public static int ProcessCreateListener = -4;
	public static LinkedList createApply = new LinkedList();
}
