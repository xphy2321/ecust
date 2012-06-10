package com.binary.os.kernel;

public class Kernel {

	public static void CPU(){
		
		while(true){
			
			if(GlobalStaticVar.PSW == 1){
				//�����жϴ���
				Interrupt();
				GlobalStaticVar.PSW = 0;
			}
			
			if(!GlobalStaticVar.DR.isEmpty()){
				
				GlobalStaticVar.IR = GlobalStaticVar.DR.poll();
				
				Command(GlobalStaticVar.IR);
			}
			
		}
		
	}
	
	public static void Interrupt(){
		//�жϴ�����
		
	}
	
	public static void Command(String IR){
		//����ִ��ָ��
		
	}
}
