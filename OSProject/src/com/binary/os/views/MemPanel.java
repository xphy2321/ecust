package com.binary.os.views;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MemPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int[][] bitMap = new int[4][8];
	public int bit = -1;
	
	public void paintComponent(Graphics g){
		paintTable(25,25,60,120,g);
		for(int i=0; i<4; i++){
			for(int j=0; j<8; j++){
				if(bitMap[i][j] == 1){
					fillTable(j*25,i*25,25,25,g, Color.RED, true);
				}else{
					fillTable(j*25,i*25,25,25,g, Color.WHITE, true);
				}
			}
		}
		if(bit == -1){
			return;
		}
		int i= bit/8;
		int j=bit%8;
		fillTable(j*25,i*25,25,25,g, Color.BLUE, true);
		
	}
	
    public void paintTable(int h,int w,int vertical,int horizontal, Graphics g){//��Ԫ��ĸߣ���Ԫ��Ŀ���ĸ߶ȣ���Ŀ�ȣ�������ڵĶ���
        g.setColor(Color.RED);
        for(int i=0;i<vertical;i=i+h){//����ĸ�
            for(int j=0;j<horizontal;j=j+w){//����Ŀ�
                g.draw3DRect(55+j, 45+i, w, h, true);
            }
        }
        g.setColor(Color.YELLOW);
        for(int i=0;i<4;i++){
        	g.drawChars(String.format("%2d",i).toCharArray(), 0, 2, 55-20, 45+15+i*w);
        }
        for(int i=0;i<8;i++){
        	g.drawChars(String.format("%2d",i).toCharArray(), 0, 2, 55+5+i*h, 45-5);
        }
        
    }
	
    public void fillTable(int x,int y,int h,int w,Graphics g,Color co, boolean isUp){//�����ɫ
        g.setColor(co);
        g.fill3DRect(55+x, 45+y, w, h, isUp);
    }
	
}
