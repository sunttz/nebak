package usi.biz.util;

public class FileUtil {
    /**
     * 获取短文件名,不带扩展名
     *
     * @param fileName
     * @return
     */
    public static String getShortName(String fileName) {
        if (fileName != null && fileName.length() > 0 && fileName.lastIndexOf(".") > -1) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    /**
     * 获取扩展名,带点
     *
     * @param fileName
     * @return
     */
    public static String getFileExtName(String fileName) {
        if (fileName != null && fileName.length() > 0 && fileName.lastIndexOf(".") > -1) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }
}
