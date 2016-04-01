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
  * 汉字转拼音缩写
  *
  * @param str
  *            要转换的汉字字符串
  * @return String 拼音缩写
  */
 public String getPYString(String str) {
     String tempStr = "";
     for (int i = 0; i < str.length(); i++) {
         char c = str.charAt(i);
         if (c >= 33 && c <= 126) {// 字母和符号原样保留
             tempStr += String.valueOf(c);
         } else {// 累加拼音声母
             tempStr += getPYChar(String.valueOf(c));
         }
     }
     return tempStr;
 }

 /**
  * 取单个字符的拼音声母
  *
  * @param c
  *            //要转换的单个汉字
  * @return String 拼音声母
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
  * 汉字转换位汉语拼音首字母，英文字符不变 
  * @param chines 汉字 
  * @return 拼音 
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
	// 过滤特殊字符
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static String converterToFullSpell(String chines) {
		chines = StringFilter(chines);
		char[] t1 = null;
		t1 = chines.toCharArray();
		String[] t2 = new String[t1.length];
		// 设置汉字拼音输出的格式

		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断能否为汉字字符

				// System.out.println(t1[i]);

				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中

					t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后

				} else {
					// 如果不是汉字字符，间接取出字符并连接到字符串t4后

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
//     System.out.println("请输入汉字：");
     String str = "生物制剂入库表,生物制剂出库表,肥料入库表,肥料出库表,其他农用物资入库表,物资出库表,蔬菜种植";
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
    	 System.out.print("拼音：" + py);
	}
 }
}
