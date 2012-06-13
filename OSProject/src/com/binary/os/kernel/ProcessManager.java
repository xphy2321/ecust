package com.binary.os.kernel;

import com.binary.os.mem.MemGlobalVar;
import com.binary.os.mem.SystemMem;
import com.binary.os.mem.UserMem;


public class ProcessManager {
	
	private static Process[] processes = new Process[10];
 	
	public static void Schedule(){
		//���̵��Ⱥ���
		//�����������г��򵽽��̿��ƿ�
		if(GlobalStaticVar.PID_NOW == -1){
			
			if(!PCBManager.readyQueue.isEmpty()){
				GlobalStaticVar.PID_NOW = (byte) PCBManager.removeFromReady();
				Process ps = processes[GlobalStaticVar.PID_NOW];
				//�ָ�������̵ļĴ������ݵ������Ĵ���
				GlobalStaticVar.Result = ps.getResult();
				GlobalStaticVar.ProcessStartNo = ps.getStartNo();
				GlobalStaticVar.ProcessOffSet = ps.getBlockNum();
				GlobalStaticVar.ProcessPagePeek = ps.getPageOffset();
				GlobalStaticVar.PC = ps.getPC();
				
				UserMem.count = 0;
				GlobalStaticVar.DR.clear();
				GlobalStaticVar.IR = null;
				
				byte[] datas = ps.restoreDataBuffer();
				if(datas.length > 0){
					for(int i = 0; i < datas.length / 4; i++){
						if(datas[i * 4] == 0)
							continue;
						byte[] temp = new byte[4];
						for(int j = 0; j < 4; j++){
							temp[j] = datas[i * 4 + j];
						}
						String com = new String(temp);
						GlobalStaticVar.DR.add(com);
					}
				}
				
				ps.clearDataBufferArea();
			}
			
		}
		else{
			
			Process p = processes[GlobalStaticVar.PID_NOW];
			p.setPageOffset(GlobalStaticVar.ProcessPagePeek);
			p.setStatus(GlobalStaticVar.PCB_READY);
			p.setResult(GlobalStaticVar.Result);
			p.setPC(GlobalStaticVar.PC);
			byte[] data;
			StringBuilder sb = new StringBuilder();
			int size = GlobalStaticVar.DR.size();
			for(int i = 0; i < size; i++){
				sb.append(GlobalStaticVar.DR.remove());
			}
			String s = sb.toString();
			if(s != null && !s.equals("")){
				data = s.getBytes();
				p.saveDataBuffer(data);
			}
			PCBManager.addToReady(GlobalStaticVar.PID_NOW);
			//��������ѡ��һ������
			UserMem.count = 0;
			GlobalStaticVar.DR.clear();
			GlobalStaticVar.IR = null;
			
			if(!PCBManager.readyQueue.isEmpty()){
				GlobalStaticVar.PID_NOW = (byte) PCBManager.removeFromReady();
				Process ps = processes[GlobalStaticVar.PID_NOW];
				//�ָ�������̵ļĴ������ݵ������Ĵ���
				GlobalStaticVar.Result = ps.getResult();
				GlobalStaticVar.ProcessStartNo = ps.getStartNo();
				GlobalStaticVar.ProcessOffSet = ps.getBlockNum();
				GlobalStaticVar.ProcessPagePeek = ps.getPageOffset();
				GlobalStaticVar.PC = ps.getPC();
				byte[] datas = ps.restoreDataBuffer();
				if(datas.length > 0){
					for(int i = 0; i < datas.length / 4; i++){
						if(datas[i * 4] == 0)
							continue;
						byte[] temp = new byte[4];
						for(int j = 0; j < 4; j++){
							temp[j] = datas[i * 4 + j];
						}
						GlobalStaticVar.DR.add(new String(temp));
					}
				}
				
				ps.clearDataBufferArea();
			}
		}
	}

	public static void Create(byte[] data){
		//����һ���������з�ֹӰ��ԭ�Ӳ���
		GlobalStaticVar.createApply.add(data);
	}
	
	public static void newProcess(byte[] data){
		//���̴���
		//��������ռ�,������ɹ���ת������
		//��ʼ�����̿��ƿ�
		if(GlobalStaticVar.PID_NUM <= 10){
			//�������ݵ��ڴ�
			//Ԥ�ȼ��
			if(Compile.checkCommand(data)){
				//�����ڴ�
				
				if(SystemMem.dispatch(Compile.filterCommand(data))){
					//����ɹ�
					Process p = new Process();
					//���ڴ������
					p.setMemInfo(MemGlobalVar.MemStartNo, MemGlobalVar.MemOffSet, (byte) 0);
					//��������
					p.setStatus(GlobalStaticVar.PCB_READY);
					PCBManager.addToReady(p.getPID());
					processes[p.getPID()] = p;
					
					MemGlobalVar.ResetMemStaticVar();
					
					GlobalStaticVar.ProcessCreateListener = p.getPID();
					return ;
				}
				GlobalStaticVar.ProcessCreateListener = -1;//�ڴ�ռ䲻��
				return ;
			}
			GlobalStaticVar.ProcessCreateListener = -2;//����﷨
			return ;
		}
		GlobalStaticVar.ProcessCreateListener = -3;//�����������
		return ;
	}
	
	public static void Destory(){
		//��������
		
		for(int i = 0; i < 32; i++){
			SystemMem.pcbs[GlobalStaticVar.PID_NOW * 32 + i] = 0;
		}
		
		SystemMem.recycle();
		
		GlobalStaticVar.PIDS[GlobalStaticVar.PID_NOW] = 0;
		
		GlobalStaticVar.PID_NOW = -1;
		GlobalStaticVar.DR.clear();
		GlobalStaticVar.PC = 0;
		GlobalStaticVar.IR = null;
		GlobalStaticVar.PID_NUM--;
		GlobalStaticVar.Result = 0;
		UserMem.count = 0;
		
		GlobalStaticVar.ResetGlobalStaticVar();
		
	}
	
	public static void Block(){
		//��������
		Process p = processes[GlobalStaticVar.PID_NOW];
		//����cpu�ֳ�
		p.setPageOffset(GlobalStaticVar.ProcessPagePeek);
		p.setResult(GlobalStaticVar.Result);
		p.setPC(GlobalStaticVar.PC);
		byte[] data;
		StringBuilder sb = new StringBuilder();
		int size = GlobalStaticVar.DR.size();
		for(int i = 0; i < size; i++){
			sb.append(GlobalStaticVar.DR.remove());
		}
		String s = sb.toString();
		if(s != null && !s.equals("")){
			data = s.getBytes();
			p.saveDataBuffer(data);
		}
		p.setStatus(GlobalStaticVar.PCB_BLOCK);
		
		System.out.println("block "+GlobalStaticVar.PID_NOW);
		
		PCBManager.addToBlock(p.getPID());
		GlobalStaticVar.PID_NOW = -1;
		GlobalStaticVar.DR.clear();
		GlobalStaticVar.PC = 0;
		GlobalStaticVar.IR = null;
		GlobalStaticVar.Result = 0;
		UserMem.count = 0;
		
		GlobalStaticVar.ResetGlobalStaticVar();
	}
	
	public static void Wakeup(int pid){
		//���̻���
		if(PCBManager.blockQueue.size() > 0){
			PCBManager.removeFromBlock(pid);
			Process p = processes[pid];
			p.setStatus(GlobalStaticVar.PCB_READY);
			PCBManager.addToReady(p.getPID());
			
			System.out.println("wake up "+pid);
		}
	}
}
