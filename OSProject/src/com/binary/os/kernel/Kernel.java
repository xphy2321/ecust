package com.binary.os.kernel;

import com.binary.os.device.IOControl;

public class Kernel {

	public static void CPU(){

		if (GlobalStaticVar.PSW == 1) {
			// �����жϴ���
			Interrupt();
			GlobalStaticVar.PSW = 0;
		}

		if (GlobalStaticVar.DR.isEmpty()) {
			Register.loadDataToDataBuffer();
		}
		
		if(GlobalStaticVar.IR == null){
			Register.loadDataToIR();
		}
			
		GlobalStaticVar.IR = GlobalStaticVar.DR.poll();
		Command(GlobalStaticVar.IR);
		
		GlobalStaticVar.IR = null;
	}
	
	private static void Interrupt(){
		//�жϴ�����
		ProcessManager.Schedule();
	}
	
	private static void Command(String IR){
		
		GlobalStaticVar.PC++;
		
		//����ִ��ָ��
		if(IR.equals("end")){
			//���̽���
			//������
			GlobalStaticVar.fm.saveOut(GlobalStaticVar.PID_NOW, GlobalStaticVar.Result);
			//���������Դ
			ProcessManager.Destory();
		}else if(IR.charAt(0) == '!'){
			//����IO�豸
			int time = Integer.parseInt(IR.charAt(2)+"");
			int type = -1;
			if(IR.charAt(1) == 'A'){
				//�����豸A
				type = 0;
			}else if(IR.charAt(1) == 'B'){
				//�����豸B
				type = 1;
			}else if(IR.charAt(1) == 'C'){
				//�����豸C
				type = 2;
			}
			
			IOControl.ApplyIO(type, GlobalStaticVar.PID_NOW, time);
			ProcessManager.Block();
			
		}else if(IR.charAt(1) == '='){
		
			GlobalStaticVar.Result = (byte)(Integer.parseInt(IR.charAt(2)+""));
		}else if(IR.charAt(1) == '+'){
			
			GlobalStaticVar.Result = GlobalStaticVar.Result++;
		}else if(IR.charAt(1) == '-'){
			
			GlobalStaticVar.Result = GlobalStaticVar.Result--;
		}
	}
}
