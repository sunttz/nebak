package usi.biz.util.excelUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import usi.biz.entity.NeServerPojo;
import usi.biz.util.FileUtil;
import usi.biz.util.PropertyUtil;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel工具类
 * 提供读取和写入excel的功能
 */
public class ExcelUtil_Nebak {

    /**
     * 标题样式（蓝）
     */
    private final static String STYLE_HEADER = "header";
    /**
     * 标题样式（绿）
     */
    private final static String STYLE_HEADER1 = "header1";
    /**
     * 标题样式（灰）
     */
    private final static String STYLE_HEADER2 = "header2";
    /**
     * 表头样式
     */
    private final static String STYLE_TITLE = "title";
    /**
     * 数据样式（黑）
     */
    private final static String STYLE_DATA = "data";
    /**
     * 数据样式（灰）
     */
    private final static String STYLE_DATA1 = "data1";

    /**
     * 存储样式
     */
    private static final HashMap<String, CellStyle> cellStyleMap = new HashMap<>();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 读取excel文件里面的内容（支持日期，数字，字符，函数公式，布尔类型）
     *
     * @param file        待读取excel文件
     * @param rowCount    读取行数（不填则默认获取有记录的行数）
     * @param columnCount 读取列数（不填则默认获取有记录的列数）
     * @param startRows   起始读取数据行（按sheet页顺序）
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<ExcelSheetPO> readExcel(File file, Integer rowCount, Integer columnCount, List<Integer> startRows)
            throws FileNotFoundException, IOException {

        // 根据后缀名称判断excel的版本
        String extName = FileUtil.getFileExtName(file.getName());
        Workbook wb = null;
        if (ExcelVersion.V2003.getSuffix().equals(extName)) {
            wb = new HSSFWorkbook(new FileInputStream(file));
        } else if (ExcelVersion.V2007.getSuffix().equals(extName)) {
            wb = new XSSFWorkbook(new FileInputStream(file));
        } else {
            // 无效后缀名称，这里只能保证excel的后缀名称，不能保证文件类型正确，不过没关系，在创建Workbook的时候会校验文件格式
            throw new IllegalArgumentException("Invalid excel version");
        }
        // 开始读取数据
        List<ExcelSheetPO> sheetPOs = new ArrayList<>();
        // 解析sheet
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            List<List<Object>> dataList = new ArrayList<>();
            ExcelSheetPO sheetPO = new ExcelSheetPO();
            sheetPO.setSheetName(sheet.getSheetName());
            sheetPO.setDataList(dataList);
            int readRowCount = 0;
            if (rowCount == null || rowCount > sheet.getPhysicalNumberOfRows()) {
                readRowCount = sheet.getPhysicalNumberOfRows();
            } else {
                readRowCount = rowCount;
            }
            int readStartRow = 0;
            if (startRows == null || startRows.size() < wb.getNumberOfSheets()) {
                readStartRow = sheet.getFirstRowNum();
            } else {
                readStartRow = startRows.get(i);
            }
            // 解析sheet 的行
            for (int j = readStartRow; j < readRowCount; j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                if (row.getFirstCellNum() < 0) {
                    continue;
                }
                int readColumnCount = 0;
                if (columnCount == null || columnCount > row.getLastCellNum()) {
                    readColumnCount = (int) row.getLastCellNum();
                } else {
                    readColumnCount = columnCount;
                }
                List<Object> rowValue = new LinkedList<Object>();
                // 解析sheet 的列
                for (int k = 0; k < readColumnCount; k++) {
                    Cell cell = row.getCell(k);
                    rowValue.add(getCellValue(wb, cell));
                }
                dataList.add(rowValue);
            }
            sheetPOs.add(sheetPO);
        }
        return sheetPOs;
    }

    /**
     * 获得单元格值
     *
     * @param wb
     * @param cell
     * @return
     */
    private static Object getCellValue(Workbook wb, Cell cell) {
        Object columnValue = null;
        if (cell != null) {
            DecimalFormat df = new DecimalFormat("0");// 格式化 number
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化日期字符串
            DecimalFormat nf = new DecimalFormat("0.00");// 格式化数字
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    if (StringUtils.isNotEmpty(cell.getStringCellValue())) {
                        columnValue = cell.getStringCellValue().trim();
                    } else {
                        columnValue = cell.getStringCellValue();
                    }
                    break;
                case Cell.CELL_TYPE_NUMERIC:
//                    if ("@".equals(cell.getCellStyle().getDataFormatString())) {
//                        columnValue = df.format(cell.getNumericCellValue());
//                    } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
//                        columnValue = nf.format(cell.getNumericCellValue());
//                    } else {
//                        columnValue = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
//                    }
                    if (cell.getCellStyle().getDataFormat() == 0) {
                        cell.setCellType(Cell.CELL_TYPE_STRING);
                        columnValue = String.valueOf(cell.getRichStringCellValue().getString());
                    } else {
                        columnValue = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    columnValue = cell.getBooleanCellValue();
                    break;
                case Cell.CELL_TYPE_BLANK:
                    columnValue = "";
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    // 格式单元格
                    FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
                    evaluator.evaluateFormulaCell(cell);
                    CellValue cellValue = evaluator.evaluate(cell);
                    columnValue = cellValue.getNumberValue();
                    break;
                default:
                    columnValue = cell.toString();
            }
        }
        return columnValue;
    }

    /**
     * 在硬盘上写入excel文件
     *
     * @param version     excel文件版本
     * @param excelSheets sheet页数据
     * @param filePath    写入文件路径（临时）
     * @param isInsert    新增模板or更新模板
     * @throws IOException
     */
    public static void createWorkbookAtDisk(ExcelVersion version, List<ExcelSheetPO> excelSheets, String filePath, boolean isInsert)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        if (CollectionUtils.isNotEmpty(excelSheets)) {
            cellStyleMap.clear();
            Workbook wb = createWorkBook(version, excelSheets, isInsert);
            wb.write(fos);
            fos.close();
        }
    }

    private static Workbook createWorkBook(ExcelVersion version, List<ExcelSheetPO> excelSheets, boolean isInsert) {
        Workbook wb = createWorkbook(version);
        for (int i = 0; i < excelSheets.size(); i++) {
            ExcelSheetPO excelSheetPO = excelSheets.get(i);
            if (excelSheetPO.getSheetName() == null) {
                excelSheetPO.setSheetName("sheet" + i);
            }
            // 过滤特殊字符
            Sheet tempSheet = wb.createSheet(WorkbookUtil.createSafeSheetName(excelSheetPO.getSheetName()));
            buildSheetData(wb, tempSheet, excelSheetPO, version, isInsert);
        }
        return wb;
    }

    private static Workbook createWorkbook(ExcelVersion version) {
        switch (version) {
            case V2003:
                return new HSSFWorkbook();
            case V2007:
                return new XSSFWorkbook();
        }
        return null;
    }

    private static void buildSheetData(Workbook wb, Sheet sheet, ExcelSheetPO excelSheetPO, ExcelVersion version, boolean isInsert) {
        sheet.setDefaultRowHeight((short) 400);
        sheet.setDefaultColumnWidth((short) 15);
        sheet.createFreezePane(0, 2, 0, 2); // 冻结前两行
        createTitle(sheet, excelSheetPO, wb, version);
        createHeader(sheet, excelSheetPO, wb, version);
        createBody(sheet, excelSheetPO, wb, version, isInsert);
        String[] saveList = {"按天", "按周"};
        String[] protocolList = {"FTP", "SFTP"};
        Map<String, String[]> paraMap = excelSheetPO.getParaMap();
        int endRow = 0;
        if (isInsert) {
            endRow = 500;
        } else {
            endRow = excelSheetPO.getDataList().size() + 1;
        }
        // 设置下拉菜单
        if (paraMap.get("areas") != null) {
            setHSSFValidation(sheet, paraMap.get("areas"), 2, endRow, 0, 0); // 所属地区
        }
        if (paraMap.get("deviceTypes") != null) {
            setHSSFValidation(sheet, paraMap.get("deviceTypes"), 2, endRow, 2, 2); // 网元类型
        }
        if (paraMap.get("firms") != null) {
            setHSSFValidation(sheet, paraMap.get("firms"), 2, endRow, 3, 3); // 所属厂家
        }
        setHSSFValidation(sheet, saveList, 2, endRow, 4, 4); // 保存类型
        if (excelSheetPO.getSheetName().equals("被动取")) {
            setHSSFValidation(sheet, protocolList, 2, endRow, 8, 8); // 备份协议
        }
    }

    private static void createBody(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version, boolean isInsert) {
        List<List<Object>> dataList = excelSheetPO.getDataList();
        for (int i = 0; i < dataList.size() && i < version.getMaxRow(); i++) {
            List<Object> values = dataList.get(i);
            Row row = sheet.createRow(2 + i);
            for (int j = 0; j < values.size() && j < version.getMaxColumn(); j++) {
                Cell cell = row.createCell(j);
                // 新增模板的示例数据置灰
                if (isInsert && i >= 0 && i < 2) {
                    cell.setCellStyle(getStyle(STYLE_DATA1, wb));
                }
                // 修改模板的Id字段置灰
                else if (!isInsert && (("被动取".equals(excelSheetPO.getSheetName()) && j > 13) || ("主动推".equals(excelSheetPO.getSheetName()) && j > 8))) {
                    cell.setCellStyle(getStyle(STYLE_DATA1, wb));
                } else {
                    cell.setCellStyle(getStyle(STYLE_DATA, wb));
                }
                cell.setCellValue(values.get(j) != null ? values.get(j).toString() : "");
            }
        }
        // 新增模板默认设置500行单元格样式，修改模板按数据量设置单元格样式
        if (isInsert) {
            int tmpNum = 500;
            for (int k = 2 + dataList.size(); k < 2 + dataList.size() + tmpNum; k++) {
                Row row2 = sheet.createRow(k);
                for (int m = 0; m < 13; m++) {
                    Cell cell2 = row2.createCell(m);
                    cell2.setCellStyle(getStyle(STYLE_DATA, wb));
                }
            }
        }
    }

    private static void createHeader(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        String[] headers = excelSheetPO.getHeaders();
        Row row = sheet.createRow(1);
        if (excelSheetPO.getSheetName().equals("被动取")) {
            sheet.setColumnWidth(13, 8000);
            for (int i = 0; i < headers.length && i < version.getMaxColumn(); i++) {
                Cell cellHeader = row.createCell(i);
                if (i > 13) {
                    cellHeader.setCellStyle(getStyle(STYLE_HEADER2, wb));
                } else if (i > 6) {
                    cellHeader.setCellStyle(getStyle(STYLE_HEADER1, wb));
                } else {
                    cellHeader.setCellStyle(getStyle(STYLE_HEADER, wb));
                }
                cellHeader.setCellValue(headers[i]);
            }
        } else if (excelSheetPO.getSheetName().equals("主动推")) {
            sheet.setColumnWidth(7, 15000);
            sheet.setColumnWidth(8, 15000);
            for (int i = 0; i < headers.length && i < version.getMaxColumn(); i++) {
                Cell cellHeader = row.createCell(i);
                if (i > 8) {
                    cellHeader.setCellStyle(getStyle(STYLE_HEADER2, wb));
                } else {
                    cellHeader.setCellStyle(getStyle(STYLE_HEADER, wb));
                }
                cellHeader.setCellValue(headers[i]);
            }
        } else {
            for (int i = 0; i < headers.length && i < version.getMaxColumn(); i++) {
                Cell cellHeader = row.createCell(i);
                cellHeader.setCellStyle(getStyle(STYLE_HEADER, wb));
                cellHeader.setCellValue(headers[i]);
            }
        }
    }

    private static void createTitle(Sheet sheet, ExcelSheetPO excelSheetPO, Workbook wb, ExcelVersion version) {
        Row titleRow = sheet.createRow(0);
        titleRow.setHeight((short) 800);
        Cell titleCel = titleRow.createCell(0);
        titleCel.setCellValue(excelSheetPO.getTitle());
        titleCel.setCellStyle(getStyle(STYLE_TITLE, wb));
        // 限制最大列数
        int column = excelSheetPO.getHeaders().length > version.getMaxColumn() ? version.getMaxColumn()
                : excelSheetPO.getHeaders().length;
        CellRangeAddress cra = new CellRangeAddress(0, 0, 0, column - 1);
        sheet.addMergedRegion(cra);
        // 使用RegionUtil类为合并后的单元格添加边框
        RegionUtil.setBorderBottom(1, cra, sheet); // 下边框
        RegionUtil.setBorderLeft(1, cra, sheet); // 左边框
        RegionUtil.setBorderRight(1, cra, sheet); // 有边框
        RegionUtil.setBorderTop(1, cra, sheet); // 上边框
    }

    private static CellStyle getStyle(String type, Workbook wb) {
        if (cellStyleMap.containsKey(type)) {
            return cellStyleMap.get(type);
        }
        // 生成一个样式
        CellStyle style = wb.createCellStyle();
        if (!(STYLE_DATA.equals(type) || STYLE_DATA1.equals(type))) {
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setWrapText(true);
        }

        if (STYLE_HEADER.equals(type)) {
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 16);
            font.setFontName("黑体");
            style.setFont(font);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.PALE_BLUE.index);
        } else if (STYLE_HEADER1.equals(type)) {
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 16);
            font.setFontName("黑体");
            style.setFont(font);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.LIME.index);
        } else if (STYLE_HEADER2.equals(type)) {
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 16);
            font.setFontName("黑体");
            style.setFont(font);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        } else if (STYLE_TITLE.equals(type)) {
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontName("仿宋");
            font.setFontHeightInPoints((short) 16);
            font.setItalic(true);
            font.setColor(IndexedColors.RED.index);
            style.setFont(font);
        } else if (STYLE_DATA.equals(type)) {
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            style.setFont(font);
        } else if (STYLE_DATA1.equals(type)) {
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = wb.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setColor(IndexedColors.GREY_25_PERCENT.index);
            style.setFont(font);
        }
        cellStyleMap.put(type, style);
        return style;
    }

    /**
     * 设置某些列的值只能输入预制的数据,显示下拉框（支持excel2003）
     *
     * @param sheet    要设置的sheet.
     * @param textlist 下拉框显示的内容
     * @param firstRow 开始行
     * @param endRow   结束行
     * @param firstCol 开始列
     * @param endCol   结束列
     * @return 设置好的sheet.
     */
    public static Sheet setHSSFValidation(Sheet sheet,
                                          String[] textlist, int firstRow, int endRow, int firstCol,
                                          int endCol) {
        // 加载下拉列表内容
        DVConstraint constraint = DVConstraint.createExplicitListConstraint(textlist);
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint);
        sheet.addValidationData(data_validation_list);
        return sheet;
    }

    /**
     * 设置单元格上提示（支持excel2003）
     *
     * @param sheet         要设置的sheet.
     * @param promptTitle   标题
     * @param promptContent 内容
     * @param firstRow      开始行
     * @param endRow        结束行
     * @param firstCol      开始列
     * @param endCol        结束列
     * @return 设置好的sheet.
     */
    public static HSSFSheet setHSSFPrompt(HSSFSheet sheet, String promptTitle,
                                          String promptContent, int firstRow, int endRow, int firstCol, int endCol) {
        // 构造constraint对象
        DVConstraint constraint = DVConstraint.createCustomFormulaConstraint("BB1");
        // 四个参数分别是：起始行、终止行、起始列、终止列
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
        // 数据有效性对象
        HSSFDataValidation data_validation_view = new HSSFDataValidation(regions, constraint);
        data_validation_view.createPromptBox(promptTitle, promptContent);
        sheet.addValidationData(data_validation_view);
        return sheet;
    }

    /**
     * 生成网元配置新增模板
     *
     * @param paraMap 下拉菜单字典值
     */
    public static String createNebakInsertTemplet(Map<String, String[]> paraMap) throws Exception {
        List<ExcelSheetPO> excelSheets = new ArrayList<>();
        // 被动取
        ExcelSheetPO excelSheetPO_get = new ExcelSheetPO();
        excelSheetPO_get.setSheetName("被动取");
        excelSheetPO_get.setTitle("被动取类型配置：蓝色区域为网元配置，绿色区域为模块配置；针对多模块网元，配置为多条数据，其中蓝色网元配置部分必须相同；\n第3-4行为多模块网元配置示例数据，请勿删除");
        excelSheetPO_get.setHeaders(new String[]{"所属地区", "设备名称", "网元类型", "所属厂家", "保存类型", "保存份数", "备注", "模块名称", "备份协议", "设备地址", "设备端口", "用户名", "密码", "备份路径"});
        excelSheetPO_get.setParaMap(paraMap);
        List<List<Object>> data_get = new ArrayList<>();
        List<Object> row0 = new ArrayList<>();
        row0.add("合肥");
        row0.add("HFGS1");
        row0.add("FW");
        row0.add("爱立信");
        row0.add("按天");
        row0.add("7");
        row0.add("AP1");
        row0.add("AP1A");
        row0.add("FTP");
        row0.add("10.225.130.19");
        row0.add("21");
        row0.add("test");
        row0.add("123");
        row0.add("/Images/nodeA");
        data_get.add(row0);
        List<Object> row1 = new ArrayList<>();
        row1.add("合肥");
        row1.add("HFGS1");
        row1.add("FW");
        row1.add("爱立信");
        row1.add("按天");
        row1.add("7");
        row1.add("AP1");
        row1.add("AP1B");
        row1.add("FTP");
        row1.add("10.225.130.19");
        row1.add("21");
        row1.add("test");
        row1.add("123");
        row1.add("/Images/nodeB");
        data_get.add(row1);
        excelSheetPO_get.setDataList(data_get);
        excelSheets.add(excelSheetPO_get);
        // 主动推
        ExcelSheetPO excelSheetPO_put = new ExcelSheetPO();
        excelSheetPO_put.setSheetName("主动推");
        excelSheetPO_put.setTitle("主动推类型配置：第3行为示例数据，请勿删除");
        excelSheetPO_put.setHeaders(new String[]{"所属地区", "设备名称", "网元类型", "所属厂家", "保存类型", "保存份数", "备注", "用户数据路径", "系统数据路径"});
        excelSheetPO_put.setParaMap(paraMap);
        List<List<Object>> data_put = new ArrayList<>();
        List<Object> row_put = new ArrayList<>();
        row_put.add("合肥");
        row_put.add("HFCG09BHW");
        row_put.add("HSS");
        row_put.add("爱立信");
        row_put.add("按周");
        row_put.add("7");
        row_put.add("HFCG09BHW系统数据");
        row_put.add("/zzyf/bak/HF_HSS/HFHSS06/UserData/81/db");
        row_put.add("/zzyf/bak/HF_HSS/HFHSS06/System/HFHSS06BE01/");
        data_put.add(row_put);
        excelSheetPO_put.setDataList(data_put);
        excelSheets.add(excelSheetPO_put);
        String tmpFilename = PropertyUtil.getStringValue("tmp.file.path") + File.separator + "insertTemplet(" + sdf.format(new Date()) + ").xls";
        createWorkbookAtDisk(ExcelVersion.V2003, excelSheets, tmpFilename, true);
        return tmpFilename;
    }

    /**
     * 生成网元配置修改模板
     *
     * @param paraMap       下拉菜单字典值
     * @param neServerPojos 待修改数据
     * @return
     */
    public static String createNebakUpdateTemplet(Map<String, String[]> paraMap, List<NeServerPojo> neServerPojos) throws Exception {
        List<ExcelSheetPO> excelSheets = new ArrayList<>();
        // 被动取
        ExcelSheetPO excelSheetPO_get = new ExcelSheetPO();
        excelSheetPO_get.setSheetName("被动取");
        excelSheetPO_get.setTitle("被动取类型配置：蓝色区域为网元配置，绿色区域为模块配置；针对多模块网元，配置为多条数据，其中蓝色网元配置部分必须相同；\n请勿修改灰色区域网元ID及模块ID");
        excelSheetPO_get.setHeaders(new String[]{"所属地区", "设备名称", "网元类型", "所属厂家", "保存类型", "保存份数", "备注", "模块名称", "备份协议", "设备地址", "设备端口", "用户名", "密码", "备份路径", "网元ID", "模块ID"});
        excelSheetPO_get.setParaMap(paraMap);
        List<List<Object>> data_get = new ArrayList<>();
        List<Object> row = null;
        NeServerPojo nsp = null;
        for (int i = 0; i < neServerPojos.size(); i++) {
            nsp = neServerPojos.get(i);
            if ("0".equals(nsp.getBakType())) {
                row = new ArrayList<>();
                row.add(nsp.getOrgName());
                row.add(nsp.getDeviceName());
                row.add(nsp.getDeviceType());
                row.add(nsp.getFirms());
                row.add(nsp.getSaveType());
                row.add(nsp.getSaveDay());
                row.add(nsp.getRemarks());
                row.add(nsp.getModuleName());
                row.add(nsp.getBakProtocol());
                row.add(nsp.getDeviceAddr());
                row.add(nsp.getDevicePort());
                row.add(nsp.getUserName());
                row.add(nsp.getPassWord());
                row.add(nsp.getBakPath());
                row.add(nsp.getServerId());
                row.add(nsp.getModuleId());
                data_get.add(row);
            }
        }
        excelSheetPO_get.setDataList(data_get);
        excelSheets.add(excelSheetPO_get);
        // 主动推
        ExcelSheetPO excelSheetPO_put = new ExcelSheetPO();
        excelSheetPO_put.setSheetName("主动推");
        excelSheetPO_put.setTitle("主动推类型配置：请勿修改灰色区域网元ID");
        excelSheetPO_put.setHeaders(new String[]{"所属地区", "设备名称", "网元类型", "所属厂家", "保存类型", "保存份数", "备注", "用户数据路径", "系统数据路径", "网元ID"});
        excelSheetPO_put.setParaMap(paraMap);
        List<List<Object>> data_put = new ArrayList<>();
        for (int j = 0; j < neServerPojos.size(); j++) {
            nsp = neServerPojos.get(j);
            if ("1".equals(nsp.getBakType())) {
                row = new ArrayList<>();
                row.add(nsp.getOrgName());
                row.add(nsp.getDeviceName());
                row.add(nsp.getDeviceType());
                row.add(nsp.getFirms());
                row.add(nsp.getSaveType());
                row.add(nsp.getSaveDay());
                row.add(nsp.getRemarks());
                row.add(nsp.getBakUserdata());
                row.add(nsp.getBakSystem());
                row.add(nsp.getServerId());
                data_put.add(row);
            }
        }
        excelSheetPO_put.setDataList(data_put);
        excelSheets.add(excelSheetPO_put);
        String tmpFilename = PropertyUtil.getStringValue("tmp.file.path") + File.separator + "updateTemplet(" + sdf.format(new Date()) + ").xls";
        createWorkbookAtDisk(ExcelVersion.V2003, excelSheets, tmpFilename, false);
        return tmpFilename;
    }

}