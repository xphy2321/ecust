package com.binary.os.filesys.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import com.binary.os.filesys.dentries.Dentry;
import com.binary.os.filesys.dentries.Directory;
import com.binary.os.filesys.dentries.RootDirectory;
import com.binary.os.filesys.dentries.SFile;
import com.binary.os.kernel.ProcessManager;
import com.binary.os.views.EditTextDialog;
import com.binary.os.views.ShowTextDialog;

public class FileManager {
	
	public static int BYTE_PER_BLOCK = 128;

	private DiskManager disk;
	private RootDirectory root;
	private LinkedList<Directory> currentPath;
	
	private LinkedList<Directory>[] processFileAddr;
	private String[] processFileName;
	
	@SuppressWarnings("unchecked")
	public FileManager(){
		disk = new DiskManager();
		//��ʼ����Ŀ¼
		root = new RootDirectory();
		disk.readDirectory(root);
		currentPath = new LinkedList<Directory>();
		currentPath.add(root);
		processFileAddr = (LinkedList<Directory>[]) new LinkedList[10];
		processFileName = new String[10];
	}
	
	
	//�������
	public String interpret(String commands){
		Interpreter cmd = new Interpreter(commands);//���������
		
		String operation = cmd.getOperat();
		
		//�����ļ�
		if(operation.equals("create")){
			return create(cmd.getDirs(), cmd.getFileName());
		}
		
		//�������༭�ļ�
		if(operation.equals("vi")){
			return vi(cmd.getDirs(), cmd.getFileName());
		}
		
		//�����ļ�
		if(operation.equals("copy")){
			return copy(cmd.getSrcDirs(), cmd.getSrcFileName(), cmd.getDesDirs() , cmd.getDesFileName());
		}

		//ɾ���ļ�
		if(operation.equals("delete")){			
			return delete(cmd.getDirs(), cmd.getFileName());
		}
		
		//�ƶ��ļ�
		if(operation.equals("move")){	
			return move(cmd.getSrcDirs(), cmd.getSrcFileName(), cmd.getDesDirs() , cmd.getDesFileName());
		}
		
		//��ʾ�ļ�
		if(operation.equals("type")){
			return type(cmd.getDirs(), cmd.getFileName(), false);
		}
		
		//�༭�ļ�
		if(operation.equals("edit")){
			return type(cmd.getDirs(), cmd.getFileName(), true);
		}
		
		//�ı��ļ�����
		if(operation.equals("change")){
			return change(cmd.getDirs(), cmd.getFileName(), cmd.getAttrs());
		}
		
		//��ʽ��
		if(operation.equals("format")){
			return format();
		}
			
		//����Ŀ¼
		if(operation.equals("makdir")){
			return makdir(cmd.getDirPath());
		}
		
		//���ĵ�ǰĿ¼
		if(operation.equals("chadir")){
			return chadir(cmd.getDirPath());
		}
		
		//ɾ����Ŀ¼
		if(operation.equals("rdir")){
			return rdir(cmd.getDirPath());
		}
		
		//ɾ��Ŀ¼
		if(operation.equals("deldir")){
			return deldir(cmd.getDirPath());
		}
		
		//�����ļ�
		if(operation.equals("run")){
			return run(cmd.getDirs(), cmd.getFileName());
		}
		
		//ˢ��
		if(operation.equals("refresh")){
			return refresh();
		}
		
		
		return "���ǺϷ������";
	}
	
	//�ⲿ���õ�ʱ��Ҫȷ�� �����ļ�
	private String create(String[] dirs, String fileName){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ��ļ�����ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			exten = stemp[1];//��׺
		}
		
		if(getCurrentDir().checkName(name, exten) == true){//ͬ��
			SFile cFile = getCurrentDir().checkFileName(name, exten);
			if(cFile == null){
				return "����Ŀ¼����ļ��������ļ�����ʧ�ܣ�";
			}
			
			int result = JOptionPane.showConfirmDialog(null, "�����ļ��������Ƿ񸲸ǣ�", fileName, JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION){//����
				getCurrentDir().removeDentry(cFile);//��ǰĿ¼�Ƴ����ļ�
				if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
					getCurrentDir().addDentry(cFile);//�Ż�Ŀ¼
					saveCurrentDir();//���浱ǰĿ¼
					return "�޷�����Ŀ¼�������ļ�ʧ�ܣ�";
				}
				
				if(disk.recycleDentry(cFile) == false){//�����ļ�ʧ��
					getCurrentDir().addDentry(cFile);//�Ż�Ŀ¼
					saveCurrentDir();//���浱ǰĿ¼
					return "�����������޷������ļ��������ļ�ʧ�ܣ�";
				}
			}else{
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
		
		saveAllDirs();//����ȫ��·��
		
		return fileName + " �ļ������ɹ���"; 
	}
	
