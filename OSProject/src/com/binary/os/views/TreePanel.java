package com.binary.os.views;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.binary.os.filesys.dentries.CurDirFile;
import com.binary.os.filesys.dentries.Dentry;
import com.binary.os.filesys.dentries.Directory;
import com.binary.os.filesys.manager.FileManager;
import java.awt.BorderLayout;

public class TreePanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private FileManager fm;
	private JTree tree;

	public TreePanel(FileManager fm) {
		this.fm = fm;
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
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane treeView = new JScrollPane(tree);
		this.removeAll();
		setLayout(new BorderLayout(0, 0));
		this.add(treeView);
		this.validate();
	}
}
