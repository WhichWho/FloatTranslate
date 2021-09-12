package com.cnl.translate.core.translate;

import java.security.MessageDigest;

public class MD5{
	
	private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String md5(String src){
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(src.getBytes());
			return byteArrayToHex(md.digest());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static String byteArrayToHex(byte[] byteArray){
		char[] resultCharArray = new char[byteArray.length * 2];
		int index = 0;
		for(byte b : byteArray){
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		return new String(resultCharArray);
	}

}
