package usi.biz.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 *
 *【功能描述：ftp 工具类】
 *【功能详细描述：逐条详细描述功能】
 * @author  【lfssay】
 * @see     【相关类/方法】
 * @version 【类版本号, 2013-8-19 下午4:39:37】
 * @since   【产品/模块版本】
 */

public class FtpUtils {

    public static String downloading = ""; // 正在下载的文件
    private static Logger logger = LoggerFactory.getLogger(FtpUtils.class);

    /**
     *
     *【功能描述：得到ftp 登陆结果 FtpClient】
     *【功能详细描述：功能详细描述】
     * @see   【类、类#方法、类#成员】
     * @return
     * @throws IOException
     */
    public static FTPClient getFTPClient(String hostname, int port, String username, String password,int activeTime) throws IOException{
        FTPClient ftpClient = new FTPClient();
        // 设置默认超时时间
        ftpClient.setDefaultTimeout(10 * 1000);
        // 设置连接超时时间
        ftpClient.setConnectTimeout(10 * 1000);
        // 设置数据超时时间
        ftpClient.setDataTimeout(10 * 1000);
        ftpClient.connect(hostname, port);
        // socket连接，设置socket连接超时时间
        ftpClient.setSoTimeout(10 * 1000);
        boolean flag = ftpClient.login(username, password);
        logger.info("=================获取FTP连接flag:"+flag);
        if(flag){
            ftpClient.setControlKeepAliveTimeout(activeTime);
            return ftpClient;
        }else{
            return null;
        }
    }

    /**
     *
     *【功能描述：上传文件   和文件夹】
     *【功能详细描述：功能详细描述】
     * @see   【类、类#方法、类#成员】
     * @param filePath  上传文件的路径
     * @param ftpRePath 上传后存储的相对位置  eg：/admin/pic/
     * @return true, 只有当所有流程都成功才返回true ，不为true就表示失败
     */

