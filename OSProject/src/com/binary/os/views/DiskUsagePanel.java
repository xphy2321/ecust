package com.binary.os.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.binary.os.filesys.manager.FileManager;

public class DiskUsagePanel extends JPanel{
	
	private FileManager fm;
	boolean[] usage = null;
	
	public DiskUsagePanel(FileManager fm){
		this.fm = fm;
	}

	public void paintComponent(Graphics g){
		usage = fm.getUsage();
		paintTable(15,15,240,240,g);
		for(int i=0; i<16; i++){
			for(int j=0; j<16; j++){
				if(16*i+j == 255){
					break;
				}
				if(usage[16*i+j]){
					fillTable(j*15,i*15,15,15,g, Color.RED, true);
				}else{
					fillTable(j*15,i*15,15,15,g, Color.WHITE, true);
				}
			}
		}
		
	}
	
    public void paintTable(int h,int w,int vertical,int horizontal, Graphics g){//��Ԫ��ĸߣ���Ԫ��Ŀ���ĸ߶ȣ���Ŀ�ȣ�������ڵĶ���
        g.setColor(Color.RED);
        for(int i=0;i<vertical;i=i+h){//����ĸ�
            for(int j=0;j<horizontal;j=j+w){//����Ŀ�
                g.draw3DRect(50+j, 36+i, w, h, true);
            }
        }
    }
	
    public void fillTable(int x,int y,int h,int w,Graphics g,Color co, boolean isUp){//�����ɫ
        g.setColor(co);
        g.fill3DRect(50+x, 36+y, w, h, isUp);
    }
   
}
