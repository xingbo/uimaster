<%@ page import="java.util.*, org.apache.poi.hssf.usermodel.*, org.apache.poi.hssf.util.HSSFColor,bmiasia.ebos.security.IUserContext,bmiasia.ebos.webflow.WebflowConstants"%><%
    String TITLE_BUNDLE_FILE = "Bundle file";
    String TITLE_BUNDLE_KEY = "Bundle key";
    String TITLE_LOCALE = "Locale";
    String TITLE_ORIGINAL_VALUE = "Original value";
    String TITLE_TRANSLATED_VALUE = "Translated value";
    String pageName = request.getParameter("page");
    
    request.setCharacterEncoding("UTF-8");
    OutputStream output = response.getOutputStream();
    response.setContentType("application/x-download");    
    response.addHeader("Content-Disposition","attachment;filename=" + pageName + ".xls");
    

    HSSFWorkbook book = new HSSFWorkbook();
    HSSFSheet sheet = book.createSheet();
    
    // start add header
    HSSFCellStyle titleCellStyle = book.createCellStyle();
    titleCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
    titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    titleCellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
    titleCellStyle.setRightBorderColor(HSSFColor.BLACK.index);
    titleCellStyle.setTopBorderColor(HSSFColor.BLACK.index);
    titleCellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
    titleCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    titleCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
    titleCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
    titleCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);

    HSSFRow titleRow = sheet.createRow(0);
    HSSFCell fileTitleCell = titleRow.createCell((short)0);
    fileTitleCell.setCellType(HSSFCell.CELL_TYPE_STRING);
    fileTitleCell.setCellStyle(titleCellStyle);
    fileTitleCell.setCellValue(TITLE_BUNDLE_FILE);
    
    HSSFCell keyTitleCell = titleRow.createCell((short)1);
    keyTitleCell.setCellType(HSSFCell.CELL_TYPE_STRING);
    keyTitleCell.setCellStyle(titleCellStyle);
    keyTitleCell.setCellValue(TITLE_BUNDLE_KEY);

    HSSFCell localeTitleCell = titleRow.createCell((short)2);
    localeTitleCell.setCellType(HSSFCell.CELL_TYPE_STRING);
    localeTitleCell.setCellStyle(titleCellStyle);
    localeTitleCell.setCellValue(TITLE_LOCALE);

    HSSFCell originalTitleCell = titleRow.createCell((short)3);
    originalTitleCell.setCellType(HSSFCell.CELL_TYPE_STRING);
    originalTitleCell.setCellStyle(titleCellStyle);
    originalTitleCell.setCellValue(TITLE_ORIGINAL_VALUE);

    HSSFCell translatedTitleCell = titleRow.createCell((short)4);
    translatedTitleCell.setCellType(HSSFCell.CELL_TYPE_STRING);
    translatedTitleCell.setCellStyle(titleCellStyle);
    translatedTitleCell.setCellValue(TITLE_TRANSLATED_VALUE);
    // end add header

    HSSFCellStyle rowCellStyle = book.createCellStyle();
    rowCellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
    rowCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    rowCellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
    rowCellStyle.setRightBorderColor(HSSFColor.BLACK.index);
    rowCellStyle.setTopBorderColor(HSSFColor.BLACK.index);
    rowCellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
    rowCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    rowCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
    rowCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
    rowCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    
    IUserContext uc = (IUserContext) request.getSession().getAttribute(WebflowConstants.USER_SESSION_KEY);
    if (uc != null)
    {
        Map resourceMap = (Map) uc.getAttribute(WebflowConstants.KEY_RESOURCE_MAP);
        if (resourceMap != null)
        {
            List pageResList = (List) resourceMap.get(pageName);
            if (pageResList != null)
            {
                for (int i = 0; i < pageResList.size(); i++)
                {
                    ArrayList resourceInfo = (ArrayList) pageResList.get(i);
                    HSSFRow row = sheet.createRow(i + 1);
                    HSSFCell fileCell = row.createCell((short)0);
                    fileCell.setEncoding(HSSFCell.ENCODING_UTF_16);   
                    fileCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    fileCell.setCellStyle(rowCellStyle);
                    fileCell.setCellValue(String.valueOf(resourceInfo.get(0)).replace('.', '/'));

                    HSSFCell keyCell = row.createCell((short)1);
                    keyCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    keyCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    keyCell.setCellStyle(rowCellStyle);
                    keyCell.setCellValue(String.valueOf(resourceInfo.get(1)));

                    HSSFCell localeCell = row.createCell((short)2);
                    localeCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    localeCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    localeCell.setCellStyle(rowCellStyle);
                    localeCell.setCellValue(String.valueOf(resourceInfo.get(2)));
                    
                    HSSFCell originalCell = row.createCell((short)3);
                    originalCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    originalCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    originalCell.setCellStyle(rowCellStyle);
                    originalCell.setCellValue(String.valueOf(resourceInfo.get(3)));

                    HSSFCell translatedCell = row.createCell((short)4);
                    translatedCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    translatedCell.setCellStyle(rowCellStyle);
                    
                    if (resourceInfo.size() == 4)
                    {
                        translatedCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
                    }
                    else
                    {
                        translatedCell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        translatedCell.setCellValue(String.valueOf(resourceInfo.get(4)));
                    }
                }
            }
        }
    }
    book.write(output);
    output.close();
%>