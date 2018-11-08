package usi.biz.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;


public class SftpUtils {

    private static final Logger log = LoggerFactory.getLogger(SftpUtils.class); // 用来记录日志
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Session session = null;
    String privateKey = null;
    String passphrase = null;
    String host = null; // sftp服务器的IP
    String username = null; // 用户名
    String password = null; // 密码
    int timeout = 10000; // 超时时间
    int port = 22; // 端口号

    public SftpUtils() {
    }

    public SftpUtils(String host, String username, String password, int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public SftpUtils(String privateKey, String passphrase, String host, String username, int port) {
        this.privateKey = privateKey;
        this.passphrase = passphrase;
        this.host = host;
        this.username = username;
        this.port = port;
    }

    /**
     * 获得sftp连接
     *
     * @return
     */
    private ChannelSftp GetConnectSftp() {
        JSch jsch = new JSch();
        ChannelSftp channelSftp = null;
        try {
            if (privateKey != null && !"".equals(privateKey)) {
                // 使用密钥验证方式，密钥可以是有口令的密钥，也可以是没有口令的密钥
                if (passphrase != null && !"".equals(passphrase)) {
                    jsch.addIdentity(privateKey, passphrase);
                } else {
                    jsch.addIdentity(privateKey);
                }
            }
            session = jsch.getSession(username, host, port);
            // 设置密码
            if (password != null && !"".equals(password)) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");// do not verify host
            // 为session对象设置properties
            session.setConfig(config);
            // 设置超时
            session.setTimeout(timeout);
            //session.setServerAliveInterval(92000);
            session.connect();
            // 参数sftp指明要打开的连接是sftp连接
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            log.info("sftp连接成功");
        } catch (JSchException e) {
            log.error("sftp异常：连接失败", e);
        }
        return channelSftp;

    }

    /**
     * 下载
     *
     * @param directory   源文件在服务器的路径，不包括文件名
     * @param srcFilename 源文件名
     * @param dstPath     下载到本地的目标路径
     */
    public void download(String directory, String srcFilename, String dstPath) {
        ChannelSftp channelSftp = null;
        // 得到连接
        channelSftp = GetConnectSftp();
        try {
            if (channelSftp != null) {
                // 进入服务器相应路径
                channelSftp.cd(directory);
                // 进行下载
                channelSftp.get(srcFilename, dstPath);
                log.info("sftp下载成功：" + directory + "/" + srcFilename);
            }
        } catch (SftpException e) {
            log.error("sftp异常：下载失败：" + directory + "/" + srcFilename, e);
            e.printStackTrace();
        } finally {
            // 释放连接
            disconnected(channelSftp);
        }

    }

    /**
     * 下载 为批量下载做准备
     *
     * @param directory   源文件在服务器的路径，不包括文件名
     * @param srcFilename 源文件名
     * @param dstPath     下载到本地的目标路径
     * @param channelSftp
     */
    private void download(String directory, String srcFilename, String dstPath,
                          ChannelSftp channelSftp) {
        try {
            if (channelSftp != null) {
                channelSftp.cd(directory);
                channelSftp.get(srcFilename, dstPath);
                log.info("sftp下载成功：" + directory + "/" + srcFilename);
            }
        } catch (SftpException e) {
            log.error("sftp异常：下载失败：" + directory + "/" + srcFilename, e);
            e.printStackTrace();
        }
    }

    /**
     * 批量下载
     *
     * @param pathName 服务器端目录
     * @param dstPath  本地目录
     */
    public boolean batchDownload(String pathName, String dstPath) {
        boolean flag = true;
        ChannelSftp channelSftp = null;
        // 得到连接
        channelSftp = GetConnectSftp();
        FtpUtils.downloading = "";
        // 如果本地目录还没有创建，那么就在这里创建
        File filedown = new File(dstPath);
        if(!filedown.exists()){
            filedown.mkdirs();
        }
        //批量下载管理
        try {
            batchDownMag(pathName, dstPath, channelSftp);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("sftp批量下载失败：" + pathName, e);
            flag = false;
        }
        //断开连接
        disconnected(channelSftp);
        return flag;
    }

    /**
     * 批量下载管理
     *
     * @param pathName    服务器端地址
     * @param dstPath     本地地址
     * @param channelSftp
     */
    private void batchDownMag(String pathName, String dstPath,
                              ChannelSftp channelSftp) throws Exception {
        // 确保服务端地址是绝对地址
        if (!pathName.startsWith("/")) {
            pathName = "/" + pathName;
        }
        // 目录标志符，若为有效地址，则为true，否则为false
        boolean flag = openDir(pathName, channelSftp);
        if (flag) {
            try {
                Vector vv = channelSftp.ls(pathName);
                if (vv == null && vv.size() == 0) {
                    return;
                } else {
                    Date lastDate = FtpUtils.getLastDate(); // 前一天日期
                    // 遍历当前目录所有文件及子文件夹
                    for (Object object : vv) {
                        ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) object;
                        // 得到当前项的名称（可能是文件名，也可能是文件夹名）
                        String filename = entry.getFilename();
                        // 去除无关项
                        if (".".equals(filename) || "..".equals(filename)) {
                            continue;
                        }
                        if (openDir(pathName + "/" + filename, channelSftp)) {
                            // 能打开，说明是目录，接着遍历
                            String dstPathChild = dstPath + "/" + filename;
                            File file = new File(dstPathChild);
                            //若本地不存在该目录，则进行创建
                            if (!file.isDirectory()) {
                                file.mkdirs();
                            }
                            //进行递归
                            batchDownMag(pathName + "/" + filename,
                                    dstPathChild, channelSftp);
                        } else {
                            SftpATTRS attrs = entry.getAttrs();
                            int mtime = attrs.getMTime(); // 最后修改日期
                            String size = FtpUtils.getFormatSize(attrs.getSize()); // 大小
                            long millions = new Long(mtime).longValue() * 1000;
                            String lastModifiedStr = sdf.format(millions);
                            Date lastModifiedDate = sdf.parse(lastModifiedStr);
                            log.info(String.format("文件【%s】最后修改日期:%s", filename, lastModifiedStr));
                            log.info("文件大小为：" + size);
                            // 只有修改日期在今天和昨天的文件才下载备份
                            if (lastModifiedDate.after(lastDate)) {
                                FtpUtils.downloading = filename + " 【" + size + "】";
                                download(pathName, filename, dstPath, channelSftp);
                            }
                        }
                    }
                }
            } catch (SftpException e) {
                log.error("sftp遍历目录失败：" + pathName, e);
                throw new Exception("sftp遍历目录失败：" + pathName);
            }
        } else {
            log.info("sftp对应的目录" + pathName + "不存在！");
            throw new Exception("sftp对应的目录" + pathName + "不存在！");
        }
    }

