package com.binary.os.kernel;

import com.binary.os.mem.MemGlobalVar;
import com.binary.os.mem.SystemMem;

public class ProcessManager {
	
	private static Process[] processes = new Process[10];
 	
	public static void Schedule(){
		//���̵��Ⱥ���
		//�����������г��򵽽��̿��ƿ�
		Process p = processes[GlobalStaticVar.PID_NOW];
		p.setMemInfo2(MemGlobalVar.PagePeek);
		p.setStatus(GlobalStaticVar.PCB_READY);
		p.setResult(GlobalStaticVar.Result);
		byte[] data;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < GlobalStaticVar.DR.size(); i++){
			sb.append(GlobalStaticVar.DR.poll());
		}
		String s = sb.toString();
		if(s != null && !s.equals("")){
			data = s.getBytes();
			p.saveDataBuffer(data);
		}
		PCBManager.addToReady(GlobalStaticVar.PID_NOW);
		GlobalStaticVar.PID_NOW = -1;
		//��������ѡ��һ������
		if(!PCBManager.readyQueue.isEmpty()){
			Process ps = processes[PCBManager.removeFromReady()];
			//�ָ�������̵ļĴ������ݵ������Ĵ���
			GlobalStaticVar.PID_NOW = ps.getPID();
			GlobalStaticVar.Result = ps.getResult();
			MemGlobalVar.StartNo = ps.getStartNo();
			MemGlobalVar.OffSet = ps.getBlockNum();
			MemGlobalVar.PagePeek = ps.getPageOffset();
			byte[] datas = ps.restoreDataBuffer();
			if(datas.length > 0){
				for(int i = 0; i < datas.length / 4; i++){
					byte[] temp = new byte[4];
					for(int j = 0; j < 4; j++){
						temp[j] = datas[i * 4 + j];
					}
					GlobalStaticVar.DR.add(new String(temp));
				}
			}
		}
	}

	public static int Create(byte[] data){
		//���̴���
		//��������ռ�,������ɹ���ת������
		//��ʼ�����̿��ƿ�
		if(GlobalStaticVar.PID_NUM <= 10){
			//�������ݵ��ڴ�
			//Ԥ�ȼ��
			if(Compile.checkCommand(data)){
				//�����ڴ�
				if(SystemMem.dispatch(data)){
					//����ɹ�
					Process p = new Process();
					//���ڴ������
					p.setMemInfo(MemGlobalVar.StartNo, MemGlobalVar.OffSet, (byte) 0);
					//��������
					p.setStatus(GlobalStaticVar.PCB_READY);
					PCBManager.addToReady(p.getPID());
					processes[p.getPID()] = p;
					return p.getPID();
				}
				return -1;//�ڴ�ռ䲻��
			}
			return -2;//����﷨
		}
		return -3;//�����������
	}
	
	public static void Destory(){
		//��������
		for(int i = 0; i < 32; i++){
			SystemMem.pcbs[GlobalStaticVar.PID_NOW * 32 + i] = 0;
		}
		GlobalStaticVar.PID_NUM--;
		GlobalStaticVar.PIDS[GlobalStaticVar.PID_NOW] = 0;
		GlobalStaticVar.PID_NOW = -1;
		GlobalStaticVar.PC = 0;
		for(int i =0; i < 3; i++){
			GlobalStaticVar.IR = null;
		}
	}
	
	public static void Block(){
		//��������
		Process p = processes[GlobalStaticVar.PID_NOW];
		//����cpu�ֳ�
		p.setMemInfo2(MemGlobalVar.PagePeek);
		p.setResult(GlobalStaticVar.Result);
		byte[] data;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < GlobalStaticVar.DR.size(); i++){
			sb.append(GlobalStaticVar.DR.poll());
		}
		String s = sb.toString();
		if(s != null && !s.equals("")){
			data = s.getBytes();
			p.saveDataBuffer(data);
		}
		p.setStatus(GlobalStaticVar.PCB_BLOCK);
		PCBManager.addToBlock(p.getPID());
	}
	
	public static void Wakeup(int pid){
		//���̻���
		if(PCBManager.blockQueue.size() > 0){
			PCBManager.removeFromBlock(pid);
			Process p = processes[pid];
			p.setPSW((byte) 0);
			p.setStatus(GlobalStaticVar.PCB_READY);
			PCBManager.addToReady(p.getPID());
		}
	}
	
	public static void Display(){
		//��ʾ
		
	}
}
