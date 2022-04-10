package com.grv.batch.tasklet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class Step3Tasklet implements Tasklet {
	private Workbook workbook;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		//task logic here
		System.out.println("Step3Tasklet called...");
		File file = new File("test.xlsx");
		if(!file.exists()) {
			file.createNewFile();
		}
		
		InputStream fis = new FileInputStream(file);
		workbook = new XSSFWorkbook();
		
		Sheet sheet = workbook.createSheet("Sheet_1");
		
		Row row1 = sheet.createRow(0);
		row1.createCell(0).setCellValue("Ram");
		row1.createCell(1).setCellValue("25");
		row1.createCell(2).setCellValue("25000");
		
		Row row2 = sheet.createRow(1);
		row2.createCell(0).setCellValue("Shyam");
		row2.createCell(1).setCellValue("28");
		row2.createCell(2).setCellValue("30000");
		
		fis.close();
		
		FileOutputStream fos = new FileOutputStream(file);
		workbook.write(fos);
		fos.close();
		
		
		return RepeatStatus.FINISHED;
	}

}
