package usi.sys.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * @Description
 * @author zhang.dechang
 * @date 2015年5月11日 上午10:35:06
 */
public class SftpUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SftpUtil.class);

	// ftp主机ip
	private static final String FTP_HOSTIP = ConfigUtil.getValue("ftp.host");
	// ftp端口
	private static final int FTP_PORT = Integer.parseInt(ConfigUtil.getValue("ftp.port"));
	// ftp用户名
	private static final String USERNAME = ConfigUtil.getValue("ftp.username");
	// ftp密码
	private static final String PASSWORD = ConfigUtil.getValue("ftp.password");
	
	// 默认超时时间（单位毫秒）
	private static final int DEFAUL_TTIMEOUT = 5000;
	
	/**
	 * 连接sftp服务器
	 * @return
	 */
	public static ChannelSftp getSftpClient() {
		ChannelSftp sftp = null;
		try {
			JSch jsch = new JSch();
			Session sshSession = jsch.getSession(USERNAME, FTP_HOSTIP, FTP_PORT);
			sshSession.setPassword(PASSWORD);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.setTimeout(DEFAUL_TTIMEOUT);
			sshSession.connect();
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
			LOGGER.info("Connected to " + FTP_HOSTIP + ".");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sftp;
	}

	/**
	 * 创建目录
	 * @param dir 目录名称
	 * @param sftp
	 */
	private static void createDir(String dir, ChannelSftp sftp) {
		try {
			// 文件服务器是linux，写死了/
			String[] dirArr = dir.split("/");
			for (String tmpDir : dirArr) {
				try {
					sftp.cd(tmpDir);
				} catch (SftpException e) {
					LOGGER.info("没有目录，需要创建，异常就不打印了。。。");
					// 创建一个目录并cd到那个目录下面，为了下一次
					sftp.mkdir(tmpDir);
					sftp.cd(tmpDir);
				}
			}
		} catch (SftpException e) {
			LOGGER.info("创建目录失败了。。。");
			e.printStackTrace();
		}

	}
	/**
	 * 上传
	 * @param fis 源文件输入流
	 * @param path 路径
	 * @param fileName 新文件名
	 * @return
	 */
	public static boolean upload(InputStream fis, String path, String filename) {
		// 获取sftp客户端连接
		ChannelSftp sftp = getSftpClient();
		boolean flag = true;
		try {
			// 切换到家目录
			sftp.cd(sftp.getHome());
			// 因为创建目录的时候，已经切换到对应的目录了，所以不创建目录后不再需要cd
			createDir(path, sftp);
			sftp.put(fis, filename);
		} catch (Exception e) {
			LOGGER.info("sftp文件上传失败。。。");
			flag = false;
			e.printStackTrace();
		} finally {
			// 关闭连接
			if (sftp.isConnected()) {
				sftp.disconnect();
				sftp.exit();
			}
		}
		return flag;
	}
	
	/**
	 * 上传到指定目录
	 * @param fis
	 * @param newFileName
	 * @return
	 */
	public static boolean upload(InputStream fis, String newFileName){
		return upload(fis, ConfigUtil.getValue("ftp.basepath"), newFileName);
	}

	/**
	 * 下载
	 * @param os 输出流
	 * @param fileName 带路径的文件名
	 * @return
	 */
	public static boolean download(OutputStream os, String fileName) {
		// 获取sftp客户端连接
		ChannelSftp sftp = getSftpClient();
		boolean flag = true;
		try {
			// 切换到家目录
			sftp.cd(sftp.getHome());
			sftp.get(fileName, os);
		} catch (Exception e) {
			LOGGER.info("sftp文件下载失败。。。");
			flag = false;
			e.printStackTrace();
		} finally {
			// 关闭连接
			if (sftp.isConnected()) {
				sftp.disconnect();
			}
		}
		return flag;
	}
	/**
	 * 删除文件
	 * @param fileName 文件名含路径（相对路径）
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		// 获取sftp客户端连接
		ChannelSftp sftp = getSftpClient();
		boolean flag = true;
		try {
			// 切换到家目录
			sftp.cd(sftp.getHome());
			sftp.rm(fileName);
		} catch (Exception e) {
			LOGGER.info("sftp文件删除失败。。。");
			flag = false;
			e.printStackTrace();
		} finally {
			// 关闭连接
			if (sftp.isConnected()) {
				sftp.disconnect();
			}
		}
		return flag;
	}
}
