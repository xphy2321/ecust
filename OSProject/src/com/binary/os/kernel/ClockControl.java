package com.binary.os.kernel;

import java.util.Timer;

import com.binary.os.device.Device;
import com.binary.os.filesys.manager.FileManager;
import com.binary.os.mem.SystemMem;
import com.binary.os.mem.UserMem;

public class ClockControl {

	private static Timer timer;
	
	public static void SystemStart(FileManager fm){
		//��ʼ��ϵͳ��Դ
		
		GlobalStaticVar.fm = fm;
		
		SystemMem.init();
		UserMem.init();
		
		timer = new Timer();
		timer.schedule(new TaskTimer(), 0, Clock.CLOCKPERIOD * 1000);
	}
	
	public static void SystemStop(){
		timer.cancel();
		//��������Դ
		
		Device.clearDevice();
	}
}
