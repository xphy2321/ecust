package com.binary.os.device;

import java.util.LinkedList;

public class DeviceGlobalVar {
	public static final int devA = 0; //A�豸���ͺ�Ϊ0,����Ϊ3
	public static final int devB = 1; //B�豸���ͺ�Ϊ0,����Ϊ2
	public static final int devC = 2; //C�豸���ͺ�Ϊ0,����Ϊ1
	
	public static int ABCTime[][] = {{-1,-1,-1}, {-1,-1}, {-1}};
	
	public static final int totalDev[] = {3, 2, 1};
	@SuppressWarnings("rawtypes")
	public static LinkedList devAWaitList = new LinkedList();
	@SuppressWarnings("rawtypes")
	public static LinkedList devBWaitList = new LinkedList();
	@SuppressWarnings("rawtypes")
	public static LinkedList devCWaitList = new LinkedList();
	
	@SuppressWarnings("rawtypes")
	public static LinkedList devAWaitTime = new LinkedList();
	@SuppressWarnings("rawtypes")
	public static LinkedList devBWaitTime = new LinkedList();
	@SuppressWarnings("rawtypes")
	public static LinkedList devCWaitTime = new LinkedList();
	
	public static int devCurrPid[][] = {{-1,-1,-1}, {-1,-1}, {-1}};
	
	public static int NUMOFAPPLYDEVICE = 0;
}
