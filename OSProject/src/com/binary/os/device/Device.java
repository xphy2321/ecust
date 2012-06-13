package com.binary.os.device;


public class Device //�豸���̿�����
{
	@SuppressWarnings("unchecked")
	public static void addListItem(int deviceType, int pid,int time){
		switch(deviceType){
		case 0:
			DeviceGlobalVar.devAWaitList.add(pid);
			DeviceGlobalVar.devAWaitTime.add(time);
			break;
		case 1:
			DeviceGlobalVar.devBWaitList.add(pid);
			DeviceGlobalVar.devBWaitTime.add(time);
			break;
		case 2:
			DeviceGlobalVar.devCWaitList.add(pid);
			DeviceGlobalVar.devCWaitTime.add(time);
			break;
		}
    }
    
    public static int delListItem(int deviceType){
    	switch(deviceType){
		case 0:
			return (Integer) DeviceGlobalVar.devAWaitList.remove();
		case 1:
			return (Integer) DeviceGlobalVar.devBWaitList.remove();
		case 2:
			return (Integer) DeviceGlobalVar.devCWaitList.remove();
		}
		return 0;
    }
    
    public static int delProcessTime(int deviceType){
    	switch(deviceType){
		case 0:
			return (Integer) DeviceGlobalVar.devAWaitTime.remove();
		case 1:
			return (Integer) DeviceGlobalVar.devBWaitTime.remove();
		case 2:
			return (Integer) DeviceGlobalVar.devCWaitTime.remove();
		}
		return 0;
    }
    
	@SuppressWarnings("unused")
	public static boolean allocDevToProcess(int deviceType, int pid, int time){
		for(int i=0; i < DeviceGlobalVar.totalDev[deviceType] && DeviceGlobalVar.devCurrPid[deviceType][i] == -1; i++){
			DeviceGlobalVar.devCurrPid[deviceType][i] = pid;
			DeviceGlobalVar.ABCTime[deviceType][i] = time;
			return true;
		}
		return false;
	}
	
	public static void clearDevice(){
		
		DeviceGlobalVar.devAWaitList.clear();
		DeviceGlobalVar.devBWaitList.clear();
		DeviceGlobalVar.devCWaitList.clear();
		
		DeviceGlobalVar.devAWaitTime.clear();
		DeviceGlobalVar.devBWaitTime.clear();
		DeviceGlobalVar.devCWaitTime.clear();
			
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < DeviceGlobalVar.devCurrPid[i].length; j++){
				DeviceGlobalVar.devCurrPid[i][j] = -1;
				DeviceGlobalVar.ABCTime[i][j] = -1;
			}
	}
}
