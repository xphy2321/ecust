package com.binary.os.views;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.binary.os.filesys.dentries.CurDirFile;
import com.binary.os.filesys.dentries.Dentry;
import com.binary.os.filesys.dentries.Directory;
import com.binary.os.filesys.manager.FileManager;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;

public class TreePanel extends JPanel implements TreeWillExpandListener,TreeSelectionListener, MouseListener{
	
	private static final long serialVersionUID = 1L;
	
	
	private MainFrame mainFrame;
	private FileManager fm;
	private JTree tree;

	public TreePanel(FileManager fm, MainFrame mainFrame) {
		this.fm = fm;
		this.mainFrame = mainFrame;
		refresh();
	}
	
	//��ʼ��
	public void init(){
		//��Ŀ¼
		Directory root = fm.getRoot();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
		rootNode.add(new DefaultMutableTreeNode(new CurDirFile(root)));
		
		for(Dentry dentry:root.getDentryList()){//����Ŀ¼��Ŀ¼�������
			if(dentry.isHide()){//����ʾ�����ļ�
				continue;
			}
			DefaultMutableTreeNode dentryNode = new DefaultMutableTreeNode(dentry);
			rootNode.add(dentryNode);//�������ļ�
			if(dentry.isFile() == false){//�����Ŀ¼�Ļ�����һ����ǰĿ¼���ļ�
				dentryNode.add(new DefaultMutableTreeNode(new CurDirFile((Directory) dentry)));//�ӡ���ǰĿ¼���ļ�
			}
		}
        tree = new JTree(rootNode);
        tree.addTreeWillExpandListener(this);
        tree.addTreeSelectionListener(this);
        tree.addMouseListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane treeView = new JScrollPane(tree);
		this.removeAll();
		setLayout(new BorderLayout(0, 0));
		this.add(treeView);
		this.validate();
	}
	
	//ˢ��
	public void refresh(){
		
		//ǳ������ǰ·������
		LinkedList<Directory> currentPath = new LinkedList<Directory>(fm.getCurrentPath());
		
		//��ʼ��
		init();

		//��ǰ����
		int count = tree.getRowCount();
		//������
		int oldcount = 0;
		//������������Ⱦ����� ѭ��չ��
		while(oldcount!=count){
			for(int i=0; i<count; i++){
				tree.expandRow(i);//չ��
			}
			oldcount = count;
			count = tree.getRowCount();
		}
		
		//�ָ�·��
		fm.setCurrentPath(currentPath);
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent arg0)
			throws ExpandVetoException {
		
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		
		//��ýڵ�·��
		TreePath treePath = event.getPath();
		
		//��ýڵ�����
		Object[] dirNodes = treePath.getPath();
		
		//���·��������
		String[] dirs = new String[dirNodes.length];
		for(int i=0; i<dirNodes.length; i++){
			DefaultMutableTreeNode dirNode = (DefaultMutableTreeNode) dirNodes[i];
			Directory dir = (Directory) dirNode.getUserObject();
			dirs[i] = dir.getName();
		}
		
		//����㿪��Ŀ¼
		fm.acceDirs(dirs);
		
		//��õ�ǰĿ¼
		Directory currentDir = fm.getCurrentDir();
			
		//���չ���Ľڵ�
		DefaultMutableTreeNode currentDirNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();

		//����ǰĿ¼��Ŀ¼�������
		for(Dentry dentry:currentDir.getDentryList()){
			if(dentry.isHide()){//����ʾ�����ļ�
				continue;
			}
			DefaultMutableTreeNode dentryNode = new DefaultMutableTreeNode(dentry);
			currentDirNode.add(dentryNode);//�������ļ�
			if(dentry.isFile() == false){//�����Ŀ¼�Ļ�����һ����ǰĿ¼���ļ�
				dentryNode.add(new DefaultMutableTreeNode(new CurDirFile((Directory) dentry)));//�ӡ���ǰĿ¼���ļ�
			}
		}
		
	}
	
	

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode note = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); 
		Dentry dentry = (Dentry) note.getUserObject();

		fm.setInfo(dentry);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			TreePath path = tree.getSelectionPath();// ��ȡѡ�нڵ�·��
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();// ͨ��·����ָ��ָ��ýڵ�
			if (node.isLeaf())// ����ýڵ���Ҷ�ӽڵ�
			{
				if(((Dentry)node.getUserObject()).equals("�˼�Ŀ¼")){
					return;
				}
				String spath = "";
				Object[] nodes = path.getPath();
				Dentry dentry = null;
				for (Object n : nodes) {
					try{
						dentry = (Dentry) ((DefaultMutableTreeNode) n).getUserObject();
					}catch(Exception e1){
						e1.printStackTrace();
						return;
					}
					spath = spath + dentry.getFullName() + "\\";
				}
				if(dentry.getExtension().equals("exe")){
					String result = fm.interpret("run " + spath);
					mainFrame.cmdPanel.resultsText.append(result+"\n\n");//��ʾ���
					mainFrame.cmdPanel.scroll();//���������ײ�
					mainFrame.cmdPanel.currDirLabel.setText(fm.getStringCurrentPath() + ">");
					mainFrame.dirTreePanel.refresh();
					mainFrame.diskUsagePanel.repaint();
				}else{
					String result = fm.interpret("edit " + spath);
					mainFrame.cmdPanel.resultsText.append(result+"\n\n");//��ʾ���
					mainFrame.cmdPanel.scroll();//���������ײ�
					mainFrame.cmdPanel.currDirLabel.setText(fm.getStringCurrentPath() + ">");
					mainFrame.dirTreePanel.refresh();
					mainFrame.diskUsagePanel.repaint();
				}
			}

		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
