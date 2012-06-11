package com.binary.os.utils;

public class ByteHelper {
	
	//���ָ����ʼ�볤�ȵ���byte����,������Χ�ķ���0
	public static byte[] getSub(byte b[], int start, int length){
		byte[] temp = new byte[length];
		if(start>=b.length){//�����ʼλ�ó���ԭ���鳤�ȣ�����ȫ0����
			return temp;
		}
		if(start+length>=b.length){//�����ȡ���ȳ���ԭ���鷶Χ��������Χ����Ԫ����0
			for(int i=start,j=0; i<b.length; i++,j++){
				temp[j] = b[i];
			}
			return temp;
		}
		for(int i=0; i<length; i++){
			temp[i] = b[start + i];
		}
		return temp;
	}
	
	//long����ת��byte���� 
	public static byte[] longToByte(long number) { 
		long temp = number; 
		byte[] b = new byte[8]; 
		for (int i = 0; i < b.length; i++) { 
			b[7-i] = new Long(temp & 0xff).byteValue();// b[0]�����λ
			temp = temp >> 8; // ������8λ 
		} 
		return b; 
	} 
	
	//byte����ת��long 
	public static long byteToLong(byte[] b) { 
		long s = 0; 
		long s0 = b[0] & 0xff;// ���λ 
		long s1 = b[1] & 0xff; 
		long s2 = b[2] & 0xff; 
		long s3 = b[3] & 0xff; 
		long s4 = b[4] & 0xff;// ���λ 
		long s5 = b[5] & 0xff; 
		long s6 = b[6] & 0xff; 
		long s7 = b[7] & 0xff; 
		
		s0 <<= 8 * 7;
		s1 <<= 8 * 6; 
		s2 <<= 8 * 5; 
		s3 <<= 8 * 4; 
		s4 <<= 24; 
		s5 <<= 16; 
		s6 <<= 8;  
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7; 
		return s; 
	} 
	
	
	public static byte[] intToByte(int number) { 
		int temp = number; 
		byte[] b = new byte[4]; 
		for (int i = 0; i < b.length; i++) { 
			b[3-i] = new Integer(temp & 0xff).byteValue();// b[0]�����λ
			temp = temp >> 8; // ������8λ 
		} 
		return b; 
	} 
	
	
	public static int byteToInt(byte[] b) { 
		int s = 0; 
		int s0 = b[0] & 0xff;// ���λ 
		int s1 = b[1] & 0xff; 
		int s2 = b[2] & 0xff; 
		int s3 = b[3] & 0xff; 
		s0 <<= 24; 
		s1 <<= 16; 
		s2 <<= 8; 
		s = s0 | s1 | s2 | s3; 
		return s; 
	} 
	
	//byteת�����޷�������
	public static int byteToUnsignedInt(byte b) { 
		int s = b & 0xff; 
		return s; 
	} 
	
	public static byte[] shortToByte(short number) { 
		int temp = number; 
		byte[] b = new byte[2]; 
		for (int i = 0; i < b.length; i++) { 
			b[1-i] = new Integer(temp & 0xff).byteValue();//b[0]�����λ
			temp = temp >> 8; // ������8λ 
		} 
		return b; 
	} 
	
	
	public static short byteToShort(byte[] b) { 
		short s = 0; 
		short s0 = (short) (b[0] & 0xff);// ���λ 
		short s1 = (short) (b[1] & 0xff); 
		s0 <<= 8; 
		s = (short) (s0 | s1); 
		return s; 
	}
	
	//�����ֽڴ�С������������65535��ת�����ֽ�����
	public static byte[] shortIntToByte(int number) { 
		int temp = number; 
		byte[] b = new byte[2]; 
		if(temp>65535){//������ִ�С���������ֽ��ܴ洢���޷������Ĵ�С �򷵻�0����
			return b;
		}
		for (int i = 0; i < b.length; i++) { 
			b[1-i] = new Integer(temp & 0xff).byteValue();//b[0]�����λ
			temp = temp >> 8; // ������8λ 
		} 
		return b; 
	} 
	
	//�����ֽ�ת�����޷��ŵ�����
	public static int byteToShortInt(byte[] b) { 
		int s = 0; 
		int s0 = b[0] & 0xff;// ���λ 
		int s1 = b[1] & 0xff; 
		s0 <<= 8; 
		s =  (s0 | s1) & 0xffff;
		return s; 
	}
	
	public static byte booleanToByte(boolean bool){
		if(bool){
			return (byte)1;
	    }else{
			return (byte)0;
		}
	}
	
	public static boolean byteToBoolean(byte b){
		if(b==0){
			return false;
	    }else{
			return true;
		}
	}
}
