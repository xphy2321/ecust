package com.binary.os.filesys.manager;

import java.util.ArrayList;
import java.util.LinkedList;
//import java.util.Stack;

import com.binary.os.filesys.dentries.Directory;
import com.binary.os.filesys.dentries.RootDirectory;
import com.binary.os.filesys.dentries.SFile;

public class FileManager {
	
	public static int BYTE_PER_BLOCK = 128;

	private DiskManager disk;
	
	private RootDirectory root;
	private LinkedList<Directory> currentPath;
	
	public FileManager(){
		disk = new DiskManager();
		//��ʼ����Ŀ¼
		root = new RootDirectory();
		disk.readDirectory(root);
		currentPath.add(root);
	}
	
	//���������
//	public String interpret(String command){
//		String[] words = command.trim().split("\\s+");//��ֳɵ���
//		
//	}
	
	//�ⲿ���õ�ʱ��Ҫȷ�� �����ļ�
	public String create(String[] dirs, String fileName, boolean isOverWrite){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ��ļ�����ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = stemp[1];//��׺
		if(exten == null){//���޺�׺
			exten = "";
		}
		
		if(!isOverWrite){//������
			if(getCurrentDir().checkName(name, exten) == true){//ͬ��
				return "�����������ļ�����ʧ�ܣ�";
			}
		}

		SFile file = new SFile();
		if(file.setName(name) == false){//�ļ���
			return "�ļ����������зǷ��ַ����ļ������ó���6����ĸ�����֣���3�����֣�";
		}
		if(file.setExtension(exten) == false){//��׺
			return "��׺���������зǷ��ַ�����׺�����ó���3����ĸ�����֣�";
		}
		
		file.setAttribute(SFile.F_S_W);//�ļ����Գ�ʼ����ʾ��д
		
		if(disk.saveDentry(file) == false){//�����ļ�
			return "���̿ռ䲻�㣡�ļ�����ʧ�ܣ�";
		}
		
		if(getCurrentDir().addDentry(file) == false){//����ļ���Ŀ¼ʧ��
			disk.recycleDentry(file);//����Ϊ�ļ�������̿�
			return "���ļ���ӵ�Ŀ¼ʧ�ܣ�����Ŀ¼�������ƣ�";
		}
		
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			disk.recycleDentry(file);//����Ϊ�ļ�������̿�
			return "���̿ռ䲻�㣡�޷�����Ŀ¼���ļ�����ʧ�ܣ�";
		}
		