	//�������༭�ļ�
	private String vi(String[] dirs, String fileName){
		//�����ļ�
		String result = create(dirs, fileName);
		if(result.equals(fileName + " �ļ������ɹ���") == false){//�����ļ�ʧ��
			return result;
		}
		
		//�༭�ļ�
		result = type(dirs, fileName, true);
		
		return result;
	}
	
	//�����ļ�
	private String copy(String[] srcDirs, String srcFileName, String[] desDirs, String desFileName){
		
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
		String srcExten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			srcExten = stemp[1];//��׺
		}
		
		SFile srcFile = getCurrentDir().checkFileName(srcName, srcExten);
		if(srcFile == null){
			return "Դ�ļ������ڣ������ļ�ʧ�ܣ�";
		}
		
		disk.readFile(srcFile);//��ȡԴ�ļ�
		
		if(acceDirs(desDirs) == false){//����Ŀ¼ʧ��
			return "Ŀ��Ŀ¼�����ڣ������ļ�ʧ�ܣ�";
		}
		
		//�������ļ�
		SFile desFile = sCreate(desFileName, false);//��ʼ����Ŀ���ļ�
		if(desFile == null){//����Ŀ���ļ�ʧ��
			return "�޷�����Ŀ���ļ��������ļ�ʧ�ܣ�";
		}
		
		desFile.setSize(srcFile.getSize());//�����ļ���С����
		desFile.setAttribute(srcFile.getAttribute());//��������
		desFile.setContent(srcFile.getContent());//�����ļ�����
		
		if(disk.saveDentry(desFile) == false){//�����ļ�
			return "���̿ռ䲻�㣡�޷�����Ŀ���ļ��������ļ�ʧ�ܣ�";
		}
		