    @SuppressWarnings("finally")
    public static boolean fileUpload(String filePath,String ftpRePath,String hostname, int port, String username, String password,int activeTime) {
        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient = getFTPClient(hostname, port, username, password,activeTime);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("GBK");

            // 使用迭代器的方法以后，只会建立一个ftpClient 连接，方便ftp性能稳固  
            iterateUpload(ftpClient,filePath,ftpRePath);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("FTP上传失败  ", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP出现异常  ", e);
            }
            //当所有流程都成功以后返回成功  
            return true;
        }
    }

    /**
     *
     *【功能描述：上传工具类，迭代】
     *【功能详细描述：功能详细描述】
     * @see   【类、类#方法、类#成员】
     * @param ftpClient
     * @param filePath
     * @param ftpRePath
     * @throws IOException
     */
    public static void iterateUpload(FTPClient ftpClient,String filePath,String ftpRePath) throws IOException{
        File srcFile = new File(filePath);
        // 原始上传文件  
        if(srcFile.isFile()){
            FileInputStream fis = new FileInputStream(srcFile);
            ftpClient.changeWorkingDirectory(ftpRePath);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.storeFile(srcFile.getName(), fis);
        }else if(srcFile.isDirectory()){
            // 只要是文件夹，就把当前文件夹的地址创建好。  
            ftpClient.makeDirectory(ftpRePath+File.separator+srcFile.getName());
            File[] files = srcFile.listFiles();
            // 如果为空，就只创建文件夹  
            for(File f:files){
                iterateUpload(ftpClient,f.getAbsolutePath(),ftpRePath+File.separator+srcFile.getName());
            }
        }
    }

    /**
     *
     *【功能描述：ftp 下载， 指定下载ftp上某一个目录或者文件到本地】
     *【功能详细描述：功能详细描述】
     * @see   【类、类#方法、类#成员】
     * @param downloadPath 本地地址
     * @param dir ftp上文件地址（相对的如：/admin/rt/）
     * @param fileName ftp上要下载的文件名
     * @return true, 只有所有流程都成功才返回true， 不为true就表示失败
     */

    @SuppressWarnings("finally")
    public static boolean fileDownload(String downloadPath,String dir,String fileName,String hostname, int port, String username, String password,int activeTime) {
        FTPClient ftpClient = new FTPClient();
        Boolean flag=true;
        try {
            ftpClient = getFTPClient(hostname, port, username, password,activeTime);
            ftpClient.setBufferSize(1024);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            downloading = "";
            if(fileName==null||fileName.equals("")){
            	iterateDown(ftpClient,dir,downloadPath);
            }else{
            	iterateDown(ftpClient,dir,downloadPath+File.separator+fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ftp下载", e);
            flag=false;
            // throw new RuntimeException("FTP下载失败", e);
            logger.info("FTP下载失败");
        } finally {
            try {
                ftpClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                //throw new RuntimeException("关闭FTP连接失败", e);
                logger.info("关闭FTP连接失败");
            }
            return flag;
        }
    }


    /**
     *
     *【功能描述：下载功能的迭代器，】
     *【功能详细描述：功能详细描述】
     * @see   【类、类#方法、类#成员】
     * @param ftpClient ftpClient 连接器
     * @param dir ftp上的路径
     * @param downloadPath 本地路径
     * @throws IOException
     */
    public static void iterateDown(FTPClient ftpClient,String dir,String downloadPath) throws IOException{
        // 列出这个地址对应到的是文件夹还是文件
        logger.info("==============dir(目标路径):"+dir);
        logger.info("==============downloadPath(结果路径):"+downloadPath);
    	//由于apache不支持中文语言环境，通过定制类解析中文日期类型
        boolean changeResult = false; // 解决备份路径配置为不存在地址会循环创建文件bug
    	if(dir.contains(":")){
    		String[] dirArray=dir.split(":");
    		ftpClient.changeWorkingDirectory(dirArray[0]+":");
            changeResult = ftpClient.changeWorkingDirectory(dirArray[1]);
    	}else{
            changeResult = ftpClient.changeWorkingDirectory(dir);
    	}
    	if(!changeResult){
            logger.info("=============FTP路径切换失败");
            throw new RuntimeException("FTP路径切换失败");
        }
        ftpClient.enterLocalPassiveMode();
        //ftpClient.configure(new FTPClientConfig("usi.biz.util.UnixFTPEntryParser"));
        //如果当前目录还没有创建，那么就在这里创建
        File filedown = new File(downloadPath);
        if(!filedown.exists()){
            filedown.mkdirs();
        }
        Date lastDate = getLastDate();
        FTPFile[] files = ftpClient.listFiles();
        for(FTPFile f:files){
            String name = f.getName();
            if(".".equals(name) || "..".equals(name)){
                continue;
            }
            String localPath = downloadPath+File.separator+f.getName();
            File file = new File(localPath);
            if(f.isFile()){
                Date lastModifiedDate = f.getTimestamp().getTime(); // 最后修改日期
                logger.info(String.format("文件【%s】最后修改日期:%s", name, lastModifiedDate));
                // 只有修改日期在今天和昨天的文件才下载备份
                if(lastModifiedDate.after(lastDate)){
                    FileOutputStream fos = null;
                    fos = new FileOutputStream(file);
                    String size = getFormatSize(f.getSize());
                    downloading = name + " 【" + size + "】";
                    logger.info("本地文件大小为:"+size);
                    logger.info("==============localPath(本地路径):"+localPath);
                    logger.info("==============path(目标文件):"+name);
                    //ftpClient.enterLocalPassiveMode(); //通知服务器开通给一个端口，防止挂死
                    //Boolean flag=ftpClient.retrieveFile(path, fos);
                    String fileName = dir + File.separator + name;
                    Boolean flag=ftpClient.retrieveFile(new String(fileName.getBytes("UTF-8"),"ISO-8859-1"), fos);
                    logger.info("==============flag(返回下载结果):"+flag);
                    IOUtils.closeQuietly(fos);
                }
            }else if(f.isDirectory()){
                file.mkdirs();
                iterateDown(ftpClient,dir+File.separator+f.getName(),localPath);
            }
        }
    }

    /**
     * 获取前一天零点日期
     * @return
     */
    private static Date getLastDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        date = calendar.getTime();
        return date;
    }

    /**
     *
     *【功能描述：删除ftp 上指定的文件】
     *【功能详细描述：功能详细描述】
     * @see   【类、类#方法、类#成员】
     * @param ftpPath ftp上的文件路径
     * @return true 成功，false，失败
     */
    public static boolean deleteDir(String ftpPath,String hostname, int port, String username, String password,int activeTime){
        FTPClient ftpClient = new FTPClient();
        boolean flag = false;
        try {
            ftpClient = getFTPClient(hostname, port, username, password,activeTime);
            ftpClient.setControlKeepAliveTimeout(activeTime);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            flag = iterateDelete(ftpClient,ftpPath);
            ftpClient.disconnect();
        } catch (IOException e) {
            // TODO 异常处理块  
            e.printStackTrace();
        }
        return flag;
    }

    /**
     *
     *【功能描述：删除文件夹】
     *【功能详细描述：功能详细描述】
     * @see   【类、类#方法、类#成员】
     * @param ftpClient
     * @param ftpPath  文件夹的地址
     * @return true 表似成功，false 失败
     * @throws IOException
     */
    public static boolean iterateDelete(FTPClient ftpClient,String ftpPath) throws IOException{
        FTPFile[] files = ftpClient.listFiles(ftpPath);
        boolean flag = false;
        for(FTPFile f:files){
            String path = ftpPath+File.separator+f.getName();
            if(f.isFile()){
                // 是文件就删除文件  
                ftpClient.deleteFile(path);
            }else if(f.isDirectory()){
               iterateDelete(ftpClient,path);
            }
        }
        // 每次删除文件夹以后就去查看该文件夹下面是否还有文件，没有就删除该空文件夹  
        FTPFile[] files2 = ftpClient.listFiles(ftpPath);
        if(files2.length==0){
            flag = ftpClient.removeDirectory(ftpPath);
        }else{
            flag = false;
        }
        return flag;
    }

    /**
     *
     *【功能描述：删除文件】
     *【功能详细描述：功能详细描述】
     * @see   【类、类#方法、类#成员】
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath,String hostname, int port, String username, String password,int activeTime){
        boolean flag = false;
        try {
            FTPClient ftpClient = getFTPClient(hostname, port, username, password, activeTime);
            flag = ftpClient.deleteFile(filePath);
        } catch (IOException e) {
            // TODO 异常处理块  
            e.printStackTrace();
        }
        return flag;

    }

    /**
     *【功能描述：检查文件夹是否存在】
     *【功能详细描述：检查文件夹是否存在，文件夹内是否有内容】
     * @param dir ftp上路径
     * @param hostname
     * @param port
     * @param username
     * @param password
     * @param activeTime
     * @return
     */
    @Deprecated
    public static boolean dirExits(String dir,String hostname, int port, String username, String password,int activeTime) {
        FTPClient ftpClient = new FTPClient();
        Boolean flag = true;
        try {
            ftpClient = getFTPClient(hostname, port, username, password,activeTime);
            ftpClient.setBufferSize(1024);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 切换到指定的目录
            final boolean changeFlag = ftpClient.changeWorkingDirectory(dir + File.separator);
            if(!changeFlag){
                flag = false;
            }else{
                // 获得指定目录下的文件夹和文件信息
                FTPFile[] ftpFiles = ftpClient.listFiles();
                if(ftpFiles.length == 0){
                    flag = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag=false;
            throw new RuntimeException("FTP检查文件失败", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接失败", e);
            }
            return flag;
        }
    }

    /**
     * 删除指定路径下的空文件
     * @param dir
     * @param hostname
     * @param port
     * @param username
     * @param password
     * @param activeTime
     * @return
     */
    @Deprecated
    public static boolean deleteEmptyFile(String dir,String hostname, int port, String username, String password,int activeTime) {
        FTPClient ftpClient = new FTPClient();
        Boolean flag = true;
        try {
            ftpClient = getFTPClient(hostname, port, username, password,activeTime);
            ftpClient.setBufferSize(1024);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 切换到指定的目录
            final boolean changeFlag = ftpClient.changeWorkingDirectory(dir + File.separator);
            if(!changeFlag){
                flag = false;
            }else{
                // 获得指定目录下的文件夹和文件信息
                FTPFile[] ftpFiles = ftpClient.listFiles();
                if(ftpFiles != null){
                    for (FTPFile ftpFile : ftpFiles) {
                        if(ftpFile.isDirectory()){
                            String filePath = dir + File.separator + ftpFile.getName() + File.separator;
                            if(ftpClient.listFiles(filePath).length == 0){
                                FtpUtils.iterateDelete(ftpClient, filePath);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag=false;
            throw new RuntimeException("FTP删除文件失败", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接失败", e);
            }
            return flag;
        }
    }

    /**
     * 格式化文件大小
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024f;
        if (kiloByte < 1) {
            return size + "Byte(s)";
        }

        double megaByte = kiloByte / 1024f;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024f;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024f;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }
}  
