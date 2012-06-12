package com.binary.os.kernel;

import java.util.TimerTask;

import com.binary.os.device.IOControl;

public class TaskTimer extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Clock.ABSOLUTECLOCK++;
		
		if(Clock.RELATIVECLOCK <= 0){
			//���̵���
			Clock.RELATIVECLOCK = Clock.CLOCKPERIOD * 5;
			GlobalStaticVar.PSW = 1;
		}
		//CPUִ�м�������
		if(GlobalStaticVar.PID_NOW == -1){
			if(!PCBManager.readyQueue.isEmpty()){
				ProcessManager.Schedule();
				Clock.RELATIVECLOCK = Clock.CLOCKPERIOD * 5;
				Kernel.CPU();
			}
		}else{
			Kernel.CPU();
			Clock.RELATIVECLOCK -= Clock.CLOCKPERIOD;
		}
		
		IOControl.IORun();
	}

}
