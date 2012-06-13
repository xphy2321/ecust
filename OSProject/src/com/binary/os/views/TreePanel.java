package com.binary.os.views;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeSelectionModel;

import com.binary.os.filesys.dentries.CurDirFile;
import com.binary.os.filesys.dentries.Dentry;
import com.binary.os.filesys.dentries.Directory;
import com.binary.os.filesys.manager.FileManager;
import java.awt.BorderLayout;

public class TreePanel extends JPanel implements TreeWillExpandListener{
	
	private static final long serialVersionUID = 1L;
	
	
	private MainFrame mainFrame;
	private FileManager fm;
	private JTree tree;

	public TreePanel(FileManager fm, MainFrame mainFrame) {
		this.fm = fm;
		this.mainFrame = mainFrame;
		init();
	}
	
	public void init(){
		//��Ŀ¼
		Directory root = fm.getRoot();
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);
		rootNode.add(new DefaultMutableTreeNode(new CurDirFile(root)));
		
		for(Dentry dentry:root.getDentryList()){//����Ŀ¼��Ŀ¼�������
			DefaultMutableTreeNode dentryNode = new DefaultMutableTreeNode(dentry);
			rootNode.add(dentryNode);//�������ļ�
			if(dentry.isFile() == false){//�����Ŀ¼�Ļ�����һ����ǰĿ¼���ļ�
				dentryNode.add(new DefaultMutableTreeNode(new CurDirFile((Directory) dentry)));//�ӡ���ǰĿ¼���ļ�
			}
		}
        tree = new JTree(rootNode);
        tree.addTreeWillExpandListener(this);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane treeView = new JScrollPane(tree);
		this.removeAll();
		setLayout(new BorderLayout(0, 0));
		this.add(treeView);
		this.validate();
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent arg0)
			throws ExpandVetoException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void treeWillExpand(TreeExpansionEvent event)
			throws ExpandVetoException {
		//���չ���Ľڵ�
		DefaultMutableTreeNode dirNode = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
		//���Ŀ¼
		Directory dir = (Directory) dirNode.getUserObject();
		
		for(Dentry dentry:dir.getDentryList()){//����Ŀ¼��Ŀ¼�������
			DefaultMutableTreeNode dentryNode = new DefaultMutableTreeNode(dentry);
			dirNode.add(dentryNode);//�������ļ�
			if(dentry.isFile() == false){//�����Ŀ¼�Ļ�����һ����ǰĿ¼���ļ�
				dentryNode.add(new DefaultMutableTreeNode(new CurDirFile((Directory) dentry)));//�ӡ���ǰĿ¼���ļ�
			}
		}
		
		JOptionPane.showMessageDialog(null, dir.getStringAttri());
		
	}
}
