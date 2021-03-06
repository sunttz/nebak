package usi.biz.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import usi.biz.dao.AutoLogDao;
import usi.biz.dao.BakResultDao;
import usi.biz.dao.NeServerDao;
import usi.biz.dao.NeServerModuleDao;
import usi.biz.entity.*;
import usi.biz.util.FtpUtils;
import usi.biz.util.SftpUtils;
import usi.biz.util.excelUtil.ExcelSheetPO;
import usi.biz.util.excelUtil.ExcelUtil_Nebak;
import usi.sys.dto.PageObj;
import usi.sys.entity.BusiDict;
import usi.sys.util.ConfigUtil;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NeServerService {
    @Resource
    private NeServerDao neServerDao;
    @Resource
    private NeServerModuleDao neServerModuleDao;
    @Resource
    private AutoLogDao autoLogDao;
    @Resource
    private BakResultDao bakResultDao;

    private static String rootName = ConfigUtil.getValue("download.file.path");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("EEE"); // 周几
    private static Logger logger = LoggerFactory.getLogger(NeServerService.class);

    // 备份ftp服务器配置(弃用)
    //private static String ftpflag=ConfigUtil.getValue("bak.ftp.flag");
    //private static String ftpIp=ConfigUtil.getValue("bak.ftp.ip");
    //private static String ftpUsername=ConfigUtil.getValue("bak.ftp.username");
    //private static String ftpPassword=ConfigUtil.getValue("bak.ftp.password");

    public List<NeServer> getAllOrg() {
        return neServerDao.getAllOrg();
    }

    public List<NeServer> getAllOrg2() {
        return neServerDao.getAllOrg2();
    }

    public List<BusiDict> getAllFirms() {
        return neServerDao.getAllFirms();
    }

    public List<BusiDict> getAllDeviceType() {
        return neServerDao.getAllDeviceType();
    }

    /**
     * 查询所有网元信息（分页）
     *
     * @return
     */
    public Map<String, Object> getPageAllNE(PageObj pageObj, Long orgId, String deviceType, String deviceName, String bakType, String saveType, String saveDay, String createDate) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<NeServer> list = neServerDao.getPageAllNE(pageObj, orgId, deviceType, deviceName, bakType, saveType, saveDay, createDate);
        map.put("total", pageObj.getTotal());
        map.put("rows", list);
        return map;
    }

    /**
     * 查询所有网元信息（不分页）
     *
     * @return
     */
    public List<NeServer> getAllNE() {
        return neServerDao.getAllNE();
    }

    /**
     * 根据时间删除自动备份日志（当前日期）
     *
     * @return
     */
    public void deleteAutoLogByTime() {
        autoLogDao.deleteAutoLogByTime();
    }

    /**
     * 查询所有网元信息（不分页）
     *
     * @return
     */
    public List<AutoLogDto> getAutoResult(String dateTime, PageObj pageObj) {
        return neServerDao.getAutoResult(dateTime, pageObj);
    }

    /**
     * 网元备份
     *
     * @return
     */
    public String bakNow(String ids) {
        String result = "";
        //本地地址
        String downloadPath = "";
        //文件地址
        String dir = "";
        //要下载的文件名
        String fileName = "";
        //主机名
        String hostname = "";
        //端口
        int port = 21;
        //用户名
        String username = "";
        //密码
        String password = "";
        //备份协议(0:ftp,1:sftp)
        String protocol = "";
        //模块名称
        String moduleName = "";
        //当天日期
        String date = getDate();
        //超时时间
        int activeTime = 3000;

        logger.info("==================网元手工备份开始");
        //第一步获取单个网元信息
        String[] serverIds = ids.split(",");
        for (int i = 0; i < serverIds.length; i++) {
            List<NeServer> list = neServerDao.getNeServerById(Long.parseLong(serverIds[i]));
            NeServer neserver = list.get(0);
            String bakType = neserver.getBakType(); // 备份类型
            // 被动取(去指定ftp主机下载)
            if ("0".equals(bakType)) {
                // 1、获取网元下载到本机的路径
                // 2、遍历网元模块，下载各模块指定ftp路径下当天和前一天修改的文件
                // 3、如果任一模块下载失败，则认为该网元备份失败
                boolean bakResult = true; // 被动取备份结果
                String neServerModuleId = neserver.getNeServerModuleId(); // 关联ID
                if (StringUtils.isEmpty(neServerModuleId)) {
                    bakResult = false;
                } else {
                    downloadPath = checkBakAddr2(neserver.getOrgName(), neserver.getDeviceType(), neserver.getDeviceName());
                    List<NeServerModule> modules = neServerModuleDao.getAllModule(neServerModuleId);
                    String moduleDownloadPath = ""; // 模块下载路径
                    if (modules.size() > 0) {
                        for (NeServerModule neServerModule : modules) {
                            moduleName = neServerModule.getModuleName();
                            moduleDownloadPath = downloadPath + File.separator + moduleName + File.separator + date;
                            File moduleDownloadFile = new File(moduleDownloadPath);
                            if (!moduleDownloadFile.exists()) {
                                moduleDownloadFile.mkdir();
                            } else {
                                deleteFile(moduleDownloadFile);
                                moduleDownloadFile.mkdir();
                            }
                            dir = neServerModule.getBakPath();
                            hostname = neServerModule.getDeviceAddr();
                            Long devicePort = neServerModule.getDevicePort(); // 备份端口
                            if (devicePort != null && devicePort != 0L) {
                                port = devicePort.intValue();
                            } else {
                                port = 21;
                            }
                            username = neServerModule.getUserName();
                            password = neServerModule.getPassWord();
                            protocol = neServerModule.getBakProtocol();
                            logger.info("==============moduleName(模块名):" + moduleName);
                            logger.info("==============moduleDownloadPath(模块下载路径):" + moduleDownloadPath);
                            logger.info("==============dir(ftp服务器路径):" + dir);
                            logger.info("==============hostname(主机名):" + hostname);
                            logger.info("==============port(端口):" + port);
                            logger.info("==============username(用户名):" + username);
                            logger.info("==============password(密码):" + password);
                            logger.info("==============protocol(备份协议):" + (("1".equals(protocol)) ? "sftp" : "ftp"));
                            boolean flag = false;
                            // sftp
                            if ("1".equals(protocol)) {
                                SftpUtils sftp = new SftpUtils(hostname, username, password, port);
                                flag = sftp.batchDownload(dir, moduleDownloadPath);
                            }
                            // ftp
                            else {
                                flag = FtpUtils.fileDownload(moduleDownloadPath, dir, fileName, hostname, port, username, password, activeTime);
                            }
                            logger.info("==============flag(返回标志位):" + flag);
                            if (!flag) {
                                bakResult = false;
                                break;
                            }
                        }
                    } else {
                        bakResult = false;
                    }
                }
                if (!bakResult) {
                    if (result.equals("")) {
                        result = serverIds[i];
                    } else {
                        result += "," + serverIds[i];
                    }
                }
            }
            // 主动推(检查指定路径是否有文件)
            else if ("1".equals(bakType)) {
                String curDay = sdf.format(new Date());// 当天日期字符串
                String bakUserdata = neserver.getBakUserdata(); // 用户数据路径
                String bakSystem = neserver.getBakSystem(); // 系统数据路径
                boolean userdataResult = true;
                boolean systemResult = true;
                // 检查当天用户数据文件是否推送成功
                if (StringUtils.isNotBlank(bakUserdata)) {
                    userdataResult = false;
                    try {
                        File userDataFile = new File(bakUserdata);
                        File[] userDatafiles = userDataFile.listFiles();
                        for (File f : userDatafiles) {
                            if (f.isDirectory()) {
                                String filename = f.getName();
                                if (filename.indexOf(curDay) > -1 && f.listFiles().length > 0) {
                                    userdataResult = true;
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 检查当天系统数据文件是否推送成功
                if (StringUtils.isNotBlank(bakSystem)) {
                    systemResult = false;
                    // 判断是否多模块网元
                    int multiModule = 0; // 模块数
                    try {
                        File bakSystemFile = new File(bakSystem);
                        File[] bakSystemFiles = bakSystemFile.listFiles();
                        for (File file : bakSystemFiles) {
                            if (file.isDirectory()) {
                                multiModule++;
                            }
                        }
                        if (multiModule > 0) {
                            int tmpNum = 0;
                            for (File f : bakSystemFiles) {
                                if (f.isDirectory()) {
                                    File[] moduleFiles = f.listFiles();
                                    for (File moduleFile : moduleFiles) {
                                        String moduleFileName = moduleFile.getName();
                                        if (moduleFileName.indexOf(curDay) > -1) {
                                            tmpNum++;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (tmpNum == multiModule) {
                                systemResult = true;
                            }
                        } else {
                            for (File f : bakSystemFiles) {
                                String fname = f.getName();
                                if (fname.indexOf(curDay) > -1) {
                                    systemResult = true;
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 用户数据文件和系统数据文件都备份成功才判定网元备份成功
                logger.info(String.format("网元【%s】用户数据文件推送结果【%s】，系统数据文件推送结果【%s】%n", neserver.getDeviceName(), userdataResult, systemResult));
                if (!(userdataResult && systemResult) || (StringUtils.isBlank(bakUserdata) && StringUtils.isBlank(bakSystem))) {
                    if (result.equals("")) {
                        result = serverIds[i];
                    } else {
                        result += "," + serverIds[i];
                    }
                }
            } else {
                if (result.equals("")) {
                    result = serverIds[i];
                } else {
                    result += "," + serverIds[i];
                }
            }
        }
        logger.info("==============result(错误结果):" + result);
        logger.info("==================网元手工备份结束");
        return result;
    }

    /**
     * 自动网元备份
     * 1、遍历网元信息，查询备份类型（被动取、主动推）、保存类型（按周、按天）
     * 2、如果保存类型为按周，判断当天是否周五，不是则删除当天数据，是则启动备份和删除过期数据流程
     * 3、如果保存类型为按天，则启动备份和删除过期数据流程
     *
     * @return
     */
    public String autoBakNow(String ids) {
        String result = "";
        //本地地址
        String downloadPath = "";
        //ftp上文件地址
        String dir = "";
        //ftp上要下载的文件名
        String fileName = "";
        //主机名
        String hostname = "";
        //端口
        int port = 21;
        //用户名
        String username = "";
        //密码
        String password = "";
        //备份协议(0:ftp,1:sftp)
        String protocol = "";
        //超时时间
        int activeTime = 3000;
        // 备份结果（1成功0失败）
        int bakFlag;
        // 备份成功数
        long succNum = 0l;
        // 备份失败数
        long failNum = 0l;
        // 备份日期,默认周五
        String bakWeek = "Fri";
        // 当天是否需要备份
        boolean isBak = true;

        //先删除日志，再进行备份
        bakResultDao.deleteBakResultByTime();
        //第一步获取单个网元信息
        String[] serverIds = ids.split(",");
        String week = sdf2.format(new Date()); // 获取当前是周几
		/*
		备份逻辑伪代码：
		  for(网元列表){
			备份类型（主动推、被动取）；
			保存类型（按周、按天）；
			if(主动推){
				if(按周 && 当天非周五){
					删除当天推送文件；
				}
				if（按周 && 当天周五 || 按天）{
					删除备份过期文件，区分按天、按周；
					执行备份逻辑，检查当天文件是否存在；
					记录日志；
				}
			}else if（被动取）{
				if（按周 && 当天周五 || 按天）{
					删除备份过期文件，区分按天、按周；
					执行备份逻辑，去指定主机下载文件；
					记录日志；
				}
			}
		}*/
        for (int i = 0; i < serverIds.length; i++) {
            isBak = false; // 是否需要备份
            bakFlag = 1; // 备份结果
            try {
                List<NeServer> list = neServerDao.getNeServerById(Long.parseLong(serverIds[i]));
                NeServer neserver = list.get(0);
                logger.info("========网元设备【" + neserver.getDeviceName() + "】备份开始=======");
                String bakType = neserver.getBakType(); // 备份类型
                String saveType = neserver.getSaveType();// 保存类型
                long saveDay = neserver.getSaveDay();// 保存份数
                // 当天是否需要备份
                if ("W".equals(saveType) && bakWeek.equals(week) || "D".equals(saveType)) {
                    isBak = true;
                }
                // 被动取(去指定ftp主机下载)
                if ("0".equals(bakType)) {
                    if ("W".equals(saveType) && bakWeek.equals(week) || "D".equals(saveType)) {
                        // 删除过期备份文件，区分按天、按周
                        deleteExpireFile_get2(neserver.getOrgName(), neserver.getDeviceType(), neserver.getDeviceName(), saveDay, saveType);
                        // 1、获取网元下载到本机的路径
                        // 2、遍历网元模块，下载各模块指定ftp路径下当天和前一天修改的文件
                        // 3、如果任一模块下载失败，则认为该网元备份失败
                        boolean bakGetResult = true; // 被动取备份结果
                        String neServerModuleId = neserver.getNeServerModuleId(); // 关联ID
                        if (StringUtils.isEmpty(neServerModuleId)) {
                            bakGetResult = false;
                        } else {
                            downloadPath = checkBakAddr2(neserver.getOrgName(), neserver.getDeviceType(), neserver.getDeviceName());
                            List<NeServerModule> modules = neServerModuleDao.getAllModule(neServerModuleId);
                            String moduleName = ""; // 模块名
                            String moduleDownloadPath = ""; // 模块下载路径
                            String date = getDate(); // 当前年月日
                            if (modules.size() > 0) {
                                for (NeServerModule neServerModule : modules) {
                                    moduleName = neServerModule.getModuleName();
                                    moduleDownloadPath = downloadPath + File.separator + moduleName + File.separator + date;
                                    File moduleDownloadFile = new File(moduleDownloadPath);
                                    if (!moduleDownloadFile.exists()) {
                                        moduleDownloadFile.mkdir();
                                    } else {
                                        deleteFile(moduleDownloadFile);
                                        moduleDownloadFile.mkdir();
                                    }
                                    dir = neServerModule.getBakPath();
                                    hostname = neServerModule.getDeviceAddr();
                                    Long devicePort = neServerModule.getDevicePort(); // 备份端口
                                    if (devicePort != null && devicePort != 0L) {
                                        port = devicePort.intValue();
                                    } else {
                                        port = 21;
                                    }
                                    username = neServerModule.getUserName();
                                    password = neServerModule.getPassWord();
                                    protocol = neServerModule.getBakProtocol();
                                    logger.info("==============moduleName(模块名):" + moduleName);
                                    logger.info("==============moduleDownloadPath(模块下载路径):" + moduleDownloadPath);
                                    logger.info("==============dir(ftp服务器路径):" + dir);
                                    logger.info("==============hostname(主机名):" + hostname);
                                    logger.info("==============port(端口):" + port);
                                    logger.info("==============username(用户名):" + username);
                                    logger.info("==============password(密码):" + password);
                                    logger.info("==============protocol(备份协议):" + (("1".equals(protocol)) ? "sftp" : "ftp"));
                                    boolean flag = false;
                                    // sftp
                                    if ("1".equals(protocol)) {
                                        SftpUtils sftp = new SftpUtils(hostname, username, password, port);
                                        flag = sftp.batchDownload(dir, moduleDownloadPath);
                                    }
                                    // ftp
                                    else {
                                        flag = FtpUtils.fileDownload(moduleDownloadPath, dir, fileName, hostname, port, username, password, activeTime);
                                    }
                                    logger.info("==============flag(返回标志位):" + flag);
                                    if (!flag) {
                                        bakGetResult = false;
                                        break;
                                    }
                                }
                            } else {
                                bakGetResult = false;
                            }
                        }
                        if (!bakGetResult) {
                            if (result.equals("")) {
                                result = serverIds[i];
                            } else {
                                result += "," + serverIds[i];
                            }
                        }
                        bakFlag = bakGetResult == true ? 1 : 0;
                    } else {
                        logger.info("该网元今天无需备份");
                    }
                }
                // 主动推(检查指定路径是否有文件)
                else if ("1".equals(bakType)) {
                    String curDay = sdf.format(new Date());// 当天日期字符串
                    String bakUserdata = neserver.getBakUserdata(); // 用户数据路径
                    String bakSystem = neserver.getBakSystem(); // 系统数据路径
                    int multiModule = 0; // 模块数
                    boolean userdataResult = true;
                    boolean systemResult = true;
                    File[] bakSystemFiles = null;
                    // 判断是否多模块网元
                    if (StringUtils.isNotBlank(bakSystem)) {
                        File bakSystemFile = new File(bakSystem);
                        bakSystemFiles = bakSystemFile.listFiles();
                        for (File file : bakSystemFiles) {
                            if (file.isDirectory()) {
                                multiModule++;
                            }
                        }
                    }
                    // 如果按周且当天非周五，删除当天推送文件
                    if ("W".equals(saveType) && !bakWeek.equals(week)) {
                        if (StringUtils.isNotBlank(bakUserdata)) {
                            File userdataFile = new File(bakUserdata);
                            File[] userdataFiles = userdataFile.listFiles();
                            for (File file : userdataFiles) {
                                if (file.isDirectory()) {
                                    String filename = file.getName();
                                    if (filename.indexOf(curDay) > -1) {
                                        deleteFile(file);
                                        logger.info("非备份日，删除当天推送文件夹%s%n", filename);
                                        break;
                                    }
                                }
                            }
                        }
                        if (StringUtils.isNotBlank(bakSystem)) {
                            if (multiModule > 0) {
                                for (File module : bakSystemFiles) {
                                    if (module.isDirectory()) {
                                        File[] files = module.listFiles();
                                        for (File f : files) {
                                            if (f.isFile()) {
                                                String fname = f.getName();
                                                if (fname.indexOf(curDay) > -1) {
                                                    f.delete();
                                                    logger.info("非备份日，删除当天推送文件%s%n", fname);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                for (File file : bakSystemFiles) {
                                    if (file.isFile()) {
                                        String filename = file.getName();
                                        if (filename.indexOf(curDay) > -1) {
                                            file.delete();
                                            logger.info(String.format("非备份日，删除当天推送文件%s", filename));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 如果按周且当天周五，或者按天备份
                    if ("W".equals(saveType) && bakWeek.equals(week) || "D".equals(saveType)) {
                        // 删除备份过期文件，区分按天、按周
                        deleteExpireFile_put(bakUserdata, bakSystem, saveDay, saveType, multiModule);
                        // 检查当天用户数据文件是否推送成功
                        if (StringUtils.isNotBlank(bakUserdata)) {
                            userdataResult = false;
                            File userDataFile = new File(bakUserdata);
                            File[] userDatafiles = userDataFile.listFiles();
                            if (userDatafiles != null) {
                                for (File f : userDatafiles) {
                                    if (f.isDirectory()) {
                                        String filename = f.getName();
                                        if (filename.indexOf(curDay) > -1 && f.listFiles().length > 0) {
                                            userdataResult = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        // 检查当天系统数据文件是否推送成功
                        if (StringUtils.isNotBlank(bakSystem)) {
                            systemResult = false;
                            // 判断是否多模块网元
                            if (multiModule > 0) {
                                int tmpNum = 0;
                                for (File f : bakSystemFiles) {
                                    if (f.isDirectory()) {
                                        File[] moduleFiles = f.listFiles();
                                        for (File moduleFile : moduleFiles) {
                                            String moduleFileName = moduleFile.getName();
                                            if (moduleFileName.indexOf(curDay) > -1) {
                                                tmpNum++;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (tmpNum == multiModule) {
                                    systemResult = true;
                                }
                            } else {
                                for (File f : bakSystemFiles) {
                                    String fname = f.getName();
                                    if (fname.indexOf(curDay) > -1) {
                                        systemResult = true;
                                        break;
                                    }
                                }
                            }
                        }
                        // 用户数据文件和系统数据文件都备份成功才判定网元备份成功
                        logger.info(String.format("网元【%s】用户数据文件推送结果【%s】，系统数据文件推送结果【%s】", neserver.getDeviceName(), userdataResult, systemResult));
                        if (!(userdataResult && systemResult) || (StringUtils.isBlank(bakUserdata) && StringUtils.isBlank(bakSystem))) {
                            bakFlag = 0;
                            if (result.equals("")) {
                                result = serverIds[i];
                            } else {
                                result += "," + serverIds[i];
                            }
                        }
                    } else {
                        logger.info("该网元今天无需备份");
                    }
                } else {
                    if (result.equals("")) {
                        result = serverIds[i];
                    } else {
                        result += "," + serverIds[i];
                    }
                    bakFlag = 0;
                }
                logger.info("========网元设备【" + neserver.getDeviceName() + "】备份结束=======");
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("网元备份", e);
                if (result.equals("")) {
                    result = serverIds[i];
                } else {
                    result += "," + serverIds[i];
                }
                bakFlag = 0;
            }
            if (isBak) {
                this.addAutoLog(Long.parseLong(serverIds[i]), bakFlag);
                succNum += bakFlag;
                failNum += bakFlag == 0 ? 1 : 0;
            }
        }
        // 保存备份结果
        BakResult bakResult = new BakResult(succNum, failNum);
        bakResultDao.saveBakResult(bakResult);
        logger.info("==============result(错误结果):" + result);
        logger.info("==============备份成功数:" + succNum);
        logger.info("==============备份失败数:" + failNum);
        return result;
    }

    /**
     * 遍历所有一级文件夹，对于空文件，删除
     */
    private void deleteEmptyFile() {
        File rootFile = new File(rootName);
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory() && f.listFiles().length == 0) {
                    f.delete();
                    System.out.printf("删除空文件%s%n", f.getName());
                }
            }
        }
    }

    /**
     * 删除主动推类型的过期备份文件
     *
     * @param bakUserdata 用户数据路径
     * @param bakSystem   系统数据路径
     * @param saveDay     保存份数
     * @param saveType    保存类型
     * @param multiModule 模块数
     */
    private void deleteExpireFile_put(String bakUserdata, String bakSystem, long saveDay, String saveType, int multiModule) {
        List<String> keepDays = getKeepDays(saveDay, saveType); // 未过期日期集合
        // 删除用户数据过期文件
        if (StringUtils.isNotBlank(bakUserdata)) {
            File userdataFile = new File(bakUserdata);
            File[] userdataFiles = userdataFile.listFiles();
            if (userdataFiles != null) {
                for (File file : userdataFiles) {
                    if (file.isDirectory()) {
                        String filename = file.getName();
                        String[] split = filename.split("_");
                        if (split.length > 1) {
                            String date = split[1].substring(0, 8);
                            if (!keepDays.contains(date)) {
                                deleteFile(file);
                                System.out.printf("删除过期备份文件夹%s%n", filename);
                            }
                        }
                    }
                }
            }
        }
        // 删除系统数据过期文件
        if (StringUtils.isNotBlank(bakSystem)) {
            File bakSystemFile = new File(bakSystem);
            File[] bakSystemFiles = bakSystemFile.listFiles();
            // 判断是否多模块网元
            if (multiModule > 0) {
                for (File module : bakSystemFiles) {
                    if (module.isDirectory()) {
                        File[] moduleFiles = module.listFiles();
                        if (moduleFiles != null) {
                            for (File f : moduleFiles) {
                                if (f.isFile()) {
                                    String fname = f.getName();
                                    String[] split = fname.split("_");
                                    if (split.length > 1) {
                                        String date = split[1].substring(0, 8);
                                        if (!keepDays.contains(date)) {
                                            f.delete();
                                            System.out.printf("删除过期备份文件%s%n", fname);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                for (File f : bakSystemFiles) {
                    if (f.isFile()) {
                        String filename = f.getName();
                        String[] split = filename.split("_");
                        if (split.length > 1) {
                            String date = split[1].substring(0, 8);
                            if (!keepDays.contains(date)) {
                                f.delete();
                                System.out.printf("删除过期备份文件%s%n", filename);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除被动取类型的过期备份文件
     *
     * @param orgName    地区
     * @param deviceType 网元类型
     * @param deviceName 网元名
     * @param saveDay    保存份数
     * @param saveType   保存类型
     */
    @Deprecated
    private void deleteExpireFile_get(String orgName, String deviceType, String deviceName, long saveDay, String saveType) throws Exception {
        List<String> keepDays = getKeepDays(saveDay, saveType);
        // 1.遍历所有一级文件夹，取出地域和网元类型匹配且已过期文件夹名放到expireFolder集合中
        List<String> expireFolders = new ArrayList<>();
        //String englishOrgName=ChineseToEnglishUtil.getPinYinHeadChar(orgName);//获取地市首字母
        String englishOrgName = neServerDao.getPinYinHeadChar(orgName);
        String matchStr = englishOrgName + "_" + deviceType; // 匹配字符
        // Date expireDate = getExpireDate(saveDay); // 过期日期
        File rootFile = new File(rootName);
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    String fileName = f.getName();
                    if (fileName.indexOf(matchStr) > -1) {
                        String[] splitName = fileName.split("_");
                        if (splitName.length == 3) {
                            String fileDateStr = splitName[2];
                            if (!keepDays.contains(fileDateStr)) {
                                expireFolders.add(fileName);
                            }
                        }
                    }
                }
            }
        }
        // 2.遍历expireFolder集合文件夹，判断当天该网元备份文件夹是否存在，存在则删除
        String matchStr2 = matchStr + "_" + deviceName;
        for (String folder : expireFolders) {
            File file = new File(rootName + File.separator + folder);
            File[] listFiles = file.listFiles();
            for (File f : listFiles) {
                String secondFileName = f.getName();
                if (secondFileName.indexOf(matchStr2) > -1) {
                    deleteFile(f);
                    System.out.printf("删除过期备份文件夹%s%n", secondFileName);
                    break;
                }
            }
        }
    }

    /**
     * 删除被动取类型的过期备份文件
     *
     * @param orgName    地区
     * @param deviceType 网元类型
     * @param deviceName 网元名
     * @param saveDay    保存份数
     * @param saveType   保存类型
     */
    private void deleteExpireFile_get2(String orgName, String deviceType, String deviceName, long saveDay, String saveType) throws Exception {
        List<String> keepDays = getKeepDays(saveDay, saveType);
        String englishOrgName = neServerDao.getPinYinHeadChar(orgName);
        String bakPath = rootName + File.separator + englishOrgName + "_" + deviceType + File.separator + deviceName;
        File bakFile = new File(bakPath);
        if (bakFile != null) {
            File[] modules = bakFile.listFiles();
            if (modules != null) {
                for (File module : modules) {
                    File[] bakFileByDay = module.listFiles();
                    if (bakFileByDay != null) {
                        for (File f : bakFileByDay) {
                            String day = f.getName();
                            if (!keepDays.contains(day)) {
                                deleteFile(f);
                                logger.info(String.format("删除过期备份文件夹%s%n", f.getAbsolutePath()));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 查询未过期日期集合
     *
     * @param saveDay
     * @param saveType
     * @return
     */
    private List<String> getKeepDays(long saveDay, String saveType) {
        List<String> keepDays = new ArrayList<>(); // 未过期日期集合
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        // 按周
        if ("W".equals(saveType)) {
            for (int i = 0; i < saveDay; i++) {
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, -i * 7);
                keepDays.add(sdf.format(calendar.getTime()));
            }
        }
        // 按天
        else if ("D".equals(saveType)) {
            for (int i = 0; i < saveDay; i++) {
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_MONTH, -i);
                keepDays.add(sdf.format(calendar.getTime()));
            }
        }
        return keepDays;
    }

    /**
     * 删除ftp超过保存天数的备份文件
     * @param bakPath 备份路径
     * @param orgName
     * @param deviceType
     * @param deviceName
     * @param saveDay
     * @throws Exception
     */
    /**
     public void deleteFtpExpireFile(String bakPath,String orgName,String deviceType,String deviceName, long saveDay) throws Exception {
     FtpUtils.deleteEmptyFile(bakPath,ftpIp,21,ftpUsername,ftpPassword,3000); // 删除空文件
     List<String> expireFolders = new ArrayList<>();
     String englishOrgName=ChineseToEnglishUtil.getPinYinHeadChar(orgName);//获取地市首字母
     String matchStr = englishOrgName + "_" + deviceType; // 匹配字符
     Date expireDate = getExpireDate(saveDay); // 过期日期
     FTPClient ftpClient = new FTPClient();
     ftpClient = FtpUtils.getFTPClient(ftpIp, 21, ftpUsername, ftpPassword,3000);
     ftpClient.setBufferSize(1024);
     ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
     final boolean changeFlag = ftpClient.changeWorkingDirectory(bakPath + File.separator);
     if(changeFlag){
     // 1.遍历所有一级文件夹，取出地域和网元类型匹配且日期在当前设备备份天数之前的文件夹名放到expireFolder集合中
     FTPFile[] ftpFiles = ftpClient.listFiles();
     for (FTPFile ftpFile : ftpFiles) {
     if(ftpFile.isDirectory()){
     String fileName = ftpFile.getName();
     if(fileName.indexOf(matchStr) > -1){
     String[] splitName = fileName.split("_");
     if(splitName.length == 3){
     String fileDateStr = splitName[2];
     Date fileDate = sdf.parse(fileDateStr);
     if(fileDate.getTime() < expireDate.getTime()){
     expireFolders.add(fileName);
     }
     }
     }
     }
     }
     // 2.遍历expireFolder集合文件夹，判断当天该网元备份文件夹是否存在，存在则删除
     String matchStr2 = matchStr + "_" + deviceName;
     for (String folder : expireFolders) {
     boolean changeFlag2 = ftpClient.changeWorkingDirectory(bakPath + File.separator + folder);
     if(changeFlag2){
     FTPFile[] ftpFiles2 = ftpClient.listFiles();
     for (FTPFile f : ftpFiles2) {
     String secondFileName = f.getName();
     if(secondFileName.indexOf(matchStr2) > -1){
     String filePath = bakPath + File.separator + folder + File.separator + secondFileName + File.separator;
     boolean deleteFlag = FtpUtils.iterateDelete(ftpClient, filePath);
     System.out.printf(String.format("删除FTP备份过期文件夹%s,结果%s%n", secondFileName, deleteFlag));
     break;
     }
     }
     }
     }
     }
     }
     */
    /**
     * 获取过期日期
     *
     * @param day
     * @return
     */
    private Date getExpireDate(long day) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -(int) day);
        date = calendar.getTime();
        return date;
    }

    /**
     * 通过id保存自动备份日志
     *
     * @return
     */
    public void addAutoLog(Long serverId, int bakFlag) {
        try {
            AutoLog record = new AutoLog();
            record.setServerId(serverId);
            record.setBakFlag(bakFlag);
            autoLogDao.saveAutoLog(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查备份服务器地址名称是否存在  不存在创建,返回路径，存在不创建
     *
     * @return
     */
    @Deprecated
    public String checkBakAddr(String orgName, String deviceType, String deviceName) {
        //获取地市首字母
        //String englishOrgName=ChineseToEnglishUtil.getPinYinHeadChar(orgName);
        String englishOrgName = neServerDao.getPinYinHeadChar(orgName);
        //获取当前年月日
        String date = getDate();
        String firstFolderName = rootName + File.separator + englishOrgName + "_" + deviceType + "_" + date;
        //检查一级目录是否存在
        File fileFirst = new File(firstFolderName);
        System.out.println("===============fileFirst:" + fileFirst);
        if (!fileFirst.exists()) {
            fileFirst.mkdir();
        }
        //检查二级目录是否存在
        String secondFolderName = firstFolderName + File.separator + englishOrgName + "_" + deviceType + "_" + deviceName + "_" + date;
        File fileSecond = new File(secondFolderName);
        System.out.println("===============fileSecond:" + fileSecond);
        if (!fileSecond.exists()) {
            fileSecond.mkdir();
        } else {
            fileSecond.delete();
            fileSecond.mkdir();
        }
        return secondFolderName;
    }

    /**
     * 检查备份服务器地址名称是否存在  不存在则创建，返回路径，存在则不创建
     *
     * @param orgName
     * @param deviceType
     * @param deviceName
     * @return
     */
    public String checkBakAddr2(String orgName, String deviceType, String deviceName) {
        String englishOrgName = neServerDao.getPinYinHeadChar(orgName);
        String firstFolderName = rootName + File.separator + englishOrgName + "_" + deviceType;
        //检查一级目录是否存在
        File fileFirst = new File(firstFolderName);
        logger.info("===============fileFirst:" + fileFirst);
        if (!fileFirst.exists()) {
            fileFirst.mkdir();
        }
        //检查二级目录是否存在
        String secondFolderName = firstFolderName + File.separator + deviceName;
        File fileSecond = new File(secondFolderName);
        logger.info("===============fileSecond:" + fileSecond);
        if (!fileSecond.exists()) {
            fileSecond.mkdir();
        }
        return secondFolderName;
    }

    /**
     * 获取当前年月日
     *
     * @return
     */
    public String getDate() {
        Calendar now = Calendar.getInstance();
        String year = String.valueOf(now.get(Calendar.YEAR));
        String month = String.valueOf(now.get(Calendar.MONTH) + 1);
        String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
        month = month.length() < 2 ? '0' + month : month;
        day = day.length() < 2 ? '0' + day : day;
        return year + month + day;
    }

    /**
     * 获取所有网元备份后列表
     *
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getResult(String orgName, String dateTime, String filePath) throws Exception {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();

        String englishOrgName = "";
        //获取地市首字母
        if (orgName != null && !orgName.equals("")) {
            //englishOrgName=ChineseToEnglishUtil.getPinYinHeadChar(orgName);
            englishOrgName = neServerDao.getPinYinHeadChar(orgName);
        }
        //标记位设置  0 无参数  1 有地市名称  2 有时间日期  3 既有地市名称也有时间日期
        int flag = 0;
        if (orgName != null && !orgName.equals("")) {
            flag = 1;
        }
        if (dateTime != null && !dateTime.equals("")) {
            flag = 2;
        }
        if (orgName != null && !orgName.equals("") && dateTime != null && !dateTime.equals("")) {
            flag = 3;
        }
        File dir = new File(filePath);
        File[] files = dir.listFiles();
        String fileName = "";
        Map<String, Object> map = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < files.length; i++) {
            File thisFile = files[i];
            map = new HashMap<String, Object>();
            fileName = files[i].getName();
            // System.out.println("===========file name:"+fileName);
            boolean isDirectory = files[i].isDirectory();
            //if(files[i].isDirectory()){//是文件夹
            switch (flag) {
                case 1: {
                    if (fileName.contains(englishOrgName)) {
                        //设置地市名称
                        map.put("orgName", orgName);
                        //设置文件夹名称
                        map.put("fileName", fileName);
                        //取得子文件夹个数
                        if (isDirectory) {
                            map.put("childFileNum", getlist(files[i]));
                        }
                        //设置文件夹时间
                        long time = files[i].lastModified();//返回文件最后修改时间，是以个long型毫秒数
                        String ctime = sdf.format(new Date(time));
                        map.put("fileDate", ctime);
                        //设置文件夹大小
                        if (isDirectory) {
                            map.put("fileSize", FtpUtils.getFormatSize(this.getFileSize(thisFile)));
                        } else {
                            map.put("fileSize", FtpUtils.getFormatSize(thisFile.length()));
                        }
                        //设置文件路径
                        map.put("filePath", filePath + "/" + fileName);
                        //设置是否文件夹
                        map.put("isDirectory", isDirectory);
                        listMap.add(map);
                    }
                    break;
                }
                case 2: {
                    if (fileName.contains(dateTime)) {
                        //设置地市名称
                        String[] nameArray = fileName.split("_");
                        map.put("orgName", neServerDao.getNameByHeadchar(nameArray[0]));
                        //设置文件夹名称
                        map.put("fileName", fileName);
                        //取得子文件夹个数
                        if (isDirectory) {
                            map.put("childFileNum", getlist(files[i]));
                        } else {
                            map.put("childFileNum", 0L);
                        }
                        //设置文件夹时间
                        long time = files[i].lastModified();//返回文件最后修改时间，是以个long型毫秒数
                        String ctime = sdf.format(new Date(time));
                        map.put("fileDate", ctime);
                        //设置文件夹大小
                        if (isDirectory) {
                            map.put("fileSize", FtpUtils.getFormatSize(this.getFileSize(thisFile)));
                        } else {
                            map.put("fileSize", FtpUtils.getFormatSize(thisFile.length()));
                        }
                        //设置文件路径
                        map.put("filePath", filePath + "/" + fileName);
                        //设置是否文件夹
                        map.put("isDirectory", isDirectory);
                        listMap.add(map);
                    }
                    break;
                }
                case 3: {
                    if (fileName.contains(dateTime) && fileName.contains(englishOrgName)) {
                        //设置地市名称
                        map.put("orgName", orgName);
                        //设置文件夹名称
                        map.put("fileName", fileName);
                        //取得子文件夹个数
                        if (isDirectory) {
                            map.put("childFileNum", getlist(files[i]));
                        } else {
                            map.put("childFileNum", 0L);
                        }
                        //设置文件夹时间
                        long time = files[i].lastModified();//返回文件最后修改时间，是以个long型毫秒数
                        String ctime = sdf.format(new Date(time));
                        map.put("fileDate", ctime);
                        //设置文件夹大小
                        if (isDirectory) {
                            map.put("fileSize", FtpUtils.getFormatSize(this.getFileSize(thisFile)));
                        } else {
                            map.put("fileSize", FtpUtils.getFormatSize(thisFile.length()));
                        }
                        //设置文件路径
                        map.put("filePath", filePath + "/" + fileName);
                        //设置是否文件夹
                        map.put("isDirectory", isDirectory);
                        listMap.add(map);
                    }
                    break;
                }
                default: {
                    //设置地市名称
                    String[] nameArray = fileName.split("_");
                    map.put("orgName", neServerDao.getNameByHeadchar(nameArray[0]));
                    //设置文件夹名称
                    map.put("fileName", fileName);
                    //取得子文件夹个数
                    if (isDirectory) {
                        map.put("childFileNum", getlist(files[i]));
                    } else {
                        map.put("childFileNum", 0L);
                    }
                    //设置文件夹时间
                    long time = files[i].lastModified();//返回文件最后修改时间，是以个long型毫秒数
                    String ctime = sdf.format(new Date(time));
                    map.put("fileDate", ctime);
                    //设置文件夹大小
                    if (isDirectory) {
                        map.put("fileSize", FtpUtils.getFormatSize(this.getFileSize(thisFile)));
                    } else {
                        map.put("fileSize", FtpUtils.getFormatSize(thisFile.length()));
                    }
                    //设置文件路径
                    map.put("filePath", filePath + "/" + fileName);
                    //设置是否文件夹
                    map.put("isDirectory", isDirectory);
                    listMap.add(map);
                }
            }
            //}
        }
        return listMap;

    }

    /**
     * 获取文件夹大小  递归
     *
     * @return
     */
    public long getFileSize(File f) throws Exception//取得文件夹大小
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 获取子文件夹个数
     *
     * @return
     */
    public long getlist(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        size = flist.length;
//        for (int i = 0; i < flist.length; i++) {
//            if (flist[i].isDirectory()) {
//               size++;
//            }
//        }
        return size;
    }

    /**
     * 递归删除文件及文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {
        File[] filearray = file.listFiles();
        if (filearray != null) {
            for (File f : filearray) {
                if (f.isDirectory()) {
                    deleteFile(f);
                } else {
                    f.delete();
                }
            }
            file.delete();
        }
    }

    /**
     * 新增网元
     *
     * @param neServer
     */
    public int saveNeServer(NeServer neServer) {
        return neServerDao.saveNeServer(neServer);
    }

    /**
     * 修改网元
     *
     * @param neServer
     * @return
     */
    public int updateNeServer(NeServer neServer) {
        return neServerDao.updateNeServer(neServer);
    }

    /**
     * 删除网元
     *
     * @param serverIds
     * @return 失败列表
     */
    public String deleteNeServer(String serverIds) {
        StringBuffer failServerIds = new StringBuffer();
        String[] ids = serverIds.split(",");
        for (String serverId : ids) {
            try {
                neServerDao.deleteNeServer(Long.parseLong(serverId));
            } catch (Exception e) {
                e.printStackTrace();
                failServerIds.append(serverId + ",");
            }
        }
        return failServerIds.toString();
    }

    public List<AutoLogDto> getFailResult(String dateTime, PageObj pageObj) {
        return neServerDao.getFailResult(dateTime, pageObj);
    }

    /**
     * 获取批量导出excel模板中的下拉字典值列表
     *
     * @return
     */
    private Map<String, String[]> getParaMap() {
        Map<String, String[]> paraMap = new HashMap();
        List<String> tmpStrs = new ArrayList<>();
        // 所属地区
        List<NeServer> areas = neServerDao.getAllOrg2();
        for (NeServer neServer : areas) {
            tmpStrs.add(neServer.getOrgName());
        }
        paraMap.put("areas", tmpStrs.toArray(new String[tmpStrs.size()]));
        // 网元类型
        tmpStrs.clear();
        List<BusiDict> deviceTypes = neServerDao.getAllDeviceType();
        for (BusiDict busiDict : deviceTypes) {
            tmpStrs.add(busiDict.getDicName());
        }
        paraMap.put("deviceTypes", tmpStrs.toArray(new String[tmpStrs.size()]));
        // 厂家
        tmpStrs.clear();
        List<BusiDict> firms = neServerDao.getAllFirms();
        for (BusiDict busiDict : firms) {
            tmpStrs.add(busiDict.getDicName());
        }
        paraMap.put("firms", tmpStrs.toArray(new String[tmpStrs.size()]));
        tmpStrs.clear();
        return paraMap;
    }

    /**
     * 生成excel新增模板
     *
     * @param neServerPojos 导入失败列表
     * @return 临时文件路径
     * @throws Exception
     */
    public String createInsertTemplet(List<NeServerPojo> neServerPojos) throws Exception {
        Map<String, String[]> paraMap = getParaMap();
        return ExcelUtil_Nebak.createNebakInsertTemplet(paraMap, neServerPojos);
    }

    /**
     * 生成excel修改模板
     *
     * @param serverIds 待修改网元ID集合
     * @return 临时文件路径
     * @throws Exception
     */
    public String createUpdateTemplet(String serverIds) throws Exception {
        Map<String, String[]> paraMap = getParaMap();
        List<NeServerPojo> neServerPojos = neServerDao.batchSelect(serverIds); // 待修改网元信息
        return ExcelUtil_Nebak.createNebakUpdateTemplet(paraMap, neServerPojos);
    }

    /**
     * 导入新增数据
     *
     * @param tmpFile
     * @return 导入成功失败数
     * @throws Exception
     */
    public Map<String, String> importInsertTemplet(File tmpFile) throws Exception {
        Map<String, String> result = new HashMap<>();
        List<ExcelSheetPO> excelSheetPOs = ExcelUtil_Nebak.readNebakInsertTemplet(tmpFile); // 导入数据
        //List<NeServerPojo> neServerPojos = new ArrayList<>(); // 导入失败列表
        int successNum_get = 0; // 导入成功数量（被动取）
        int successNum_put = 0; // 导入成功数量（主动推）
        int failNum_get = 0; // 导入失败数量（被动取）
        int failNum_put = 0; // 导入失败数量（主动推）
        //String insertFailTempletName = ""; // 导入失败excel名称
        /*
         * 1、解析数据到NeServer和NeServerModule对象中，若校验失败则不处理
         * 2、执行插入数据库逻辑，若执行失败则不处理
         * 3、返回执行成功、失败数量
         */
        if (excelSheetPOs.size() == 2) {
            ExcelSheetPO excelSheetPO = null;
            List<List<Object>> dataList = null;
            //NeServerPojo neServerPojo = null;
            NeServer neServer = null;
            NeServerModule neServerModule = null;

            //网元信息
            Long orgId = null; //机构ID
            String orgName; //机构名称
            String deviceName; //设备名称
            String deviceType; //设备类型
            String remarks; //备注
            String bakType; //备份类型(0被动取1主动推)
            String saveTypeTmp;
            String saveType; // 保存类型(D按天W按周)
            Long saveDay = null; // 保存份数
            String firms; // 厂家
            String bakUserdata; // 用户数据路径
            String bakSystem; // 系统数据路径
            String neServerModuleId = ""; // 关联ID

            // 模块信息
            String moduleName; //模块名
            String deviceAddr; //设备地址
            Long devicePort; //设备端口
            String bakPath; //备份路径
            String userName; //用户名
            String passWord; //密码
            String bakProtocolTmp;
            String bakProtocol; //备份协议(0=ftp、1=sftp)

            // 上一条网元信息(用于判断是否多模块网元)
            String orgNamePrev = ""; //机构名称
            String deviceNamePrev = ""; //设备名称
            String deviceTypePrev = ""; //设备类型
            String remarksPrev = ""; //备注
            String saveTypePrev = ""; // 保存类型(D按天W按周)
            Long saveDayPrev = null; // 保存份数
            String firmsPrev = ""; // 厂家

            excelSheetPO = excelSheetPOs.get(0); // 被动取的数据
            if ("被动取".equals(excelSheetPO.getSheetName())) {
                dataList = excelSheetPO.getDataList();
                for (int i = 0; i < dataList.size(); i++) {
                    List<Object> row = dataList.get(i); // 行数据
                    bakType = "0";
                    try {
                        // 网元数据
                        orgName = row.get(0) != null ? row.get(0).toString() : null;
                        if (StringUtils.isNotEmpty(orgName)) {
                            String orgIdTmp = neServerDao.getOrgIdByName(orgName);
                            orgId = StringUtils.isNotEmpty(orgIdTmp) ? Long.valueOf(orgIdTmp) : null;
                        }
                        deviceName = row.get(1) != null ? row.get(1).toString() : null;
                        deviceType = row.get(2) != null ? row.get(2).toString() : null;
                        firms = row.get(3) != null ? row.get(3).toString() : null;
                        saveTypeTmp = row.get(4) != null ? row.get(4).toString() : null;
                        saveType = "按周".equals(saveTypeTmp) ? "W" : "D";
                        saveDay = row.get(5) != null ? Long.valueOf(row.get(5).toString()) : null;
                        remarks = row.get(6) != null ? row.get(6).toString() : null;
                        // 模块数据
                        moduleName = row.get(7) != null ? row.get(7).toString() : null;
                        bakProtocolTmp = row.get(8) != null ? row.get(8).toString() : null;
                        bakProtocol = "SFTP".equals(bakProtocolTmp) ? "1" : "0";
                        deviceAddr = row.get(9) != null ? row.get(9).toString() : null;
                        devicePort = row.get(10) != null ? Long.valueOf(row.get(10).toString()) : null;
                        userName = row.get(11) != null ? row.get(11).toString() : null;
                        passWord = row.get(12) != null ? row.get(12).toString() : null;
                        bakPath = row.get(13) != null ? row.get(13).toString() : null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("行数据解析出错：" + row);
                        failNum_get++;
                        continue;
                    }

                    // 校验数据
                    if (StringUtils.isEmpty(orgName) || StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(deviceType) || StringUtils.isEmpty(firms)
                            || StringUtils.isEmpty(saveType) || saveDay == null || StringUtils.isEmpty(remarks) || StringUtils.isEmpty(moduleName)
                            || StringUtils.isEmpty(bakProtocol) || StringUtils.isEmpty(deviceAddr) || !deviceAddr.matches("^(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)$") || devicePort == null || StringUtils.isEmpty(userName)
                            || StringUtils.isEmpty(passWord) || StringUtils.isEmpty(bakPath)) {
                        /*neServerPojo = new NeServerPojo();
                        neServerPojo.setBakType("0");
                        neServerPojo.setOrgName(orgName);
                        neServerPojo.setDeviceName(deviceName);
                        neServerPojo.setDeviceType(deviceType);
                        neServerPojo.setFirms(firms);
                        neServerPojo.setSaveType(saveTypeTmp);
                        neServerPojo.setSaveDay(saveDay);
                        neServerPojo.setRemarks(remarks);
                        neServerPojo.setModuleName(moduleName);
                        neServerPojo.setBakProtocol(bakProtocolTmp);
                        neServerPojo.setDeviceAddr(deviceAddr);
                        neServerPojo.setDevicePort(devicePort);
                        neServerPojo.setUserName(userName);
                        neServerPojo.setPassWord(passWord);
                        neServerPojo.setBakPath(bakPath);
                        neServerPojos.add(neServerPojo);*/
                        failNum_get++;
                        logger.error("校验数据出错：" + row);
                        continue;
                    }
                    // 入库
                    try {
                        // 若网元信息和上一条相同，则只更新模块信息
                        if (!orgName.equals(orgNamePrev) || !deviceName.equals(deviceNamePrev) || !deviceType.equals(deviceTypePrev) || !remarks.equals(remarksPrev)
                                || !saveType.equals(saveTypePrev) || saveDay.longValue() != saveDayPrev.longValue() || !firms.equals(firmsPrev)) {
                            neServerModuleId = generateModuleId();
                            neServer = new NeServer();
                            neServer.setOrgId(orgId);
                            neServer.setOrgName(orgName);
                            neServer.setDeviceType(deviceType);
                            neServer.setDeviceName(deviceName);
                            neServer.setRemarks(remarks);
                            neServer.setSaveType(saveType);
                            neServer.setSaveDay(saveDay);
                            neServer.setBakType(bakType);
                            neServer.setFirms(firms);
                            neServer.setDevicePort(0L);
                            neServer.setNeServerModuleId(neServerModuleId);
                            neServerDao.saveNeServer(neServer);
                        }
                        // 设置上一条数据
                        orgNamePrev = orgName;
                        deviceNamePrev = deviceName;
                        deviceTypePrev = deviceType;
                        remarksPrev = remarks;
                        saveTypePrev = saveType;
                        saveDayPrev = saveDay;
                        firmsPrev = firms;

                        neServerModule = new NeServerModule();
                        neServerModule.setModuleName(moduleName);
                        neServerModule.setDeviceAddr(deviceAddr);
                        neServerModule.setDevicePort(devicePort);
                        neServerModule.setUserName(userName);
                        neServerModule.setPassWord(passWord);
                        neServerModule.setBakPath(bakPath);
                        neServerModule.setBakProtocol(bakProtocol);
                        neServerModule.setNeServerModuleId(neServerModuleId);
                        neServerModuleDao.saveNeServerModule(neServerModule);
                        successNum_get++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("入库失败：" + row);
                        /*neServerPojo = new NeServerPojo();
                        neServerPojo.setBakType("0");
                        neServerPojo.setOrgName(orgName);
                        neServerPojo.setDeviceName(deviceName);
                        neServerPojo.setDeviceType(deviceType);
                        neServerPojo.setFirms(firms);
                        neServerPojo.setSaveType(saveTypeTmp);
                        neServerPojo.setSaveDay(saveDay);
                        neServerPojo.setRemarks(remarks);
                        neServerPojo.setModuleName(moduleName);
                        neServerPojo.setBakProtocol(bakProtocolTmp);
                        neServerPojo.setDeviceAddr(deviceAddr);
                        neServerPojo.setDevicePort(devicePort);
                        neServerPojo.setUserName(userName);
                        neServerPojo.setPassWord(passWord);
                        neServerPojo.setBakPath(bakPath);
                        neServerPojos.add(neServerPojo);*/
                        failNum_get++;
                        continue;
                    }
                }
            }
            excelSheetPO = excelSheetPOs.get(1); // 主动推的数据
            if ("主动推".equals(excelSheetPO.getSheetName())) {
                dataList = excelSheetPO.getDataList();
                for (int i = 0; i < dataList.size(); i++) {
                    List<Object> row = dataList.get(i); // 行数据
                    bakType = "1";
                    try {
                        // 网元数据
                        orgName = row.get(0) != null ? row.get(0).toString() : null;
                        if (StringUtils.isNotEmpty(orgName)) {
                            String orgIdTmp = neServerDao.getOrgIdByName(orgName);
                            orgId = StringUtils.isNotEmpty(orgIdTmp) ? Long.valueOf(orgIdTmp) : null;
                        }
                        deviceName = row.get(1) != null ? row.get(1).toString() : null;
                        deviceType = row.get(2) != null ? row.get(2).toString() : null;
                        firms = row.get(3) != null ? row.get(3).toString() : null;
                        saveTypeTmp = row.get(4) != null ? row.get(4).toString() : null;
                        saveType = "按周".equals(saveTypeTmp) ? "W" : "D";
                        saveDay = row.get(5) != null ? Long.valueOf(row.get(5).toString()) : null;
                        remarks = row.get(6) != null ? row.get(6).toString() : null;
                        bakUserdata = row.get(7) != null ? row.get(7).toString() : null;
                        bakSystem = row.get(8) != null ? row.get(8).toString() : null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("行数据解析出错：" + row);
                        failNum_put++;
                        continue;
                    }

                    // 校验数据
                    if (StringUtils.isEmpty(orgName) || StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(deviceType) || StringUtils.isEmpty(firms)
                            || StringUtils.isEmpty(saveType) || saveDay == null || StringUtils.isEmpty(remarks) || (StringUtils.isEmpty(bakSystem) && StringUtils.isEmpty(bakUserdata))) {
                        /*neServerPojo = new NeServerPojo();
                        neServerPojo.setOrgName(orgName);
                        neServerPojo.setDeviceName(deviceName);
                        neServerPojo.setDeviceType(deviceType);
                        neServerPojo.setFirms(firms);
                        neServerPojo.setSaveType(saveTypeTmp);
                        neServerPojo.setSaveDay(saveDay);
                        neServerPojo.setRemarks(remarks);
                        neServerPojo.setBakUserdata(bakUserdata);
                        neServerPojo.setBakSystem(bakSystem);
                        neServerPojos.add(neServerPojo);*/
                        failNum_put++;
                        logger.error("校验数据出错：" + row);
                        continue;
                    }
                    // 入库
                    try {
                        neServerModuleId = generateModuleId();
                        neServer = new NeServer();
                        neServer.setOrgId(orgId);
                        neServer.setOrgName(orgName);
                        neServer.setDeviceType(deviceType);
                        neServer.setDeviceName(deviceName);
                        neServer.setRemarks(remarks);
                        neServer.setSaveType(saveType);
                        neServer.setSaveDay(saveDay);
                        neServer.setBakType(bakType);
                        neServer.setFirms(firms);
                        neServer.setBakUserdata(bakUserdata);
                        neServer.setBakSystem(bakSystem);
                        neServer.setDevicePort(0L);
                        neServer.setNeServerModuleId(neServerModuleId);
                        neServerDao.saveNeServer(neServer);
                        successNum_put++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("入库失败：" + row);
                        /*neServerPojo = new NeServerPojo();
                        neServerPojo.setBakType("1");
                        neServerPojo.setOrgName(orgName);
                        neServerPojo.setDeviceName(deviceName);
                        neServerPojo.setDeviceType(deviceType);
                        neServerPojo.setFirms(firms);
                        neServerPojo.setSaveType(saveTypeTmp);
                        neServerPojo.setSaveDay(saveDay);
                        neServerPojo.setRemarks(remarks);
                        neServerPojo.setBakUserdata(bakUserdata);
                        neServerPojo.setBakSystem(bakSystem);
                        neServerPojos.add(neServerPojo);*/
                        failNum_put++;
                        continue;
                    }
                }
            }
        }
        /*if (neServerPojos.size() > 0) {
            insertFailTempletName = createInsertTemplet(neServerPojos);
            if(StringUtils.isNotEmpty(insertFailTempletName)){
                int len = insertFailTempletName.lastIndexOf(File.separator);
                insertFailTempletName = insertFailTempletName.substring(len + 1);
            }
        }*/
        // result.put("insertFailTempletName", insertFailTempletName);
        result.put("successNum_get", String.valueOf(successNum_get));
        result.put("successNum_put", String.valueOf(successNum_put));
        result.put("failNum_get", String.valueOf(failNum_get));
        result.put("failNum_put", String.valueOf(failNum_put));
        return result;
    }

    /**
     * 导入修改数据
     *
     * @param tmpFile
     * @return 导入成功失败数
     * @throws Exception
     */
    public Map<String, String> importUpdateTemplet(File tmpFile) throws Exception {
        Map<String, String> result = new HashMap<>();
        List<ExcelSheetPO> excelSheetPOs = ExcelUtil_Nebak.readNebakUpdateTemplet(tmpFile); // 导入数据
        int successNum_get = 0; // 导入成功数量（被动取）
        int successNum_put = 0; // 导入成功数量（主动推）
        int failNum_get = 0; // 导入失败数量（被动取）
        int failNum_put = 0; // 导入失败数量（主动推）
        /*
         * 1、解析数据到NeServer和NeServerModule对象中，若校验失败则不处理
         * 2、执行修改数据逻辑，若执行失败则不处理
         * 3、返回执行成功、失败数量
         */
        if (excelSheetPOs.size() == 2) {
            ExcelSheetPO excelSheetPO = null;
            List<List<Object>> dataList = null;
            NeServer neServer = null;
            NeServerModule neServerModule = null;

            //网元信息
            Long orgId = null; //机构ID
            String orgName; //机构名称
            String deviceName; //设备名称
            String deviceType; //设备类型
            String remarks; //备注
            String bakType; //备份类型(0被动取1主动推)
            String saveTypeTmp;
            String saveType; // 保存类型(D按天W按周)
            Long saveDay = null; // 保存份数
            String firms; // 厂家
            String bakUserdata; // 用户数据路径
            String bakSystem; // 系统数据路径
            Long serverId = -1L; // 网元ID

            // 模块信息
            String moduleName; //模块名
            String deviceAddr; //设备地址
            Long devicePort; //设备端口
            String bakPath; //备份路径
            String userName; //用户名
            String passWord; //密码
            String bakProtocolTmp;
            String bakProtocol; //备份协议(0=ftp、1=sftp)
            Long moduleId = null; // 模块ID

            // 上一条网元ID
            Long serverIdPrev = -1L;

            excelSheetPO = excelSheetPOs.get(0); // 被动取的数据
            if ("被动取".equals(excelSheetPO.getSheetName())) {
                dataList = excelSheetPO.getDataList();
                for (int i = 0; i < dataList.size(); i++) {
                    List<Object> row = dataList.get(i); // 行数据
                    bakType = "0";
                    try {
                        // 网元数据
                        orgName = row.get(0) != null ? row.get(0).toString() : null;
                        if (StringUtils.isNotEmpty(orgName)) {
                            String orgIdTmp = neServerDao.getOrgIdByName(orgName);
                            orgId = StringUtils.isNotEmpty(orgIdTmp) ? Long.valueOf(orgIdTmp) : null;
                        }
                        deviceName = row.get(1) != null ? row.get(1).toString() : null;
                        deviceType = row.get(2) != null ? row.get(2).toString() : null;
                        firms = row.get(3) != null ? row.get(3).toString() : null;
                        saveTypeTmp = row.get(4) != null ? row.get(4).toString() : null;
                        saveType = "按周".equals(saveTypeTmp) ? "W" : "D";
                        saveDay = row.get(5) != null ? Long.valueOf(row.get(5).toString()) : null;
                        remarks = row.get(6) != null ? row.get(6).toString() : null;
                        // 模块数据
                        moduleName = row.get(7) != null ? row.get(7).toString() : null;
                        bakProtocolTmp = row.get(8) != null ? row.get(8).toString() : null;
                        bakProtocol = "SFTP".equals(bakProtocolTmp) ? "1" : "0";
                        deviceAddr = row.get(9) != null ? row.get(9).toString() : null;
                        devicePort = row.get(10) != null ? Long.valueOf(row.get(10).toString()) : null;
                        userName = row.get(11) != null ? row.get(11).toString() : null;
                        passWord = row.get(12) != null ? row.get(12).toString() : null;
                        bakPath = row.get(13) != null ? row.get(13).toString() : null;
                        serverId = row.get(14) != null ? Long.valueOf(row.get(14).toString()) : null;
                        moduleId = row.get(15) != null ? Long.valueOf(row.get(15).toString()) : null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("行数据解析出错：" + row);
                        failNum_get++;
                        continue;
                    }
                    // 校验数据
                    if (StringUtils.isEmpty(orgName) || StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(deviceType) || StringUtils.isEmpty(firms)
                            || StringUtils.isEmpty(saveType) || saveDay == null || StringUtils.isEmpty(remarks) || StringUtils.isEmpty(moduleName)
                            || StringUtils.isEmpty(bakProtocol) || StringUtils.isEmpty(deviceAddr) || !deviceAddr.matches("^(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)$") || devicePort == null || StringUtils.isEmpty(userName)
                            || StringUtils.isEmpty(passWord) || StringUtils.isEmpty(bakPath) || serverId == null || moduleId == null) {
                        failNum_get++;
                        logger.error("校验数据出错：" + row);
                        continue;
                    }
                    // 入库
                    try {
                        // 若网元ID和上一条相同，则只更新模块信息
                        if (serverId.longValue() != serverIdPrev.longValue()) {
                            neServer = neServerDao.getNeServerById(serverId).get(0);
                            neServer.setOrgId(orgId);
                            neServer.setOrgName(orgName);
                            neServer.setDeviceType(deviceType);
                            neServer.setDeviceName(deviceName);
                            neServer.setRemarks(remarks);
                            neServer.setSaveType(saveType);
                            neServer.setSaveDay(saveDay);
                            neServer.setBakType(bakType);
                            neServer.setFirms(firms);
                            neServerDao.updateNeServer(neServer);
                        }
                        // 设置上一条数据
                        serverIdPrev = serverId;

                        neServerModule = new NeServerModule();
                        neServerModule.setModuleId(moduleId);
                        neServerModule.setModuleName(moduleName);
                        neServerModule.setDeviceAddr(deviceAddr);
                        neServerModule.setDevicePort(devicePort);
                        neServerModule.setUserName(userName);
                        neServerModule.setPassWord(passWord);
                        neServerModule.setBakPath(bakPath);
                        neServerModule.setBakProtocol(bakProtocol);
                        neServerModuleDao.updateNeServerModule(neServerModule);
                        successNum_get++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("入库失败：" + row);
                        failNum_get++;
                        continue;
                    }
                }
            }
            excelSheetPO = excelSheetPOs.get(1); // 主动推的数据
            if ("主动推".equals(excelSheetPO.getSheetName())) {
                dataList = excelSheetPO.getDataList();
                for (int i = 0; i < dataList.size(); i++) {
                    List<Object> row = dataList.get(i); // 行数据
                    bakType = "1";
                    try {
                        // 网元数据
                        orgName = row.get(0) != null ? row.get(0).toString() : null;
                        if (StringUtils.isNotEmpty(orgName)) {
                            String orgIdTmp = neServerDao.getOrgIdByName(orgName);
                            orgId = StringUtils.isNotEmpty(orgIdTmp) ? Long.valueOf(orgIdTmp) : null;
                        }
                        deviceName = row.get(1) != null ? row.get(1).toString() : null;
                        deviceType = row.get(2) != null ? row.get(2).toString() : null;
                        firms = row.get(3) != null ? row.get(3).toString() : null;
                        saveTypeTmp = row.get(4) != null ? row.get(4).toString() : null;
                        saveType = "按周".equals(saveTypeTmp) ? "W" : "D";
                        saveDay = row.get(5) != null ? Long.valueOf(row.get(5).toString()) : null;
                        remarks = row.get(6) != null ? row.get(6).toString() : null;
                        bakUserdata = row.get(7) != null ? row.get(7).toString() : null;
                        bakSystem = row.get(8) != null ? row.get(8).toString() : null;
                        serverId = row.get(9) != null ? Long.valueOf(row.get(9).toString()) : null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("行数据解析出错：" + row);
                        failNum_put++;
                        continue;
                    }

                    // 校验数据
                    if (serverId == null || StringUtils.isEmpty(orgName) || StringUtils.isEmpty(deviceName) || StringUtils.isEmpty(deviceType) || StringUtils.isEmpty(firms)
                            || StringUtils.isEmpty(saveType) || saveDay == null || StringUtils.isEmpty(remarks) || (StringUtils.isEmpty(bakSystem) && StringUtils.isEmpty(bakUserdata))) {
                        failNum_put++;
                        logger.error("校验数据出错：" + row);
                        continue;
                    }
                    // 入库
                    try {
                        neServer = neServerDao.getNeServerById(serverId).get(0);
                        neServer.setOrgId(orgId);
                        neServer.setOrgName(orgName);
                        neServer.setDeviceType(deviceType);
                        neServer.setDeviceName(deviceName);
                        neServer.setRemarks(remarks);
                        neServer.setSaveType(saveType);
                        neServer.setSaveDay(saveDay);
                        neServer.setBakType(bakType);
                        neServer.setFirms(firms);
                        neServer.setBakUserdata(bakUserdata);
                        neServer.setBakSystem(bakSystem);
                        neServerDao.updateNeServer(neServer);
                        successNum_put++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("入库失败：" + row);
                        failNum_put++;
                        continue;
                    }
                }
            }
        }
        result.put("successNum_get", String.valueOf(successNum_get));
        result.put("successNum_put", String.valueOf(successNum_put));
        result.put("failNum_get", String.valueOf(failNum_get));
        result.put("failNum_put", String.valueOf(failNum_put));
        return result;
    }

    private String generateModuleId() {
        String s = UUID.randomUUID().toString();
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }
}
