package com.binary.os.kernel;

public class ProcessManager {
	
	public static void Schedule(){
		//���̵��Ⱥ���
		
	}

	public static void Create(){
		//���̴���
		PCB p = new PCB();
		
		//��������ռ�,������ɹ���ת������
		
		//��ʼ�����̿��ƿ�
		p.setPid(GlobalStaticVar.PCB_IDENTIFY++);
		p.setStatus(GlobalStaticVar.PCB_EMPTY);
		
		//��������
		PCBManager.addToReady(p);
	}
	
	public static void Destory(){
		//��������
		
	}
	
	public static void Block(){
		//��������
		
	}
	
	public static void Wakeup(){
		//���̻���
		
	}
	
	public static void Display(){
		//��ʾ
		
	}
}
