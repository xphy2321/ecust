package com.binary.os.filesys.manager;

import java.util.ArrayList;
import java.util.LinkedList;

//������
public class Interpreter {
	
	ArrayList<String> words = new ArrayList<String>();

	public Interpreter(String commands){
		String[] wordsArray = commands.trim().split("\\s+");//��ֳɵ���
		for(String word:wordsArray){//ȥ�����ַ�
			if(!word.equals("")){//��Ϊ��
				words.add(word);
			}
		}
	}
	
	//��ò�����
	public String getOperat(){
		if(words.size() == 0){//�����ڲ���
			return "";
		}
		String operation = words.get(0);//��һ������Ϊ����
		return operation;
	}

	//���ļ���ʱ������ļ���
	public String getFileName(){
		if(words.size() < 2){//�������ļ���
			return "";
		}
		String[] pathArray = words.get(1).split("\\\\|/");//���·��
		LinkedList<String> path = new LinkedList<String>();
		for(String p:pathArray){//ȥ�����ַ�
			if(!p.equals("")){//��Ϊ��
				path.add(p);
			}
		}
		
		return path.peekLast();
	}
	
	//���ļ���ʱ�����·����
	public String[] getDirs(){
		if(words.size() < 2){//�������ļ���
			return new String[0];
		}
		String[] pathArray = words.get(1).split("\\\\|/");//���·��
		LinkedList<String> path = new LinkedList<String>();
		for(String p:pathArray){//ȥ�����ַ�
			if(!p.equals("")){//��Ϊ��
				path.add(p);
			}
		}
		
		if(path.size() == 1){//ֻ���ļ���
			return new String[0];
		}else{
			path.pollLast();//ɾ���ļ���
			String[] dirs = new String[path.size()];
			dirs = path.toArray(dirs);
			return dirs;
		}
	}

	//˫�ļ���ʱ�����Դ�ļ���
	public String getSrcFileName(){
		return getFileName();
	}
	
	//˫�ļ���ʱ�����Դ·����
	public String[] getSrcDirs(){
		return getDirs();
	}
	
	//˫�ļ���ʱ�����Ŀ���ļ���
	public String getDesFileName(){
		if(words.size() < 3){//�������ļ���
			return "";
		}
		String[] pathArray = words.get(2).split("\\\\|/");//���·��
		LinkedList<String> path = new LinkedList<String>();
		for(String p:pathArray){//ȥ�����ַ�
			if(!p.equals("")){//��Ϊ��
				path.add(p);
			}
		}
		
		return path.peekLast();
	}
	
	//˫�ļ���ʱ�����Ŀ��·����
	public String[] getDesDirs(){
		if(words.size() < 3){//�������ļ���
			return new String[0];
		}
		String[] pathArray = words.get(2).split("\\\\|/");//���·��
		LinkedList<String> path = new LinkedList<String>();
		for(String p:pathArray){//ȥ�����ַ�
			if(!p.equals("")){//��Ϊ��
				path.add(p);
			}
		}
		
		
		if(path.size() == 1){//ֻ���ļ���
			return new String[0];
		}else{
			path.pollLast();//ɾ���ļ���
			String[] dirs = new String[path.size()];
			dirs = path.toArray(dirs);
			return dirs;
		}
	}
	
	//���Ŀ¼
	public String[] getDirPath(){
		if(words.size() < 2){//������Ŀ¼
			return new String[0];
		}
		String[] pathArray = words.get(1).split("\\\\|/");//���·��
		LinkedList<String> path = new LinkedList<String>();
		for(String p:pathArray){//ȥ�����ַ�
			if(!p.equals("")){//��Ϊ��
				path.add(p);
			}
		}
		
		String[] dirs = new String[path.size()];
		dirs = path.toArray(dirs);
		return dirs;
	}
	
	//���Ҫ�޸ĵ�����
	public String[] getAttrs(){
		if(words.size() < 3){//����������
			return new String[0];
		}
		
		ArrayList<String> attrsList = new ArrayList<String>();
		//�жϴӵ��������ʿ�ʼ�ǲ�������
		for(int i=2; i<words.size(); i++){
			String attri = words.get(i);
			if(attri.equals("r") || attri.equals("w") || attri.equals("s") || attri.equals("h")){//������
				attrsList.add(attri);//�������
			}
		}
		
		String[] attrs = new String[attrsList.size()];
		attrs = attrsList.toArray(attrs);
		return attrs;
	}
	
	
}
