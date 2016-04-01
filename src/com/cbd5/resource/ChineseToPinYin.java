package com.cbd5.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;



public class ChineseToPinYin {
	   /**
  * ����תƴ����д
  *
  * @param str
  *            Ҫת���ĺ����ַ���
  * @return String ƴ����д
  */
 public String getPYString(String str) {
     String tempStr = "";
     for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         if (c >= 33 && c <= 126) {// ��ĸ�ͷ���ԭ������
             tempStr += String.valueOf(c);
         } else {// �ۼ�ƴ����ĸ
             tempStr += getPYChar(String.valueOf(c));
         }
     }
     return tempStr;
 }

 /**
  * ȡ�����ַ���ƴ����ĸ
  *
  * @param c
  *            //Ҫת���ĵ�������
  * @return String ƴ����ĸ
  */
 public String getPYChar(String c) {
     byte[] array = new byte[2];
     array = String.valueOf(c).getBytes();
     int i = (short) (array[0] - '\0' + 256) * 256 + ((short) (array[1] - '\0' + 256));
     if (i < 0xB0A1)
         return "*";
     if (i < 0xB0C5)
         return "a";
     if (i < 0xB2C1)
         return "b";
     if (i < 0xB4EE)
         return "c";
     if (i < 0xB6EA)
         return "d";
     if (i < 0xB7A2)
         return "e";
     if (i < 0xB8C1)
         return "f";
     if (i < 0xB9FE)
         return "g";
     if (i < 0xBBF7)
         return "h";
     if (i < 0xBFA6)
         return "j";
     if (i < 0xC0AC)
         return "k";
     if (i < 0xC2E8)
         return "l";
     if (i < 0xC4C3)
         return "m";
     if (i < 0xC5B6)
         return "n";
     if (i < 0xC5BE)
         return "o";
     if (i < 0xC6DA)
         return "p";
     if (i < 0xC8BB)
         return "q";
     if (i < 0xC8F6)
         return "r";
     if (i < 0xCBFA)
         return "s";
     if (i < 0xCDDA)
         return "t";
     if (i < 0xCEF4)
         return "w";
     if (i < 0xD1B9)
         return "x";
     if (i < 0xD4D1)
         return "y";
     if (i < 0xD7FA)
         return "z";
     return "*";
 }

 /** 
  * ����ת��λ����ƴ������ĸ��Ӣ���ַ����� 
  * @param chines ���� 
  * @return ƴ�� 
  */  
  public static String converterToFirstSpell(String chines){  
 	 chines = StringFilter(chines);
      String pinyinName = "";  
      char[] nameChar = chines.toCharArray();  
      HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
      defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
      defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
      for (int i = 0; i < nameChar.length; i++) {  
          if (nameChar[i] > 128) {  
              try {  
                  pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0);  
              } catch (BadHanyuPinyinOutputFormatCombination e) {  
                  e.printStackTrace();  
              }  
          }else{  
              pinyinName += nameChar[i];  
          }  
      }  
      return pinyinName;  
  }  
	// ���������ַ�
	public static String StringFilter(String str) throws PatternSyntaxException {
		// ֻ������ĸ������
		// String regEx = "[^a-zA-Z0-9]";
		// ��������������ַ�
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~��@#��%����&*��������+|{}������������������������]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static String converterToFullSpell(String chines) {
		chines = StringFilter(chines);
		char[] t1 = null;
		t1 = chines.toCharArray();
		String[] t2 = new String[t1.length];
		// ���ú���ƴ������ĸ�ʽ

		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// �ж��ܷ�Ϊ�����ַ�

				// System.out.println(t1[i]);

				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// �����ֵļ���ȫƴ���浽t2������

					t4 += t2[0];// ȡ���ú���ȫƴ�ĵ�һ�ֶ��������ӵ��ַ���t4��

				} else {
					// ������Ǻ����ַ������ȡ���ַ������ӵ��ַ���t4��

					t4 += Character.toString(t1[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return t4;
	}

 public static void main(String[] args) {
     ChineseToPinYin ctpy = new ChineseToPinYin();
//     Scanner sc = new Scanner(System.in);
//     System.out.println("�����뺺�֣�");
     String str = "�����Ƽ�����,�����Ƽ������,��������,���ϳ����,����ũ����������,���ʳ����,�߲���ֲ";
     String[] arrStrings = str.split(",");
//     String py = ctpy.getPYString(str);
//     str = new StringBuilder(str).reverse().toString();
//     System.out.println(new StringBuilder(str).reverse().toString());
//     String str2 = str.substring(0, 1);
//     String str3 = str.substring(1);
//     str = str3+str2;
//     String py0 = ctpy.converterToFullSpell(str2);
//     String py1 = ctpy.converterToFullSpell(str3);
//     String py = py1+"."+py0;
     for (int i = 0; i < arrStrings.length; i++) {
		
    	 String py = ChineseToPinYin.converterToFirstSpell(arrStrings[i]);
    	 System.out.print("ƴ����" + py);
	}
 }
}
