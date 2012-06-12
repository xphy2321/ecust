package com.binary.os.kernel;

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
		
		if(GlobalStaticVar.IR.isEmpty() || GlobalStaticVar.IR == null){
			Register.loadDataToIR();
		}
			
		GlobalStaticVar.IR = GlobalStaticVar.DR.poll();
		Command(new String(GlobalStaticVar.IR));
		
		GlobalStaticVar.IR = null;
	}
	
	public static void Interrupt(){
		//�жϴ�����
		
	}
	
	public static void Command(String IR){
		//����ִ��ָ��
		if(IR.equals("end")){
			//���̽���
			//������
			
			//���������Դ
			GlobalStaticVar.PSW = -1;
		}else if(IR.charAt(0) == '!'){
			
			
		}else if(IR.charAt(1) == '='){
		
			GlobalStaticVar.Result = (byte) IR.charAt(2);
		}else if(IR.charAt(1) == '+'){
			
			GlobalStaticVar.Result = GlobalStaticVar.Result++;
		}else if(IR.charAt(1) == '-'){
			
			GlobalStaticVar.Result = GlobalStaticVar.Result--;
		}
	}
}