		//����Ŀ¼
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			disk.recycleDentry(desFile);//����Ϊ�ļ�������̿�
			getCurrentDir().removeDentry(desFile);//ɾ��Ŀ¼��
			return "���̿ռ䲻�㣡�޷�����Ŀ¼���ļ�����ʧ�ܣ�";
		}
		
		saveAllDirs();//����ȫ��·��
		
		return "�����ļ��ɹ���";
	}
	
	//ɾ���ļ�
	private String delete(String[] dirs, String fileName){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ�ɾ���ļ�ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			exten = stemp[1];//��׺
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
		
		saveAllDirs();//����ȫ��·��
		
		return fileName + " �ļ�ɾ���ɹ���"; 
	}
		
	//�ƶ��ļ�
	private String move(String[] srcDirs, String srcFileName, String[] desDirs, String desFileName){
		
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
		
		LinkedList<Directory> oldPath = new LinkedList<Directory>(currentPath);//ǳ����//����ԴĿ¼·��
		
		String[] stemp = srcFileName.split("\\.");//����ļ����ͺ�׺
		String srcName = stemp[0];//�ļ���
		String srcExten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			srcExten = stemp[1];//��׺
		}
		
		//��Դ�ļ��Ƿ������ԴĿ¼
		SFile srcFile = getCurrentDir().checkFileName(srcName, srcExten);
		if(srcFile == null){
			return "Դ�ļ������ڣ��ƶ��ļ�ʧ�ܣ�";
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
			return "Ŀ��Ŀ¼�����ڣ��ƶ��ļ�ʧ�ܣ�";
		}
		
		//Ŀ���ļ���
		stemp = desFileName.split("\\.");//����ļ����ͺ�׺
		String desName = stemp[0];//�ļ���
		String desExten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			desExten = stemp[1];//��׺
		}
		
		//�������
		if(getCurrentDir().checkName(desName, desExten) == true){//ͬ��
			currentPath = oldPath; //�ص�ԴĿ¼·��
			getCurrentDir().addDentry(srcFile);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "Ŀ��Ŀ¼����ͬ���ļ��л��ļ����ƶ��ļ�ʧ�ܣ�";
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
			return "����Ŀ��Ŀ¼�������ƣ��ƶ��ļ�ʧ�ܣ�";
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
		
		saveAllDirs();//����ȫ��·��
		
		return "�ƶ��ļ��ɹ���";
	}
	
	//��ʾ�ļ�
	private String type(String[] dirs, String fileName, boolean isForEdit){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ���ʾ�ļ�ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			exten = stemp[1];//��׺
		}
		
		//��ȡ�ļ�
		SFile file = getCurrentDir().checkFileName(name, exten);
		if(file == null){
			return "�ļ������ڣ���ʾ�ļ�ʧ�ܣ�";
		}	
		
		disk.readFile(file);//��ȡ�ļ�
		
		//�Ƿ���Ϊ�˱༭
		if(isForEdit){//��Ϊ�˱༭��Ҫ�ж�����
			if(file.getAttribute() == SFile.F_H_R || file.getAttribute() == SFile.F_S_R){//���ļ�������ֻ����
				return "�ļ�����Ϊֻ�����޷��༭�ļ���";
			}
			new EditTextDialog(fileName, file.toString(), this);//�༭�ļ�
			return "�༭�ļ�������";
		}else{
			new ShowTextDialog(fileName, file.toString());//��ʾ�ļ�
			return "��ʾ�ļ��ɹ���";
		}
		
		
	}
	
	//�༭�ļ�
	public String edit(String fileName, String text){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ��༭�ļ�ʧ�ܣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			exten = stemp[1];//��׺
		}
		
		//��ȡ�ļ�
		SFile file = getCurrentDir().checkFileName(name, exten);
		if(file == null){
			return "�ļ������ڣ��༭�ļ�ʧ�ܣ�";
		}	
		
		if(file.getAttribute() == SFile.F_H_R || file.getAttribute() == SFile.F_S_R){//���ļ�������ֻ����
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
		
		//����Ŀ¼
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			disk.recycleDentry(file);//����Ϊ�ļ�������̿�
			return "���̿ռ䲻�㣡�޷�����Ŀ¼�������ļ�ʧ�ܣ�";
		}
		
		saveAllDirs();//����ȫ��·��
		
		return fileName + " �ļ�����ɹ��� �ļ���СΪ" + file.getSize() + "�ֽ�"; 
	}
		
	//�ı��ļ�����
	private String change(String[] dirs, String fileName, String[] attrs){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ��޸��ļ�����ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			exten = stemp[1];//��׺
		}
		
		//��ȡ�ļ�
		SFile file = getCurrentDir().checkFileName(name, exten);
		if(file == null){
			return "�ļ������ڣ��޸��ļ�����ʧ�ܣ�";
		}	
		
		if(attrs.length == 0){//û��Ҫ��������
			return fileName + " û��Ҫ�޸ĵ����ԣ�  ��ǰ����Ϊ " + file.getStringAttri();
		}
		
		//��������
		for(String attri:attrs){//Ҫ���ĳɵ����Զ���
			file.changeAttri(attri);//��������
		}
		
		//����Ŀ¼
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			disk.recycleDentry(file);//����Ϊ�ļ�������̿�
			return "���̿ռ䲻�㣡�޷�����Ŀ¼�������ļ�ʧ�ܣ�";
		}
		
		String cAttri = file.getStringAttri();//��ȡ������Ϣ
		
		return fileName + " �޸����Գɹ���  ��ǰ����Ϊ " + cAttri; 
	}
	
	private String format(){
		String result = "�ɹ���ʽ�����̣�";
		if(!disk.format()){
			result = "��ʽ������ʧ�ܣ�";
		}
		disk.readDirectory(root);
		currentPath.clear();//�����ǰ·��
		currentPath.add(root);
		return result;
	}
	
	//����Ŀ¼
	private String makdir(String[] dirs){
		
		ArrayList<String> unCreatedDirs = checkDirs(dirs);//���δ����Ŀ¼��
		if(unCreatedDirs.size() == 0){//��Ҫ������Ŀ¼�����Ѵ���
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
		saveAllDirs();//����ȫ��·��
		
		return getCurrentDir().getName() + "�ļ��д����ɹ���"; 
	}
	
	//���ĵ�ǰĿ¼
	private String chadir(String[] dirs){
		if(acceDirs(dirs) == false){//���Ŀ¼��ʧ��
			return "Ŀ¼�����ڣ�";
		}
		return "����Ŀ¼�ɹ���";
	}
	
	//ɾ����Ŀ¼
	private String rdir(String[] dirs){
		if(dirs.length == 0){//������Ŀ¼��
			return "û������Ҫɾ����Ŀ¼��";
		}
		
		LinkedList<Directory> oldPath = new LinkedList<Directory>(currentPath);//ǳ����
		
		//����ǲ��Ǹ�Ŀ¼
		if(dirs[0].equals("root:")){//Ϊ���Ե�ַ
			if(dirs.length ==1){//ֻ�и�Ŀ¼
				return "�޷�ɾ����Ŀ¼��";
			}
			currentPath.clear();//�����ǰĿ¼
			currentPath.add(root);//�赱ǰĿ¼Ϊroot
		}
		
		//����·��
		String sDir = null;
		for(int i=0; i<dirs.length-1; i++){//������Ҫɾ��Ŀ¼�ĸ�Ŀ¼
			sDir = dirs[i];
			if(sDir.equals("root:")){//����root��ַ
				continue;
			}
			Directory tDir = getCurrentDir().checkDirName(sDir);//���ļ�����
			if(tDir != null){//���ڴ�����Ŀ¼
				disk.readDirectory(tDir);//��ȡĿ¼
				currentPath.add(tDir);//���µ�ǰĿ¼
			}else{//������
				currentPath = oldPath;//�ָ��ɵ�ǰĿ¼
				return "·�������ڣ�ɾ��Ŀ¼ʧ�ܣ�";
			}
		}
		//���Ŀ¼
		sDir = dirs[dirs.length-1];//���һ��Ŀ¼����Ҫɾ����Ŀ¼
		Directory tDir = getCurrentDir().checkDirName(sDir);//���ļ�����
		
		//���Ŀ¼�Ƿ����
		if(tDir == null){//Ŀ¼������
			currentPath = oldPath;//�ָ��ɵ�ǰĿ¼
			return "Ŀ¼�����ڣ�ɾ��Ŀ¼ʧ�ܣ�";
		}
		
		//����Ƿ��ǵ�ǰĿ¼
		currentPath.add(tDir);//��ʱ�Ȱ�Ҫɾ��Ŀ¼�ӵ���ǰ·��
		if(currentPath.equals(oldPath)){//����ԭ����ǰĿ¼
			return "�޷�ɾ����ǰĿ¼��";
		}else{//�ָ�
			currentPath.pollLast();
		}
		
		//����ǲ��ǿ�Ŀ¼
		if(tDir.getSize() != 0){//���ǿ�Ŀ¼
			currentPath = oldPath;//�ָ��ɵ�ǰĿ¼
			return "Ŀ¼" + dirs[dirs.length-1] + "���ǿ�Ŀ¼���޷�ɾ����";
		}
		
		//��Ŀ¼�Ӹ�Ŀ¼ɾ���������游Ŀ¼
		getCurrentDir().removeDentry(tDir);//��ǰĿ¼�Ƴ���Ŀ¼
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			getCurrentDir().addDentry(tDir);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "�޷����¸�Ŀ¼��ɾ��Ŀ¼ʧ�ܣ�";
		}
		
		//����Ŀ¼��ռ�ռ�
		if(disk.recycleDentry(tDir) == false){//����Ŀ¼ʧ��
			getCurrentDir().addDentry(tDir);//�Ż�Ŀ¼
			saveCurrentDir();//���浱ǰĿ¼
			return "�����������޷������ļ���ɾ���ļ�ʧ�ܣ�";
		}
		
		saveAllDirs();//����ȫ��·��
			
		return sDir + " Ŀ¼ɾ���ɹ���"; 
	}
	
	//ɾ��Ŀ¼��ɾ�����ļ�
	private String deldir(String[] dirs){
		if(dirs.length == 0){//������Ŀ¼��
			return "û������Ҫɾ����Ŀ¼��";
		}
		
		LinkedList<Directory> oldPath = new LinkedList<Directory>(currentPath);
		
		//����ǲ��Ǹ�Ŀ¼
		if(dirs[0].equals("root:")){//Ϊ���Ե�ַ
			if(dirs.length ==1){//ֻ�и�Ŀ¼
				return "�޷�ɾ����Ŀ¼��";
			}
			currentPath.clear();//�����ǰĿ¼
			currentPath.add(root);//�赱ǰĿ¼Ϊroot
		}
		
		//����·��
		String sDir = null;
		for(int i=0; i<dirs.length-1; i++){//������Ҫɾ��Ŀ¼�ĸ�Ŀ¼
			sDir = dirs[i];
			if(sDir.equals("root:")){//����root��ַ
				continue;
			}
			Directory tDir = getCurrentDir().checkDirName(sDir);//���ļ�����
			if(tDir != null){//���ڴ�����Ŀ¼
				disk.readDirectory(tDir);//��ȡĿ¼
				currentPath.add(tDir);//���µ�ǰĿ¼
			}else{//������
				currentPath = oldPath;//�ָ��ɵ�ǰĿ¼
				return "·�������ڣ�ɾ��Ŀ¼ʧ�ܣ�";
			}
		}
		//���Ŀ¼
		sDir = dirs[dirs.length-1];//���һ��Ŀ¼����Ҫɾ����Ŀ¼
		Directory tDir = getCurrentDir().checkDirName(sDir);//���ļ�����
		
		//���Ŀ¼�Ƿ����
		if(tDir == null){//Ŀ¼������
			currentPath = oldPath;//�ָ��ɵ�ǰĿ¼
			return "Ŀ¼�����ڣ�ɾ��Ŀ¼ʧ�ܣ�";
		}
		
		//����Ƿ��ǵ�ǰĿ¼
		currentPath.add(tDir);//��ʱ�Ȱ�Ҫɾ��Ŀ¼�ӵ���ǰ·��
		if(currentPath.equals(oldPath)){//����ԭ����ǰĿ¼
			return "�޷�ɾ����ǰĿ¼��";
		}else{//�ָ�
			currentPath.pollLast();
		}
		
		deleSub(tDir);//ɾ��Ŀ¼����Ŀ¼��
		getCurrentDir().removeDentry(tDir);//��ǰĿ¼�Ƴ�Ҫɾ��Ŀ¼��FCB
		saveAllDirs();//����ȫ��·��
			
		return sDir + " Ŀ¼ɾ���ɹ���"; 
	}
	
	//ִ�п�ִ���ļ�
	private String run(String[] dirs, String fileName){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return "�ļ���Ϊ�գ������ļ�ʧ�ܣ�";
		}
		
		if(acceDirs(dirs) == false){//����Ŀ¼ʧ��
			return "Ŀ¼�����ڣ�";
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			exten = stemp[1];//��׺
		}
		
		//����׺
		if(exten.equals("exe") == false){//���ǿ�ִ���ļ�
			return "�ļ����ǿ�ִ���ļ���";
		}
		
		//��ȡ�ļ�
		SFile file = getCurrentDir().checkFileName(name, exten);
		if(file == null){
			return "�ļ������ڣ������ļ�ʧ�ܣ�";
		}	
		
		disk.readFile(file);//��ȡ�ļ�
		
		//��������
		int result = ProcessManager.Create(file.getContent());
		if(result == -1){
			return "�����ļ�ʧ�ܣ��޷��������̣��ڴ�ռ䲻�㣡";
		}
		if(result == -2){
			return "�����ļ�ʧ�ܣ������﷨����";
		}
		if(result == -3){
			return "�����ļ�ʧ�ܣ��޷��������̣�����������Ϊ���";
		}
		
		processFileAddr[result] = new LinkedList<Directory>(currentPath);//ǳ����//�����̵������ļ���·������
		processFileName[result] = fileName;//�����ļ���
		
		
		return "�ɹ������ļ�" + fileName + "�� �����Ľ��̵�PIDΪ" + result;
	}
	
	//����Ŀ¼��������Ŀ¼
	private boolean acceDirs(String[] dirs){
		if(dirs.length == 0){//������Ŀ¼��
			return true;
		}
		LinkedList<Directory> oldPath = new LinkedList<Directory>(currentPath);//ǳ����
		
		if(dirs[0].equals("..")){//��һ��Ŀ¼
			if(getCurrentDir() == root){
				return true;
			}
			currentPath.pollLast();//����һ��
			return true;
		}
		
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
				disk.readDirectory(tDir);//��ȡĿ¼
				currentPath.add(tDir);//���µ�ǰĿ¼
			}else{//������
				currentPath = oldPath;//�ָ��ɵ�ǰĿ¼
				return false;
			}
		}
		return true;
	}
	
	//����Ŀ¼���������Ѵ���Ŀ¼����ò����ڵ�Ŀ¼��
	private ArrayList<String> checkDirs(String[] dirs){
		boolean notExist = false;
		ArrayList<String> unCreatedDirs = new ArrayList<String>();//��Ż�δ������Ŀ¼
		
		if(dirs.length == 0){//������Ŀ¼��
			return unCreatedDirs;
		}
		
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
				disk.readDirectory(tDir);//��ȡĿ¼
				currentPath.add(tDir);//���µ�ǰĿ¼
			}else{//������
				notExist = true;
				unCreatedDirs.add(sDir);//���뻹δ������Ŀ¼
			}
		}
		return unCreatedDirs;
	}

	//�ڵ�ǰĿ¼�����ļ�
	private SFile sCreate(String fileName, boolean isOverWrite){
		
		if(fileName.equals("")){//�ļ���Ϊ��
			return null;
		}
		
		String[] stemp = fileName.split("\\.");//����ļ����ͺ�׺
		String name = stemp[0];//�ļ���
		String exten = "";//��׺
		if(stemp.length > 1){//���ں�׺
			exten = stemp[1];//��׺
		}
		
		if(getCurrentDir().checkName(name, exten) == true){//ͬ��
			SFile cFile = getCurrentDir().checkFileName(name, exten);
			if(cFile == null){
				return null;
			}
			
			if(isOverWrite){//����
				getCurrentDir().removeDentry(cFile);//��ǰĿ¼�Ƴ����ļ�
				if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
					getCurrentDir().addDentry(cFile);//�Ż�Ŀ¼
					saveCurrentDir();//���浱ǰĿ¼
					return null;
				}
				
				if(disk.recycleDentry(cFile) == false){//�����ļ�ʧ��
					getCurrentDir().addDentry(cFile);//�Ż�Ŀ¼
					saveCurrentDir();//���浱ǰĿ¼
					return null;
				}
			}else{
				return null;
			}
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
		
		saveAllDirs();//����ȫ��·��
		
		return file;
	}
	
	//ɾ��Ŀ¼�µ�����ݹ鷨
	private void deleSub(Directory dir){
		disk.readDirectory(dir);//��ȡĿ¼
		for(Dentry dentry:dir.getDentryList()){//ȡ������Ŀ¼��
			if(dentry.isFile()){//������ļ���ɾ��
				disk.recycleDentry(dentry);
			}else{//��Ŀ¼�Ļ��ݹ�˷���
				deleSub((Directory)dentry);
			}
		}
		disk.recycleDentry(dir);//����Ŀ¼
	}
	
	private boolean saveCurrentDir(){
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
	
	//���浱ǰ·������Ŀ¼������Ŀ¼�����ű���
	private void saveAllDirs(){
		for(int i=currentPath.size()-1; i>0; i--){//�����rootĿ¼��
			disk.saveDentry(currentPath.get(i));//����Ŀ¼
		}
		disk.saveRoot((RootDirectory)currentPath.get(0));//��һ��һ����rootĿ¼
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
	
	//��ô���ʹ�����
	public boolean[] getUsage(){
		return disk.getUsage();
	}
	
	//����out�ļ�
	public boolean saveOut(int pid, byte result){
		LinkedList<Directory> oldPath = new LinkedList<Directory>(currentPath);//ǳ������ǰ·��
		
		LinkedList<Directory> outPath  = processFileAddr[pid];//��ý��̶�Ӧ��·��
		if(outPath == null){//·�������ھͱ�����ʧ��
			return false;
		}
		
		currentPath = outPath;
		
		SFile outFile = sCreate("out", true);//����out�ļ�
		if(outFile == null){
			return false;
		}
		
		String text = "ִ���ļ�·��Ϊ��" + getStringCurrentPath() + "\n���Ϊ��x=" + result;
		
		//�����ļ��ı�
		if(outFile.setText(text) == false){
			return false;
		}
		
		//�����ļ�
		if(disk.saveDentry(outFile) == false){//�����ļ�ʧ��
			return false;
		}
		
		//����Ŀ¼
		if(saveCurrentDir() == false){//���浱ǰĿ¼ʧ��
			getCurrentDir().removeDentry(outFile);
			disk.recycleDentry(outFile);//����Ϊ�ļ�������̿�
			return false;
		}
		
		saveAllDirs();//����ȫ��·��
		
		currentPath = oldPath;//�ָ���ǰ·��
		
		return true;
	}
	
	//ˢ�� ״̬
	public String refresh(){
		//ˢ�´��̣����¶�ȡ������
		disk.refresh();
		//���¶�ȡ��Ŀ¼
		disk.readDirectory(root);
		
		
		String path = getStringCurrentPath();//��ǰĿ¼String��
		//��ǰĿ¼�ص���Ŀ¼
		this.currentPath.clear();
		this.currentPath.add(root);
		
		//��������������������Ŀ¼��
		Interpreter iper = new Interpreter("chadir " + path);
		//���·��
		checkDirs(iper.getDirs());

		return "��ˢ�£�";
	}
	
//	public RootDirectory getRoot() {
//		return root;
//	}
//
//	public DiskManager getDisk() {
//		return disk;
//	}
}
