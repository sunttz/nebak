package usi.biz.controller;

import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import usi.biz.entity.AutoLogDto;
import usi.biz.entity.BakResult;
import usi.biz.entity.JsonResult;
import usi.biz.entity.NeServer;
import usi.biz.service.BakResultService;
import usi.biz.service.NeServerService;
import usi.biz.util.DiskInfoUtil;
import usi.biz.util.FtpUtils;
import usi.biz.util.PropertyUtil;
import usi.sys.dto.AuthInfo;
import usi.sys.dto.PageObj;
import usi.sys.entity.BusiDict;
import usi.sys.util.ConfigUtil;
import usi.sys.util.ConstantUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/netElement")
public class NeServerController {

    @Resource
    private NeServerService neServerService;

    @Resource
    BakResultService bakResultService;

    private static String rootName = ConfigUtil.getValue("download.file.path");

    /**
     * 网元serve页面
     *
     * @return
     */
    @RequestMapping(value = "/main.do", method = RequestMethod.GET)
    public String getMain(Model model) {
        Map<Object, String> map = DiskInfoUtil.DiskInfo();
        String diskinfo = "当前备份服务器已用空间容量：" + map.get("usedSpace").toString()
                + ",空闲空间容量：" + map.get("freeSpace").toString()
                + ",请注意及时做备份管理节省空间!";
        model.addAttribute("diskinfo", diskinfo);
        return "ne_server/ne_server";
    }

    /**
     * 获取所有orgName
     *
     * @return
     */
    @RequestMapping(value = "/getAllOrg.do", method = RequestMethod.POST)
    @ResponseBody
    public List<NeServer> getAllOrg(HttpSession session) {
        AuthInfo auth = (AuthInfo) session.getAttribute(ConstantUtil.AUTH_INFO);
        Long orgId = auth.getOrgId();
        String orgName = auth.getOrgName();
        List<NeServer> result = new ArrayList<NeServer>();
        List<NeServer> areaList = new ArrayList<NeServer>();
        //如果是合肥或者省公司机构只能查询本地分公司的
        if (orgId == 1L || orgId == 2L) {
            areaList = neServerService.getAllOrg();
            NeServer neServer = new NeServer();
            neServer.setOrgId(-1L);
            neServer.setOrgName("全部");
            result.add(neServer);
            result.addAll(areaList);
        } else {//如果不是，则只传本地分公司的
            NeServer neServer = new NeServer();
            neServer.setOrgId(orgId);
            neServer.setOrgName(orgName);
            result.add(neServer);
        }
        return result;
    }

    /**
     * 获取所有orgName(新增网元时使用)
     *
     * @return
     */
    @RequestMapping(value = "/getAllOrg2.do", method = RequestMethod.POST)
    @ResponseBody
    public List<NeServer> getAllOrg2(HttpSession session) {
        List<NeServer> result = neServerService.getAllOrg2();
        return result;
    }

    /**
     * 获取所有厂家名称
     *
     * @return
     */
    @RequestMapping(value = "/getAllFirms.do")
    @ResponseBody
    public List<BusiDict> getAllFirms() {
        List<BusiDict> result = neServerService.getAllFirms();
        return result;
    }

    /**
     * 获取所有网元类型
     *
     * @return
     */
    @RequestMapping(value = "/getAllDeviceType.do")
    @ResponseBody
    public List<BusiDict> getAllDeviceType() {
        List<BusiDict> result = neServerService.getAllDeviceType();
        return result;
    }

    /**
     * 获取所有网元类型
     *
     * @return
     */
    @RequestMapping(value = "/getAllDeviceType2.do")
    @ResponseBody
    public List<BusiDict> getAllDeviceType2() {
        List<BusiDict> result = new ArrayList<>();
        BusiDict busiDict = new BusiDict();
        busiDict.setDicCode("-1");
        busiDict.setDicName("全部");
        result.add(busiDict);
        result.addAll(neServerService.getAllDeviceType());
        return result;
    }

