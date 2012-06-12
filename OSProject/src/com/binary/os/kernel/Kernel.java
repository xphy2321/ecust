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
			//����IO�豸
			int time = IR.charAt(2);
			if(IR.charAt(1) == 'A'){
				//�����豸A
				
			}else if(IR.charAt(1) == 'B'){
				//�����豸B
				
			}else if(IR.charAt(1) == 'C'){
				//�����豸C
				
			}
			
		}else if(IR.charAt(1) == '='){
		
			GlobalStaticVar.Result = (byte)(Integer.parseInt(IR.charAt(2)+""));
		}else if(IR.charAt(1) == '+'){
			
			GlobalStaticVar.Result = GlobalStaticVar.Result++;
		}else if(IR.charAt(1) == '-'){
			
			GlobalStaticVar.Result = GlobalStaticVar.Result--;
		}
	}
}
