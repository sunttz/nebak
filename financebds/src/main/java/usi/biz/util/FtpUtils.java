package usi.biz.util;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
  
     
    public static void main(String[] args) {  
    	 String hostname = "127.0.0.1";
         int port = 21;
         String username = "business";
         String password = "business";
         int activeTime=3000;
//        boolean flag = fileUpload("E:/tt/data","/admin/");  
         boolean flag =  fileDownload("E:/tt/te/128","/admin/748/","748",hostname, port, username, password,activeTime);  
//       System.out.println(flag);  
//        boolean flag = deleteDir("/admin/3128/");  
//        boolean flag = deleteFile("/admin/2616/input/a0012.xml");  
        System.out.println(flag);  
    }  
  
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
        ftpClient.setConnectTimeout(5000);
        ftpClient.connect(hostname, port);
        boolean flag = ftpClient.login(username, password);
        System.out.println("=================获取FTP连接flag:"+flag);
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
            if(fileName==null||fileName.equals("")){
            	iterateDown(ftpClient,dir,downloadPath);  
            }else{
            	iterateDown(ftpClient,dir,downloadPath+File.separator+fileName);  
            }
        } catch (IOException e) {  
            e.printStackTrace();  
            flag=false;
            throw new RuntimeException("FTP下载失败", e);
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
    	System.out.println("==============dir(目标路径):"+dir);
    	System.out.println("==============downloadPath(结果路径):"+downloadPath);
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
            System.out.println("=============FTP路径切换失败");
            throw new RuntimeException("FTP路径切换失败");
        }
        //ftpClient.enterLocalPassiveMode();
        //ftpClient.configure(new FTPClientConfig("usi.biz.util.UnixFTPEntryParser"));
        FTPFile[] files = ftpClient.listFiles(); 
        for(FTPFile f:files){  
            //如果当前目录还没有创建，那么就在这里创建  
            File filedown = new File(downloadPath);  
            if(!filedown.exists()){  
                filedown.mkdirs();  
            }  
            String localPath = downloadPath+File.separator+f.getName(); 
            ftpClient.enterLocalPassiveMode(); //通知服务器开通给一个端口，防止挂死
            File file = new File(localPath);  
            if(f.isFile()){  
                FileOutputStream fos = null;  
                fos = new FileOutputStream(localPath);  
                System.out.println("本地文件大小为:"+f.getSize());  
                String path = f.getName();  
                System.out.println("==============localPath(本地路径):"+localPath);
                System.out.println("==============path(目标文件):"+path);
                Boolean flag=ftpClient.retrieveFile(path, fos);  
                System.out.println("==============flag(返回下载结果):"+flag);
                //Boolean flag=ftpClient.retrieveFile(new String(path.getBytes("GBK"),"ISO-8859-1"), fos);   
                IOUtils.closeQuietly(fos);  
            }else if(f.isDirectory()){  
                file.mkdirs();  
                iterateDown(ftpClient,dir+File.separator+f.getName(),localPath);  
            }  
        }  
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
        } catch (IOException e) {
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
  
}  