		return fileName + " �ļ������ɹ���"; 
	}
	
	//�����ļ�
	public String copy(String[] srcDirs, String srcFileName, String[] desDirs, String desFileName){
		
		if(srcFileName.equals("")){//�ļ���Ϊ��
			return "Դ�ļ���Ϊ�գ������ļ�ʧ�ܣ�";
		}
		
		if(desFileName.equals("")){//�ļ���Ϊ��
			return "Ŀ���ļ���Ϊ�գ������ļ�ʧ�ܣ�";
		}
		
		if(acceDirs(srcDirs) == false){//����Ŀ¼ʧ��
			return "ԴĿ¼�����ڣ������ļ�ʧ�ܣ�";
		}
		
		String[] stemp = srcFileName.split("\\.");//����ļ����ͺ�׺
		String srcName = stemp[0];//�ļ���
		String srcExten = stemp[1];//��׺
		if(srcExten == null){//���޺�׺
			srcExten = "";
		}
		
		SFile srcFile = getCurrentDir().checkFileName(srcName, srcExten);
		if(srcFile == null){
			return "Դ�ļ������ڣ������ļ�ʧ�ܣ�";
		}
		
		disk.readFile(srcFile);//��ȡԴ�ļ�
		
		if(acceDirs(desDirs) == false){//����Ŀ¼ʧ��
			return "Ŀ��Ŀ¼�����ڣ������ļ�ʧ�ܣ�";
		}
		
		SFile desFile = sCreate(desFileName);//��ʼ����Ŀ���ļ�
		if(desFile == null){//����Ŀ���ļ�ʧ��
			return "�޷�����Ŀ���ļ��������ļ�ʧ�ܣ�";
		}
		
		desFile.setAttribute(srcFile.getAttribute());//��������
		desFile.setContent(srcFile.getContent());//�����ļ�����
		
		if(disk.saveDentry(desFile) == false){//�����ļ�
			getCurrentDir().removeDentry(desFile);//��ǰĿ¼�Ƴ����ļ�
			saveCurrentDir();//���浱ǰĿ¼
			return "���̿ռ䲻�㣡�޷�����Ŀ���ļ��������ļ�ʧ�ܣ�";
		}
		return "�����ļ��ɹ���";
	}
	
	//ɾ���ļ�
	public String delete(String[] dirs, String fileName){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ�ɾ���ļ�ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = stemp[1];//��׺
		if(exten == null){//���޺�׺
			exten = "";
		}
		
		SFile file = getCurrentDir().checkFileName(name, exten);
		if(file == null){
			return "�ļ������ڣ�ɾ���ļ�ʧ�ܣ�";
		}
		
		getCurrentDir().removeDentry(file);//��ǰĿ¼�Ƴ����ļ�
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			getCurrentDir().addDentry(file);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "�޷�����Ŀ¼��ɾ���ļ�ʧ�ܣ�";
		}
		
		if(disk.recycleDentry(file) == false){//�����ļ�ʧ��
			getCurrentDir().addDentry(file);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "�����������޷������ļ���ɾ���ļ�ʧ�ܣ�";
		}
		
		return fileName + " �ļ�ɾ���ɹ���"; 
	}
		
	//�ƶ��ļ�
	public String move(String[] srcDirs, String srcFileName, String[] desDirs, String desFileName){
		
		if(srcFileName.equals("")){//�ļ���Ϊ��
			return "Դ�ļ���Ϊ�գ��ƶ��ļ�ʧ�ܣ�";
		}
		
		if(desFileName.equals("")){//�ļ���Ϊ��
			return "Ŀ���ļ���Ϊ�գ��ƶ��ļ�ʧ�ܣ�";
		}
		
		//����ԴĿ¼
		if(acceDirs(srcDirs) == false){//����Ŀ¼ʧ��
			return "ԴĿ¼�����ڣ��ƶ��ļ�ʧ�ܣ�";
		}
		
		LinkedList<Directory> oldPath = currentPath;//����ԴĿ¼·��
		
		String[] stemp = srcFileName.split("\\.");//����ļ����ͺ�׺
		String srcName = stemp[0];//�ļ���
		String srcExten = stemp[1];//��׺
		if(srcExten == null){//���޺�׺
			srcExten = "";
		}
		
		//��Դ�ļ��Ƿ������ԴĿ¼
		SFile srcFile = getCurrentDir().checkFileName(srcName, srcExten);
		if(srcFile == null){
			return "Դ�ļ������ڣ��ƶ��ļ�ʧ��";
		}
		
		//ԴĿ¼�Ƴ�Դ�ļ�
		getCurrentDir().removeDentry(srcFile);//��ǰĿ¼�Ƴ����ļ�
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			getCurrentDir().addDentry(srcFile);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "�޷�����ԴĿ¼���ƶ��ļ�ʧ�ܣ�";
		}
		
		//����Ŀ��Ŀ¼
		if(acceDirs(desDirs) == false){//����Ŀ¼ʧ��
			getCurrentDir().addDentry(srcFile);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "Ŀ��Ŀ¼�����ڣ��ƶ��ļ�ʧ��";
		}
		
		//Ŀ���ļ���
		stemp = desFileName.split("\\.");//����ļ����ͺ�׺
		String desName = stemp[0];//�ļ���
		String desExten = stemp[1];//��׺
		if(desExten == null){//���޺�׺
			desExten = "";
		}
		
		//��Ŀ���ļ���
		if(srcFile.setName(desName) == false){//Ŀ���ļ�������
			currentPath = oldPath; //�ص�ԴĿ¼·��
			getCurrentDir().addDentry(srcFile);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "Ŀ���ļ����������зǷ��ַ����ƶ��ļ�ʧ�ܣ�";
		}
		
		//��Ŀ���׺
		if(srcFile.setExtension(desExten) == false){//Ŀ���׺����
			currentPath = oldPath; //�ص�ԴĿ¼·��
			getCurrentDir().addDentry(srcFile);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "Ŀ���ļ���׺���������зǷ��ַ����ƶ��ļ�ʧ�ܣ�";
		}
		
		//��Դ�ļ��ӵ�Ŀ��Ŀ¼
		if(getCurrentDir().addDentry(srcFile) == false){//����ļ���Ŀ¼ʧ��
			currentPath = oldPath; //�ص�ԴĿ¼·��
			getCurrentDir().addDentry(srcFile);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "����Ŀ��Ŀ¼�������ƣ��ƶ��ļ�ʧ��";
		}
		
		//����Ŀ��Ŀ¼
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			getCurrentDir().removeDentry(srcFile);//�Ƴ���ǰ�����
			saveCurrentDir();//���浱ǰĿ¼
			currentPath = oldPath; //�ص�ԴĿ¼·��
			getCurrentDir().addDentry(srcFile);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "�޷�����Ŀ��Ŀ¼���ƶ��ļ�ʧ�ܣ�";
		}
		
		return "�ƶ��ļ��ɹ���";
	}
	
	//��ʾ�ļ�
	public String type(String[] dirs, String fileName, boolean isForEdit){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ���ʾ�ļ�ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = stemp[1];//��׺
		if(exten == null){//���޺�׺
			exten = "";
		}
		
		//��ȡ�ļ�
		SFile file = getCurrentDir().checkFileName(name, exten);
		if(file == null){
			return "�ļ������ڣ���ʾ�ļ�ʧ�ܣ�";
		}	
		
		//�Ƿ���Ϊ�˱༭
		if(isForEdit){//��Ϊ�˱༭��Ҫ�ж�����
			if(file.getAttribute() == SFile.F_H_R || file.getAttribute() == SFile.F_S_W){//���ļ�������ֻ����
				return "�ļ�����Ϊֻ�����޷��༭�ļ���";
			}
		}
		
		disk.readFile(file);//��ȡ�ļ�
		
		return file.toString();//�����ļ��ı�����
	}
	
	public String edit(String[] dirs, String fileName, String text){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ��༭�ļ�ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = stemp[1];//��׺
		if(exten == null){//���޺�׺
			exten = "";
		}
		
		//��ȡ�ļ�
		SFile file = getCurrentDir().checkFileName(name, exten);
		if(file == null){
			return "�ļ������ڣ��༭�ļ�ʧ�ܣ�";
		}	
		
		if(file.getAttribute() == SFile.F_H_R || file.getAttribute() == SFile.F_S_W){//���ļ�������ֻ����
			return "�ļ�����Ϊֻ�����޷��༭�ļ���";
		}
		
		//�����ļ��ı�
		if(file.setText(text) == false){
			return "�ı���С�������������������ļ�ʧ�ܣ�";
		}
		
		//�����ļ�
		if(disk.saveDentry(file) == false){//�����ļ�ʧ��
			return "���̿ռ䲻�㣡�����ļ�ʧ�ܣ�";
		}
		
		return fileName + " �ļ������ɹ���"; 
	}
	
	//�ı��ļ�����
	public String change(String[] dirs, String fileName, String[] attrs){
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ��޸��ļ�����ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = stemp[1];//��׺
		if(exten == null){//���޺�׺
			exten = "";
		}
		
		//��ȡ�ļ�
		SFile file = getCurrentDir().checkFileName(name, exten);
		if(file == null){
			return "�ļ������ڣ��޸��ļ�����ʧ�ܣ�";
		}	
		
		//��þ�����
		int oldAttri = file.getAttribute();
		
		//��������
		for(String attri:attrs){//Ҫ���ĳɵ����Զ���
			file.changeAttri(attri);//��������
		}
		
		//�����ļ�
		if(disk.saveDentry(file) == false){//�����ļ�ʧ��
			file.setAttribute(oldAttri);//�ָ�����
			return "���̿ռ䲻�㣡�޷������ļ��޸ģ��޸��ļ�����ʧ�ܣ�";
		}
		
		String cAttri = file.getStringAttri();//��ȡ������Ϣ
		
		return fileName + " �޸����Գɹ���  ��ǰ����Ϊ " + cAttri; 
	}
	
	public String format(){
		String result = "�ɹ���ʽ�����̣�";
		if(!disk.format()){
			result = "��ʽ������ʧ�ܣ�";
		}
		disk.readDirectory(root);
		return result;
	}
	
	//����Ŀ¼
	public String makdir(String[] dirs){
		
		ArrayList<String> unCreatedDirs = checkDirs(dirs);//���δ����Ŀ¼��
		if(unCreatedDirs.size() == dirs.length){//��Ҫ������Ŀ¼�����Ѵ���
			return "��Ҫ������Ŀ¼���Ѵ��ڣ�����Ҫ�ٴ�����";
		}
		
		for(String dirName:unCreatedDirs){//ѭ������δ����Ŀ¼

			//���ͬ��
			if(getCurrentDir().checkName(dirName, "") == true){//ͬ��
				return "����������" + dirName + "�ļ��д���ʧ�ܣ�";
			}
			
			//���ļ�����
			Directory dir = new Directory();
			if(dir.setName(dirName) == false){//�ļ�����
				return "�ļ�����" + dirName + "�������зǷ��ַ����ļ��������ó���6����ĸ�����֣���3�����֣�";
			}
			
			dir.setAttribute(SFile.D_S_W);//�ļ������Գ�ʼ����ʾ��д
			
			//�����ļ���
			if(disk.saveDentry(dir) == false){//�����ļ���
				return "���̿ռ䲻�㣡�ļ���" + dirName + "����ʧ�ܣ�";
			}
			
			//����ļ��е���ǰĿ¼
			if(getCurrentDir().addDentry(dir) == false){//����ļ��е�Ŀ¼ʧ��
				disk.recycleDentry(dir);//����Ϊ�ļ��з�����̿�
				return "���ļ���" + dirName + "��ӵ�Ŀ¼ʧ�ܣ�����Ŀ¼�������ƣ�";
			}
			
			//���浱ǰĿ¼
			if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
				disk.recycleDentry(dir);//����Ϊ�ļ��з�����̿�
				return "���̿ռ䲻�㣡�޷�����Ŀ¼���ļ���" + dirName + "����ʧ�ܣ�";
			}
			
			currentPath.add(dir);//��ӵ���ǰ·���У����µ�ǰĿ¼Ϊ�Ѵ���Ŀ¼
		}
		
		return "�ļ��д����ɹ���"; 
	}
	
	//���ĵ�ǰĿ¼
	public String chadir(String[] dirs){
		if(acceDirs(dirs) == false){//���Ŀ¼��ʧ��
			return "Ŀ¼�����ڣ�";
		}
		return "Ŀ¼�����ɹ���";
	}
	
