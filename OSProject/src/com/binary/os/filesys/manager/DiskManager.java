package com.binary.os.filesys.manager;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedList;

import com.binary.os.filesys.blocks.Block;
import com.binary.os.filesys.blocks.DirectoryBlock;
import com.binary.os.filesys.blocks.EmptyIndexBlock;
import com.binary.os.filesys.blocks.IndexBlock;
import com.binary.os.filesys.blocks.SuperBlock;
import com.binary.os.filesys.dentries.Dentry;
import com.binary.os.filesys.dentries.Directory;
import com.binary.os.filesys.dentries.RootDirectory;
import com.binary.os.filesys.dentries.SFile;
import com.binary.os.utils.ByteContainer;


public class DiskManager {

	public static String DISK_PATH = "disk";
	public static int BYTE_PER_BLOCK = 128;
	
	private SuperBlock sBlock;
	
	public DiskManager(){
		sBlock = new SuperBlock(readBlock(0));
	}
	
	private byte[] readBlock(int no){
		byte[] content = new byte[BYTE_PER_BLOCK];
		RandomAccessFile disk = null;
		try {
			File diskFile = new File(DISK_PATH);
			disk  = new RandomAccessFile(diskFile,"rw");
			disk.seek(no*BYTE_PER_BLOCK);
			disk.read(content);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(disk!=null){
				try {
					disk.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}
	
	private boolean saveBlock(Block block){
		int no = block.getNo();
		RandomAccessFile disk = null;
		try {
			File diskFile = new File(DISK_PATH);
			disk  = new RandomAccessFile(diskFile,"rw");
			disk.seek(no*BYTE_PER_BLOCK);
			disk.write(block.toByte());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			if(disk!=null){
				try {
					disk.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private int getAnEmptyBlock(){
		int index;
		if(sBlock.getEmptyCount()==1){//��ǰջ�����һ���ɷ����̿��
			index = sBlock.getEmptystack()[0];
			if(index == 0){//0Ϊ��������β
				return index;
			}
			EmptyIndexBlock eib = new EmptyIndexBlock(index, readBlock(index));
			sBlock.setEmptystack(eib.getEmptystack());
			sBlock.setEmptyCount(eib.getEmptyCount());
		}else{
			index = sBlock.pop();
		}
		saveSuperBlock();
		return index;
	}
	
	private boolean addAnEmptyBlock(int index){
		boolean isOK = true;
		if(index == 0){//0Ϊ��������β
			return false;
		}
		if(sBlock.getEmptyCount()==10){
			EmptyIndexBlock eib = new EmptyIndexBlock(index);
			eib.setEmptyCount(sBlock.getEmptyCount());
			eib.setEmptystack(sBlock.getEmptystack());
			saveBlock(eib);
			int[] stack = new int[10];
			stack[0] = index;
			sBlock.setEmptystack(stack);
			sBlock.setEmptyCount(1);
		}else{
			isOK = sBlock.push(index);
		}
		saveSuperBlock();
		return isOK;
	}
	
	private LinkedList<Integer> getEmptyBlock(int count){
		LinkedList<Integer> indexs = new LinkedList<Integer>();
		for(int i=0; i<count; i++){
			int index = getAnEmptyBlock();
			if(index == 0){//0Ϊ��������β
				break;
			}
			indexs.add(index);
		}
		if(indexs.size() < count){
			addEmptyBlock(indexs);
			indexs.clear();
		}
		return indexs;
	}
	
	private boolean addEmptyBlock(LinkedList<Integer> indexs){
		while(!indexs.isEmpty()){
			if(addAnEmptyBlock(indexs.pollLast())==false){//���Ż��տ�
				return false;
			}
		}
		return true;
	}
	
	private boolean saveSuperBlock(){
		sBlock.setModified(false);
		return saveBlock(sBlock);
	}
		
	public void readFile(SFile file){

		ByteContainer content = new ByteContainer();
				
		int index = file.getDirectAddr1();//ֱ�ӵ�ַ1
		if(index > 0){//����ֱ�ӵ�ַ1
	
			content.add(readBlock(index));
			
			index = file.getDirectAddr2();//ֱ�ӵ�ַ2
			if(index > 0){//����ֱ�ӵ�ַ2
				
				content.add(readBlock(index));
			
				index = file.getLev1Index();//һ��������ַ
				if(index > 0){//����һ��������ַ

					IndexBlock iBlock = new IndexBlock(index, readBlock(index));//һ��������
					
					for(int i:iBlock.getIndexList()){
						content.add(readBlock(i));
					}
			
					index = file.getLev2Index();//����������ַ
					if(index > 0){//���ڶ���������ַ

						iBlock = new IndexBlock(index, readBlock(index));//һ��������
						
						for(int i:iBlock.getIndexList()){
							
							IndexBlock iBlock2 = new IndexBlock(i, readBlock(i));//����������
							ArrayList<Integer> indexList2 = iBlock2.getIndexList();
							
							for(int j:indexList2){
								content.add(readBlock(j));
							}	
						}
					}
				}
			}
		}
		file.setContent(content.get());
	}
	
	public void readDirectory(Directory dir){
		
		ArrayList<Dentry> dentryList = dir.getDentryList();
		dentryList.clear();//�������
		
		int index = dir.getDirectAddr1();
		if(index>0){
			DirectoryBlock dBlock = new DirectoryBlock(index, readBlock(index));
			dentryList.addAll(dBlock.getDentryList());
			
			index = dir.getDirectAddr2();
			if(index>0){
				dBlock = new DirectoryBlock(index, readBlock(index));
				dentryList.addAll(dBlock.getDentryList());
			
				index = dir.getLev1Index();
				if(index>0){
					IndexBlock iBlock = new IndexBlock(index, readBlock(index));
					
					for(int i:iBlock.getIndexList()){
						dBlock = new DirectoryBlock(i, readBlock(i));
						dentryList.addAll(dBlock.getDentryList());
					}
					
					index = dir.getLev2Index();
					if(index>0){
						iBlock = new IndexBlock(index, readBlock(index));
						
						for(int i:iBlock.getIndexList()){
							IndexBlock iBlock2 = new IndexBlock(i, readBlock(i));
	
							for(int j:iBlock2.getIndexList()){
								dBlock = new DirectoryBlock(j, readBlock(j));
								dentryList.addAll(dBlock.getDentryList());
							}	
						}
					}
				}
			}
		}
	}
	
	public boolean saveDentry(Dentry dentry){
		
		recycleDentry(dentry);//д�ļ�ǰ�Ȱ��ļ�ԭ��ռ�õĿ�������·���
		
		int fileSize = dentry.getSize();//�ļ��ֽ���
		int count = (int) Math.ceil(fileSize/128f);//��Ҫʵ�����
		if(count == 0){
			return true;
		}
		ByteContainer fileContent = new ByteContainer(dentry.toByte());
		
		int scount = count;//ȫ����Ҫ�Ŀ�
		int tcount = count;//����������
		if(tcount>0){
			tcount = tcount - 2;//����ֱ�ӿ�
			if(tcount>0){
				scount++;//��һ��һ��������
				tcount = tcount - 32;//һ��������32��ʵ���
				if(tcount>0){
					scount = scount + 2;//�ڶ���һ���������һ��������ĵ�һ������������
					tcount = tcount - 32;//һ��������ĵ�һ�������������32��ʵ���
					while(tcount>0){
						scount++;//����һ������������
						tcount = tcount - 32;//ÿ������������������32��ʵ���
					}
				}
			}
		}
		
		LinkedList<Integer> indexs = getEmptyBlock(scount);//Ԥ�Ȼ�ȡ��Ҫ�Ŀ��п�
		if(indexs.size() == 0){//����������п�ʧ��
			return false;
		}
		
		if(count > 0){//ֱ��1
		
			int index = indexs.poll();
			dentry.setDirectAddr1(index);//����ַд���ļ�
			Block block = new Block(index, fileContent.poll(BYTE_PER_BLOCK));
			saveBlock(block);
			
			count = count - 1;//����һ��
			
			if(count > 0){//ֱ��2
				
				index = indexs.poll();
				dentry.setDirectAddr2(index);//����ַд���ļ�
				block = new Block(index, fileContent.poll(BYTE_PER_BLOCK));
				saveBlock(block);
				
				count = count - 1;//����һ��
				
				if(count > 0){//һ�μ��
					
					index = indexs.poll();
					dentry.setLev1Index(index);//��һ��������ַд���ļ�
					IndexBlock iBlock = new IndexBlock(index);//һ�μ�ӵ�������
					
					tcount = count;//һ������ʵ����Ҫ��ʵ�����
					if(tcount > 32){//��಻�ܳ���32��
						tcount = 32;
					}
			
					for(int i=0; i<tcount; i++){
						index = indexs.poll();
						iBlock.addIndex(index);//�������ż���������
						block = new Block(index, fileContent.poll(BYTE_PER_BLOCK));
						saveBlock(block);
					}
			
					saveBlock(iBlock);//д��һ��������
					
					count = count - 32;//һ������������32��ʵ���
					
					if(count > 0){//�������
						
						index = indexs.poll();
						dentry.setLev2Index(index);//������������ַд���ļ�
						iBlock = new IndexBlock(index);//���μ�ӵĵ�һ��������
						
						while(count>0){//������ʣ�������Ҫ���µĶ���������
							index = indexs.poll();
							iBlock.addIndex(index);//���ڶ���������ż����һ��������
							
							IndexBlock iBlock2 = new IndexBlock(index);//�ڶ���������
							
							tcount = count;//��������ʵ����Ҫ��ʵ�����
							if(tcount > 32){//��಻�ܳ���32��
								tcount = 32;
							}
							
							for(int i=0;i<tcount; i++){
								index = indexs.poll();
								iBlock2.addIndex(index);//��ʵ���ż���ڶ�������
								block = new Block(index, fileContent.poll(BYTE_PER_BLOCK));
								saveBlock(block);//�ڶ���������ʵ���
							}
							saveBlock(iBlock2);//���ڶ���������д�����
							
							count = count - 32;//һ������������32��ʵ���
						}
						saveBlock(iBlock);//����һ��������д�����
					}
				}
			}
		}
		
		return true;
	}
	
	public boolean saveRoot(RootDirectory root){
		boolean isOk = true;
		
		ByteContainer fileContent = new ByteContainer(root.toByte());
		
		Block block = new Block(1, fileContent.poll(BYTE_PER_BLOCK));
		if(!saveBlock(block)){
			isOk = false;
		}
			
		block = new Block(2, fileContent.poll(BYTE_PER_BLOCK));
		if(!saveBlock(block)){
			isOk = false;
		}

		return isOk;
	}
	
	public boolean recycleDentry(Dentry dentry){
		
		LinkedList<Integer> indexs = new LinkedList<Integer>();//�����տ�Ŷ���
				
		int index = dentry.getDirectAddr1();//ֱ�ӵ�ַ1
		if(index > 0){//����ֱ�ӵ�ַ1
	
			indexs.add(index);
			dentry.setDirectAddr1(0);//ֱ�ӵ�ַ1��0
			
			index = dentry.getDirectAddr2();//ֱ�ӵ�ַ2
			if(index > 0){//����ֱ�ӵ�ַ2
				
				indexs.add(index);
				dentry.setDirectAddr2(0);//ֱ�ӵ�ַ2��0
			
				index = dentry.getLev1Index();//һ��������ַ
				if(index > 0){//����һ��������ַ
					
					indexs.add(index);
					dentry.setLev1Index(0);//һ��������ַ��0

					IndexBlock iBlock = new IndexBlock(index, readBlock(index));//һ��������
					ArrayList<Integer> indexList = iBlock.getIndexList();
					
					for(int i:indexList){
						indexs.add(i);
					}
			
					index = dentry.getLev2Index();//����������ַ
					if(index > 0){//���ڶ���������ַ

						indexs.add(index);
						dentry.setLev2Index(0);//����������ַ��0
						
						iBlock = new IndexBlock(index, readBlock(index));//һ��������
						indexList = iBlock.getIndexList();
						
						IndexBlock iBlock2;
						ArrayList<Integer> indexList2;
						for(int i:indexList){
							
							indexs.add(i);
							
							iBlock2 = new IndexBlock(i, readBlock(i));//����������
							indexList2 = iBlock2.getIndexList();
							
							for(int j:indexList2){
								indexs.add(j);
							}	
						}
					}
				}
			}
		}
		
		return addEmptyBlock(indexs);
	}
	
	public boolean format(){
		
		//��մ���
		byte[] content = new byte[255*128];
		RandomAccessFile disk = null;
		try {
			File diskFile = new File(DISK_PATH);
			disk  = new RandomAccessFile(diskFile,"rw");
			disk.seek(0);
			disk.write(content);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally{
			if(disk!=null){
				try {
					disk.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//��ʼʱ������
		SuperBlock sBlock = new SuperBlock();
		sBlock.format();
		saveBlock(sBlock);
		
		//��ʼÿ�����������飬�����һ��
		for(int i=12; i<243; i=i+10){
			EmptyIndexBlock eib = new EmptyIndexBlock(i);
			int[] temp = new int[10];
			for(int j=0; j<10; j++){
				temp[j] =  i + 10 - j;
			}
			eib.setEmptystack(temp);
			eib.setEmptyCount(10);
			saveBlock(eib);
		}
		
		//���һ������������ֻ����������
		EmptyIndexBlock eib = new EmptyIndexBlock(252);
		int[] temp = new int[10];
		temp[0] = 0;
		temp[1] = 254;
		temp[2] = 253;
		eib.setEmptystack(temp);
		eib.setEmptyCount(3);
		saveBlock(eib);
		
		this.sBlock = new SuperBlock(readBlock(0));//���¶�ȡSuperBlock
		
		return true;
	}
	
	public boolean[] getUsage(){
		boolean[] usage = new boolean[255];//ʹ���������
		for(int i=0; i<255; i++){//��ʼ��ȫΪ��ʹ��
			usage[i] = true;
		}
		
		int emptyCount = sBlock.getEmptyCount();//������
		int[] emptyStack = sBlock.getEmptystack();//���п��ջ

		int index = 0;
		do{
			for(int i=emptyCount-1; i>=0; i--){//��ջ��ÿ�����п�Ŷ�Ӧ��ʹ�����Ϊfalse;
				index = emptyStack[i];
				if(index == 0){//��Ϊ0 ��Ϊ��β
					break;
				}
				usage[index] = false;
			}
			if(index != 0){
				EmptyIndexBlock eib = new EmptyIndexBlock(index, readBlock(index));//��ȡ��һ������������
				emptyStack = eib.getEmptystack();//���¿��п��ջ
				emptyCount = eib.getEmptyCount();//���¿�����
			}
		}while(index != 0);
		
		usage[0] = true;
		usage[1] = true;
		usage[2] = true;
		
		return usage;
	}
	
	//ˢ��
	public void refresh(){
		this.sBlock = new SuperBlock(readBlock(0));//���¶�ȡSuperBlock
	}
}
