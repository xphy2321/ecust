package com.binary.os.views;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.binary.os.filesys.dentries.Dentry;
import com.binary.os.filesys.manager.FileManager;

public class CmdPanel extends JPanel implements KeyListener {
	
	private MainFrame mainFrame;
	private FileManager fm = null;
	private String lastCommand = "";
	
	public JScrollPane resultScrolPane;
	public JTextArea resultsText;
	public JLabel currDirLabel;
	public JTextField cmdText;

	/**
	 * Create the panel.
	 */
	public CmdPanel(FileManager fm, MainFrame mainFrame) {
		this.fm = fm;
		this.mainFrame = mainFrame;
		
		setBounds(222, 287, 554, 433);
		setOpaque(false);
		setLayout(null);
		
		resultScrolPane = new JScrollPane();
		resultScrolPane.setBounds(0, 0, 502, 392);
		add(resultScrolPane);
		resultScrolPane.setOpaque(false);
		
		resultsText = new JTextArea();
		resultsText.setLineWrap(true);
		resultScrolPane.setViewportView(resultsText);
		resultsText.setFont(new Font("΢���ź�", Font.PLAIN, 15));
		resultsText.setEditable(false);
		resultsText.setOpaque(false);
		
		JLabel lblNewLabel_3 = new JLabel("\u5F53\u524D\u8DEF\u5F84\uFF1A");
		lblNewLabel_3.setFont(new Font("����", Font.BOLD, 15));
		lblNewLabel_3.setBounds(5, 394, 80, 18);
		add(lblNewLabel_3);
		
		currDirLabel = new JLabel("New Label");
		currDirLabel.setFont(new Font("Arial", Font.BOLD, 15));
		currDirLabel.setBounds(81, 394, 421, 18);
		add(currDirLabel);
		
		cmdText = new JTextField();
		cmdText.setFont(new Font("Arial", Font.BOLD, 15));
		cmdText.setBounds(0, 412, 502, 21);
		add(cmdText);
		cmdText.setColumns(10);
		cmdText.addKeyListener(this);

		currDirLabel.setText(fm.getStringCurrentPath());
		resultScrolPane.getViewport().setOpaque(false); 
	}
	
	public void scroll(){
		resultScrolPane.validate();
		JScrollBar sBar = resultScrolPane.getVerticalScrollBar();
		sBar.setValue(sBar.getMaximum());//���������ײ�
		resultScrolPane.validate();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {//�س���
			String command = cmdText.getText();//����
			lastCommand = command;//��������
			resultsText.append(fm.getStringCurrentPath() + ">" + command + "\n");//������
			cmdText.setText("");//��������
			
			String result = fm.interpret(command);//��������
			
			resultsText.append(result+"\n\n");//��ʾ���
			scroll();//���������ײ�
			currDirLabel.setText(fm.getStringCurrentPath());
			mainFrame.dirTreePanel.refresh();
		}else if (e.getKeyCode() == KeyEvent.VK_UP) {
			cmdText.setText(lastCommand);//��ȡ��һ��ָ��
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