//	//ɾ����Ŀ¼
//	public String rdir(String[] dirs){
//		LinkedList<Directory> oldPath = currentPath;
//		
//		if(dirs[0].equals("root:")){//Ϊ���Ե�ַ
//			if(dirs.length ==1){//ֻ�и�Ŀ¼
//				return "�޷�ɾ����Ŀ¼��";
//			}
//			currentPath.clear();//�����ǰĿ¼
//			currentPath.add(root);//�赱ǰĿ¼Ϊroot
//		}
//		
//		String sDir = null;
//		for(int i=0; i<dirs.length-1; i++){//������Ҫɾ��Ŀ¼�ĸ�Ŀ¼
//			sDir = dirs[i];
//			if(sDir.equals("root:")){//����root��ַ
//				continue;
//			}
//			Directory tDir = getCurrentDir().checkDirName(sDir);//���ļ�����
//			if(tDir != null){//���ڴ�����Ŀ¼
//				currentPath.add(tDir);//���µ�ǰĿ¼
//			}else{//������
//				currentPath = oldPath;//�ָ��ɵ�ǰĿ¼
//				return "·�������ڣ�ɾ��Ŀ¼ʧ�ܣ�";
//			}
//		}
//		sDir = dirs[dirs.length-1];//���һ��Ŀ¼����Ҫɾ����Ŀ¼
//		Directory tDir = getCurrentDir().checkDirName(sDir);//���ļ�����
//		if(tDir != null){//���ڴ�����Ŀ¼
//			if(currentPath.equals(oldPath)){//����ԭ����ǰĿ¼
//				return "�޷�ɾ����ǰĿ¼��";
//			}
//			if(tDir.getSize() != 0){//���ǿ�Ŀ¼
//				currentPath = oldPath;//�ָ��ɵ�ǰĿ¼
//				return "Ŀ¼" + dirs[dirs.length-1] + "���ǿ�Ŀ¼���޷�ɾ����";
//			}
//			
//		}else{//������
//			currentDir = oldDir;//�ָ��ɵ�ǰĿ¼
//			return "·�������ڣ�ɾ��Ŀ¼ʧ�ܣ�";
//		}
//		return true;
//	}
//	
//	public String deldir(){
//		
//	}
//	
//	public boolean[] getUsage(){
//		return disk.getUsage();
//	}
	
	
	//����Ŀ¼��������Ŀ¼
	public boolean acceDirs(String[] dirs){
		LinkedList<Directory> oldPath = currentPath;
		
		if(dirs[0].equals("root:")){//Ϊ���Ե�ַ
			currentPath.clear();//�����ǰĿ¼
			currentPath.add(root);//�赱ǰĿ¼Ϊroot
		}
		
		for(String sDir:dirs){
			if(sDir.equals("root:")){//����root��ַ
				continue;
			}
			Directory tDir = getCurrentDir().checkDirName(sDir);//���ļ�����
			if(tDir != null){//���ڴ�����Ŀ¼
				currentPath.add(tDir);//���µ�ǰĿ¼
			}else{//������
				currentPath = oldPath;//�ָ��ɵ�ǰĿ¼
				return false;
			}
		}
		return true;
	}
	
	//����Ŀ¼���������Ѵ���Ŀ¼����ò����ڵ�Ŀ¼��
	public ArrayList<String> checkDirs(String[] dirs){
		boolean notExist = false;
		ArrayList<String> unCreatedDirs = new ArrayList<String>();//��Ż�δ������Ŀ¼
		
		if(dirs[0].equals("root:")){//Ϊ���Ե�ַ
			currentPath.clear();//�����ǰĿ¼
			currentPath.add(root);//�赱ǰĿ¼Ϊroot
		}
		
		for(String sDir:dirs){
			if(sDir.equals("root:")){//����root��ַ
				continue;
			}
			if(notExist){//���Ѿ��ǲ����ڵ�Ŀ¼
				unCreatedDirs.add(sDir);//���뻹δ������Ŀ¼
				continue;
			}
			Directory tDir = getCurrentDir().checkDirName(sDir);//���ļ�����
			if(tDir != null){//���ڴ�����Ŀ¼
				currentPath.add(tDir);//���µ�ǰĿ¼
			}else{//������
				notExist = true;
				unCreatedDirs.add(sDir);//���뻹δ������Ŀ¼
			}
		}
		return unCreatedDirs;
	}

	public SFile sCreate(String fileName){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return null;
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = stemp[1];//��׺
		if(exten == null){//���޺�׺
			exten = "";
		}
		
		if(getCurrentDir().checkName(name, exten) == true){//ͬ��
			return null;
		}


		SFile file = new SFile();
		if(file.setName(name) == false){//�ļ���
			return null;
		}
		if(file.setExtension(exten) == false){//��׺
			return null;
		}
		
		file.setAttribute(SFile.F_S_W);//�ļ����Գ�ʼ����ʾ��д
		
		if(disk.saveDentry(file) == false){//�����ļ�
			return null;
		}
		
		if(getCurrentDir().addDentry(file) == false){//����ļ���Ŀ¼ʧ��
			disk.recycleDentry(file);//����Ϊ�ļ�������̿�
			return null;
		}
		
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			disk.recycleDentry(file);//����Ϊ�ļ�������̿�
			return null;
		}
		
		return file;
	}
	
	public Directory sMakdir(String dirName){
		
		if(dirName.equals("")){//�ļ�����Ϊ��
			return null;
		}
		
		if(getCurrentDir().checkName(dirName, "") == true){//ͬ��
			return null;
		}

		Directory dir = new Directory();
		if(dir.setName(dirName) == false){//�ļ�����
			return null;
		}
		
		dir.setAttribute(SFile.D_S_W);//�ļ������Գ�ʼ����ʾ��д
		

		
		if(disk.saveDentry(dir) == false){//�����ļ���
			return null;
		}
		
		if(getCurrentDir().addDentry(dir) == false){//����ļ��е�Ŀ¼ʧ��
			disk.recycleDentry(dir);//����Ϊ�ļ��з�����̿�
			return null;
		}
		
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			disk.recycleDentry(dir);//����Ϊ�ļ��з�����̿�
			return null;
		}
		
		return dir;
	}
	
	public boolean saveCurrentDir(){
		if(getCurrentDir() == root){//����ǰĿ¼Ϊ��Ŀ¼
			if(disk.saveRoot(root) == false){//�����Ŀ¼ʧ��
				return false;
			}
		}else{//�Ǹ�Ŀ¼
			if(disk.saveDentry(getCurrentDir()) == false){//����Ŀ¼ʧ��
				return false;
			}
		}
		return true;
	}
	
	public Directory getCurrentDir(){
		return currentPath.getLast();//·�����һ�����ǵ�ǰĿ¼
	}
	
	//��ȡ��ǰĿ¼·����
	public String getStringCurrentPath(){
		String path = "";
		for(Directory dir:currentPath){
			path = path + dir.getName() + "\\";
		}
		return path;
	}
	
	public RootDirectory getRoot() {
		return root;
	}

	public DiskManager getDisk() {
		return disk;
	}

}
