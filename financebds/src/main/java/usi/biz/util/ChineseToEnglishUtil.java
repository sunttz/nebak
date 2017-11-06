package usi.biz.util;

import net.sourceforge.pinyin4j.PinyinHelper;  
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;  
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;  
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;  
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;  
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination; 
public class ChineseToEnglishUtil {
	  // 将汉字转换为全拼  
    public static String getPingYin(String src) {  
  
        char[] t1 = null;  
        t1 = src.toCharArray();  
        String[] t2 = new String[t1.length];  
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();  
          
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);  
        String t4 = "";  
        int t0 = t1.length;  
        try {  
            for (int i = 0; i < t0; i++) {  
                // 判断是否为汉字字符  
                if (java.lang.Character.toString(t1[i]).matches(  
                        "[\\u4E00-\\u9FA5]+")) {  
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);  
                    t4 += t2[0];  
                } else  
                    t4 += java.lang.Character.toString(t1[i]);  
            }  
            // System.out.println(t4);  
            return t4;  
        } catch (BadHanyuPinyinOutputFormatCombination e1) {  
            e1.printStackTrace();  
        }  
        return t4;  
    }  
  
    // 返回中文的首字母  
    public static String getPinYinHeadChar(String str) {  
  
        String convert = "";  
        for (int j = 0; j < str.length(); j++) {  
            char word = str.charAt(j);  
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);  
            if (pinyinArray != null) {  
                convert += pinyinArray[0].charAt(0);  
            } else {  
                convert += word;  
            }  
        }  
        return convert.toUpperCase();  
    }  
    // 根据中文首字母返回安徽地市中文
    public static String getNameByHeadchar(String str){
    	String name="";
    	switch(str){
    	case "HF": name="合肥";break;
    	case "WH": name="芜湖";break;
    	case "BB": name="蚌埠";break;
    	case "HN": name="淮南";break;
    	case "MAS": name="马鞍山";break;
    	case "HB": name="淮北";break;
    	case "TL": name="铜陵";break;
    	case "AQ": name="安庆";break;
    	case "HS": name="黄山";break;
    	case "FY": name="阜阳";break;
    	case "CZ": name="滁州";break;
    	case "LA": name="六安";break;
    	case "XC": name="宣城";break;
    	case "BZ": name="亳州";break;
    	default:;
    	}
    	
    	return name;
    }
    // 将字符串转移为ASCII码  
    public static String getCnASCII(String cnStr) {  
        StringBuffer strBuf = new StringBuffer();  
        byte[] bGBK = cnStr.getBytes();  
        for (int i = 0; i < bGBK.length; i++) {  
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));  
        }  
        return strBuf.toString();  
    }  
  
    public static void main(String[] args) {  
        System.out.println(getPingYin("綦江qq县"));  
        System.out.println(getPinYinHeadChar("綦江县"));  
        System.out.println(getCnASCII("綦江县"));  
    }  
}