    /**
     * 获取所有网元列表
     *
     * @return
     */
    @RequestMapping(value = "/getPageAllNE.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getPageAllNE(PageObj getAllJob, HttpServletRequest request, Long orgId, String deviceType) {
        String deviceName = request.getParameter("deviceName");
        String bakType = request.getParameter("bakType");
        String saveType = request.getParameter("saveType");
        String saveDay = request.getParameter("saveDay");
        String createDate = request.getParameter("createDate");
        return neServerService.getPageAllNE(getAllJob, orgId, deviceType, deviceName, bakType, saveType, saveDay, createDate);
    }

    /**
     * 网元备份
     *
     * @return
     */
    @RequestMapping(value = "/bakNow.do", method = RequestMethod.POST)
    @ResponseBody
    public String bakNow(String ids) {
        return neServerService.bakNow(ids);
    }


    /**
     * 备份服务器的所有文件夹页面
     *
     * @return
     */
    @RequestMapping(value = "/resultmain.do", method = RequestMethod.GET)
    public String getResultMain(Model model) {
        model.addAttribute("filePath", rootName);
        return "ne_server/result";
    }

    /**
     * 获取所有网元备份后列表
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getResult.do", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> getResult(HttpSession session, String orgName, String dateTime, String filePath) throws Exception {
        orgName = orgName.equals("全部") ? "" : orgName;
        return neServerService.getResult(orgName, dateTime, filePath);
    }

    /**
     * 自动备份初始化页面
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/autoResult.do", method = RequestMethod.GET)
    public String autoResult(Model model) {
        return "ne_server/autoResultInit";
    }

    /**
     * 获取所有网元备份后列表
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getAutoResult.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getAutoResult(PageObj pageObj, HttpSession session, String dateTime) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (dateTime == null || dateTime.equals("")) {
            Calendar now = Calendar.getInstance();
            String year = String.valueOf(now.get(Calendar.YEAR));
            String month = String.valueOf(now.get(Calendar.MONTH) + 1);
            String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
            month = month.length() < 2 ? '0' + month : month;
            day = day.length() < 2 ? '0' + day : day;
            dateTime = year + month + day;
        }
        List<AutoLogDto> autoResult = neServerService.getAutoResult(dateTime, pageObj);
        map.put("total", pageObj.getTotal());
        map.put("rows", autoResult);
        return map;
    }

    /**
     * 网元备份进度
     *
     * @return
     */
    @RequestMapping(value = "/nebakDownloading")
    @ResponseBody
    public String nebakDownloading() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        String downloading = FtpUtils.downloading;
        map.put("downloading", downloading);
        JSONObject jsonObject = JSONObject.fromObject(map);
        return jsonObject.toString();
    }

    /**
     * 进度条刷新，数据从session当中取
     */
    @RequestMapping(value = "/flushProgress")
    @ResponseBody
    public String flushProgress(HttpServletRequest request) throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            HttpSession session = request.getSession();
            map.put("percent", session.getAttribute("percent"));// 百分比数字
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.fromObject(map);
        return jsonObject.toString();
    }

    /**
     * 实现网元文件夹的下载功能
     *
     * @param souceFileName
     * @param response
     */
    @RequestMapping(value = "uploadZipFile", method = RequestMethod.GET)
    public void uploadZipFile(String souceFileName, HttpServletRequest request, HttpServletResponse response) {
        File souceFile = new File(souceFileName);
        String zipFilePath = souceFileName;
        // 文件夹需压缩，文件不压缩
        boolean isDirectory = souceFile.isDirectory();
        if (isDirectory) {
            zipFilePath += ".zip";
        }
        String fileName = zipFilePath.substring(zipFilePath.lastIndexOf("/") + 1);
        System.out.println(zipFilePath);
        // System.out.println(fileName);
        FileInputStream fis = null;
        OutputStream out = null;
        long currentLen = 0;// 已读取文件大小
        long totleLen = 0;// 总文件大小
        double percent = 0.0; //下载进度
        HttpSession session = request.getSession();
        session.setAttribute("percent", 0);
        try {
            if (isDirectory) {
                zip(souceFile, zipFilePath);
            }
            out = new BufferedOutputStream(response.getOutputStream());
            File file = new File(zipFilePath);
            totleLen = file.length();
            // response.setContentType("application/zip ");
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            response.addHeader("Content-Length", "" + totleLen);
            response.setContentType("application/octet-stream");
            fis = new FileInputStream(file);
            // byte[] b = new byte[fis.available()];
            byte[] b = new byte[1024 * 1024 * 10];
            int i = -1;
            while ((i = fis.read(b)) != -1) {
                currentLen += i;
                out.write(b, 0, i);
                // 获取下载进度
                percent = Math.ceil(currentLen * 1.0 / totleLen * 1000) / 10.0;
                System.out.println("下载进度:" + percent + "%");
                session.setAttribute("percent", percent);
            }
            out.flush();
            if (percent == 100) {
                System.out.println("下载完成了");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File file1 = new File(zipFilePath);
            if (isDirectory && file1.isFile() && file1.exists()) {
                file1.delete();
            }
        }

    }

    private void zip(File souceFile, String destFileName) throws IOException {
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(destFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ZipOutputStream out = new ZipOutputStream(fileOut);
        zip(souceFile, out, "");
        out.close();
    }

    //实现文件和文件夹的打包
    public void zip(File souceFile, ZipOutputStream out, String base)
            throws IOException {
        if (souceFile.isDirectory()) {
            File[] files = souceFile.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (File file : files) {
                zip(file, out, base + file.getName());
            }
        } else {
            if (base.length() > 0) {
                out.putNextEntry(new ZipEntry(base));
            } else {
                out.putNextEntry(new ZipEntry(souceFile.getName()));
            }
            FileInputStream in = new FileInputStream(souceFile);

            int b;
            byte[] by = new byte[1024];
            while ((b = in.read(by)) != -1) {
                out.write(by, 0, b);
            }
            in.close();
            out.flush();
        }
    }

    /**
     * 网元serve配置页面
     *
     * @return
     */
    @RequestMapping(value = "/neServerConfig.do", method = RequestMethod.GET)
    public String neServerConfig(Model model) {
        return "ne_server/neServerConfig";
    }

    /**
     * 网元配置-保存
     *
     * @param neServer
     * @return
     */
    @RequestMapping(value = "/saveNeserver.do", method = RequestMethod.POST)
    @ResponseBody
    public boolean addNeserver(NeServer neServer) {
        boolean flag = false;
        try {
            int result = -1;
            Long serverId = neServer.getServerId();
            if (serverId != null) {
                result = neServerService.updateNeServer(neServer);
            } else {
                result = neServerService.saveNeServer(neServer);
            }
            if (result == 1) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 网元配置-删除
     *
     * @param serverIds
     * @return
     */
    @RequestMapping(value = "/deleteNeserver.do", method = RequestMethod.POST)
    @ResponseBody
    public String deleteNeserver(String serverIds) {
        String failServerIds = neServerService.deleteNeServer(serverIds);
        return failServerIds;
    }

    /**
     * 自动结果统计
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/autoResultStatistics.do", method = RequestMethod.GET)
    public String autoResultStatistics(Model model) {
        return "ne_server/autoResultStatistics";
    }

    /**
     * 备份失败列表
     *
     * @param pageObj
     * @param session
     * @param dateTime
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getFailResult.do", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getFailResult(PageObj pageObj, HttpSession session, String dateTime) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (dateTime == null || dateTime.equals("")) {
            Calendar now = Calendar.getInstance();
            String year = String.valueOf(now.get(Calendar.YEAR));
            String month = String.valueOf(now.get(Calendar.MONTH) + 1);
            String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
            month = month.length() < 2 ? '0' + month : month;
            day = day.length() < 2 ? '0' + day : day;
            dateTime = year + month + day;

        }
        List<AutoLogDto> failResult = neServerService.getFailResult(dateTime, pageObj);
        map.put("total", pageObj.getTotal());
        map.put("rows", failResult);
        return map;
    }

    /**
     * 查询指定天备份结果
     *
     * @param createDate
     * @return
     */
    @RequestMapping(value = "/getBakResultByDay.do", method = RequestMethod.POST)
    @ResponseBody
    public BakResult getBakResultByDay(String createDate) {
        BakResult bakResult = null;
        try {
            bakResult = bakResultService.queryByTime(createDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bakResult;
    }

    /**
     * 查询指定时间段备份结果
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/getBakResultByTime.do", method = RequestMethod.POST)
    @ResponseBody
    public List<BakResult> getBakResultByTime(String startDate, String endDate) {
        List<BakResult> bakResults = null;
        try {
            bakResults = bakResultService.queryBakResult(startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bakResults;
    }

    /**
     * 备份结果管理-删除
     *
     * @param delPaths 文件、文件夹路径
     * @return
     */
    @RequestMapping(value = "/delFilePaths.do", method = RequestMethod.POST)
    @ResponseBody
    public boolean delFilePaths(String delPaths) {
        boolean result = false;
        try {
            String[] delPathsTmp = delPaths.split(",");
            for (String delPath : delPathsTmp) {
                File file = new File(delPath);
                if (file.isDirectory()) {
                    neServerService.deleteFile(file);
                } else {
                    file.delete();
                }
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 导出excel模板
     *
     * @param type 模板类型
     * @param request
     * @param response
     */
    @RequestMapping(value = "/downloadTemplet.do")
    public void downloadTemplet(String type, HttpServletRequest request, HttpServletResponse response) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File tmpFile = null;
        String filename = ""; // 下载文件名
        String tmpFilename = ""; // 临时文件路径
        try {
            if ("insert".equals(type)) {
                tmpFilename = neServerService.createInsertTemplet(null);
                filename = "网元配置新增模板.xls";
            } else if ("update".equals(type)) {
                String serverIds = request.getParameter("serverIds");
                tmpFilename = neServerService.createUpdateTemplet(serverIds);
                filename = "网元配置修改模板.xls";
            }
            tmpFile = new File(tmpFilename);
            request.setCharacterEncoding("UTF-8");
            String agent = request.getHeader("User-Agent").toUpperCase();
            if ((agent.indexOf("MSIE") > 0) || ((agent.indexOf("RV") != -1) && (agent.indexOf("FIREFOX") == -1)))
                filename = URLEncoder.encode(filename, "UTF-8");
            else {
                filename = new String(filename.getBytes("UTF-8"), "ISO8859-1");
            }
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setHeader("Content-Length", String.valueOf(tmpFile.length()));
            bis = new BufferedInputStream(new FileInputStream(tmpFile));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))
                bos.write(buff, 0, bytesRead);
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                tmpFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 导入excel模板
     *
     * @param file
     * @param request
     * @return
     */
    @RequestMapping(value = "/importExcel.do")
    @ResponseBody
    public JsonResult importExcel(@RequestParam("excelFile") CommonsMultipartFile file, HttpServletRequest request) {
        if (!file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String fileType = originalFilename.substring(originalFilename.indexOf("."));// 取文件格式后缀名
            if (!fileType.equals(".xls") && !fileType.equals(".xlsx")) {
                return JsonResult.errorMsg("仅允许导入'xls'或'xlsx'格式文件!");
            }
            String excelType = "";
            if (originalFilename.indexOf("新增") > -1) {
                excelType = "insert";
            } else if (originalFilename.indexOf("修改") > -1) {
                excelType = "update";
            } else {
                return JsonResult.errorMsg("excel模板名称错误！");
            }
            String filename = excelType + System.currentTimeMillis() + fileType;// 取当前时间戳作为文件名
            String path = PropertyUtil.getStringValue("tmp.file.path") + File.separator + filename;// 存放位置
            File destFile = new File(path);
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), destFile);// 复制临时文件到指定目录下
                // todo 导入excel逻辑
                Map<String, String> result = null;
                if("insert".equals(excelType)){
                    result = neServerService.importInsertTemplet(destFile);
                }else if("update".equals(excelType)){

                }
                return JsonResult.ok(result);
            } catch (Exception e) {
                e.printStackTrace();
                return JsonResult.errorException(e.getMessage());
            }
        } else {
            return JsonResult.errorMsg("上传文件为空！");
        }
    }

    /*@RequestMapping("/wb")
    public String wb() {
        return "websocket";
    }*/
}
