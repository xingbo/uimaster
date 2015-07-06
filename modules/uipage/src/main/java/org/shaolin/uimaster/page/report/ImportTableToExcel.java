package org.shaolin.uimaster.page.report;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportTableToExcel {

	private final List<Object[]> rows;
	
	public ImportTableToExcel(List<Object[]> rows) {
		this.rows = rows;
	}
	
	public Workbook createWorkbook(String name, String[] columnTitles) {
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();

	    // Note that sheet name is Excel must not exceed 31 characters
	    // and must not contain any of the any of the following characters:
	    // 0x0000
	    // 0x0003
	    // colon (:)
	    // backslash (\)
	    // asterisk (*)
	    // question mark (?)
	    // forward slash (/)
	    // opening square bracket ([)
	    // closing square bracket (])
		String safeName = WorkbookUtil.createSafeSheetName(name);
		Sheet sheet = wb.createSheet(safeName);
		int j = 0;
		Row titleRow = sheet.createRow(0);
		titleRow.setRowStyle(wb.createCellStyle());
		titleRow.getRowStyle().setBorderBottom(CellStyle.BORDER_DOUBLE);
		titleRow.getRowStyle().setBorderBottom(CellStyle.BORDER_DOUBLE);
		for (String t : columnTitles) {
			Cell cell = titleRow.createCell(j++);
			cell.setCellValue(createHelper.createRichTextString(t));
		}
		
		j = 1;
		for (Object[] row : rows) {
			Row srow = sheet.createRow(j++);
			int i = 0;
			for (Object cells : row) {
				Cell cell = srow.createCell(i++);
			    cell.setCellValue(createHelper.createRichTextString(cells.toString()));
			}
		}
		
		return wb;
	}
}
