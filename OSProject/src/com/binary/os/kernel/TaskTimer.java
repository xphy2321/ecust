package com.binary.os.kernel;

import java.util.TimerTask;

public class TaskTimer extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Clock.ABSOLUTECLOCK++;
		Clock.RELATIVECLOCK -= Clock.CLOCKPERIOD;
		if(Clock.RELATIVECLOCK <= 0){
			//���̵���
			Clock.RELATIVECLOCK = Clock.CLOCKPERIOD * 5;
			ProcessManager.Schedule();
		}
		//CPUִ�м�������
		Kernel.CPU();
		
	}

}