    /**
     * 上传
     *
     * @param directory   上传目录，不包括文件名
     * @param srcPath     本地文件路径，包括文件名
     * @param dstFilename 在服务器中对上传文件进行命名
     */
    public void upload(String directory, String srcPath, String dstFilename) {
        ChannelSftp channelSftp = null;
        // 获取连接
        channelSftp = GetConnectSftp();
        if (channelSftp != null) {
            // 若存在directory这个目录，则进入其中；若不存在，则先在服务器先创建该目录并进入
            createDir(directory, channelSftp);
            try {
                channelSftp.put(srcPath, dstFilename);
                log.info("sftp上传成功：" + directory);
            } catch (SftpException e) {
                log.error("sftp异常：上传失败:" + directory, e);
                e.printStackTrace();
            } finally {
                // 释放连接
                disconnected(channelSftp);
            }
        }
    }

    /**
     * 删除文件
     *
     * @param directory      要删除文件所在目录，不包括文件名
     * @param deleteFilename 要删除的文件名
     */
    public void delete(String directory, String deleteFilename) {
        ChannelSftp channelSftp = null;
        // 获取连接
        channelSftp = GetConnectSftp();
        if (channelSftp != null) {
            try {
                // 进入服务器相应目录
                channelSftp.cd(directory);
                // 删除文件
                channelSftp.rm(deleteFilename);
            } catch (SftpException e) {
                log.error("sftp异常：删除失败:" + deleteFilename, e);
                e.printStackTrace();
            } finally {
                // 释放连接
                disconnected(channelSftp);
            }

        }
    }

    /**
     * 打开或进入目录
     *
     * @param directory
     * @param channelsftp
     * @return
     */
    private boolean openDir(String directory, ChannelSftp channelsftp) {
        try {
            channelsftp.cd(directory);
            return true;
        } catch (SftpException e) {
            //log.error("sftp异常：该目录不存在：" + directory, e);
            // e.printStackTrace();
            return false;
        }
    }

    /**
     * 断开sftp连接
     *
     * @param channelSftp
     */
    private void disconnected(ChannelSftp channelSftp) {
        if (channelSftp != null) {
            try {
                // 判断session是否连接
                if (channelSftp.getSession().isConnected()) {
                    // 若连接，则释放连接
                    channelSftp.getSession().disconnect();
                }
            } catch (JSchException e) {
                log.error("sftp异常：无法获取session", e);
                e.printStackTrace();
            }
            // 判断channelSftp是否连接
            if (channelSftp.isConnected()) {
                // 若连接，则释放连接
                channelSftp.disconnect();
            }
        }
    }

    /**
     * 若存在相应目录，则进入该目录中 若不存在相应目录，则会在服务器中创建并进入该目录中
     *
     * @param directory
     * @param channelSftp
     */
    private void createDir(String directory, ChannelSftp channelSftp) {
        try {
            // 判断是否存在相应目录
            if (isDirExist(directory, channelSftp)) {
                // 若存在，则进入并设置为当前目录
                channelSftp.cd(directory);
                return;
            }
            // 对路径进行拆分
            String[] pathArray = directory.split("/");
            // 加"/"设置为绝对路径
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArray) {
                // 若directory为绝对路径，则为true
                if (path.equals("")) {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(filePath.toString(), channelSftp)) {
                    channelSftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    channelSftp.mkdir(filePath.toString());
                    // 进入并设置为当前路径
                    channelSftp.cd(filePath.toString());
                }
            }
        } catch (SftpException e) {
            log.error("sftp异常：创建路径错误：" + directory, e);
            e.printStackTrace();
        }
    }

    /**
     * 判断目录是否存在
     *
     * @param directory
     * @param channelSftp
     * @return
     */
    private boolean isDirExist(String directory, ChannelSftp channelSftp) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = channelSftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (SftpException e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
            e.printStackTrace();
        }
        return isDirExistFlag;
    }
}
