package usi.sys.util;
import java.util.ArrayList;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtil {
	/**
	 * 将汉字转换为全拼 modify: ckding@ustcinfo.com
	 */
	@SuppressWarnings("unused")
	private String getPinYin(String src) {
		char[] t1 = null;
		t1 = src.toCharArray();
		// System.out.println(t1.length);
		String[] t2 = new String[t1.length];
		// System.out.println(t2.length);
		// 设置汉字拼音输出的格式
		HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
		t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		t3.setVCharType(HanyuPinyinVCharType.WITH_V);
		String t4 = "";
		int t0 = t1.length;
		try {
			for (int i = 0; i < t0; i++) {
				// 判断是否为汉字字符
				// System.out.println(t1[i]);
				if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
					t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
					t4 += t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
				} else {
					// 如果不是汉字字符，直接取出字符并连接到字符串t4后
					t4 += Character.toString(t1[i]);
				}
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return t4;
	}

	/**
	 * 提取每个汉字的首字母,多音字放在数组的同一行，如下图 modify: ckding@ustcinfo.com
	 * ------------------------------- | z | c | |
	 * ------------------------------- | g | | | -------------------------------
	 * | l | y | | ------------------------------- | 2 | | |
	 * ------------------------------- | m | m | m |
	 */
	private ArrayList<String> getPinYinHeadChar(String str, ArrayList<String> pyl) {
		ArrayList<String> py = pyl;
		if (!"".equals(str)) {
			char word = str.charAt(0);// 取单个字
			// 提取汉字的首字母
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			ArrayList<String> pylTmp = new ArrayList<String>();
			if (pinyinArray == null) {// 无法解析的字符
				pinyinArray = new String[]{String.valueOf(word)};
			}
			if (pinyinArray != null) {
				String uncode = "";
				for (int j = 0; j < pinyinArray.length; j++) {
					char hpy = pinyinArray[j].charAt(0);
					if (uncode.indexOf(hpy) == -1) {// 剔除重复的首字母
						uncode += hpy;
						if (py.isEmpty()) {// 第一个字
							pylTmp.add(String.valueOf(hpy));
						} else {
							for (int i = 0; i < py.size(); i++) {
								pylTmp.add(py.get(i) + String.valueOf(hpy));
							}
						}
					}
				}
				py = pylTmp;
				pylTmp = null;
				String newstr = str.substring(1);// 截掉已经解析的首字
				return getPinYinHeadChar(newstr, py);
			}
		}
		return py;
	}

	/**
	 * 获取字符串首字母，多音字只取其中一个读音
	 */
	public static String getFirstChar(String str) {
		String uncode = "";
		if (!"".equals(str)) {
			for (int i = 0; i < str.length(); i++) {
				char word = str.charAt(i);// 取单个字
				// 提取汉字的首字母
				String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
				// ArrayList<String> pyl_tmp = new ArrayList<String>();
				if (pinyinArray == null) {// 无法解析的字符
					pinyinArray = new String[]{String.valueOf(word)};
				}
				if (pinyinArray != null) {
					char hpy = pinyinArray[0].charAt(0);
					uncode += hpy;
				}
			}
		}
		return uncode.toUpperCase();
	}

	/**
	 * 生成检索码串 modify: ckding@ustcinfo.com
	 */
	public static String seacherCode(String str) {
		ArrayList<String> pyls = new ArrayList<String>();
		String sc = "";
		PinyinUtil pu = new PinyinUtil();
		pyls = pu.getPinYinHeadChar(str, pyls);
		for (int i = 0; i < pyls.size(); i++) {
			sc += "|" + pyls.get(i);
		}
		return sc.toUpperCase();
	}

	public static void main(String[] args) {
		String searcherCode = "";
		// searcherCode =seacherCode("董长2城乐乐s");
		searcherCode = getFirstChar("董长2城乐乐s");
		System.out.println(searcherCode);
	}

}
