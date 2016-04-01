package com.cbd5.resource.yjkg;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import jxl.write.Number;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by CBD5 on 3/23/16.
 */
public class GenerateExcel {

    @Deprecated
    private void usage() {
        try {
            //创建一个Excel文件
            WritableWorkbook book = Workbook.createWorkbook(new File("/Users/CBD5/Downloads/Test.xls"));

            //创建Excel中的页面，设置页面名称，页面号由0开始，页面会按页面号从小到大的顺序在Excel中从左向右排列
            WritableSheet sheet1 = book.createSheet("Sheet1", 0);
            //设置要合并单元格的下标(start col,start row,into col,into row)
            sheet1.mergeCells(0, 0, 1, 1);
            //作用是指定第i+1行的高度，比如将第一行的高度设为200
            sheet1.setRowView(1, 2000);
            //作用是指定第i+1列的宽度，比如将第一列的宽度设为30
            //sheet1.setColumnView(1, 160);
            WritableSheet sheet2 = book.createSheet("Sheet_2", 1);

            //设置单元格的样式
            WritableCellFormat cellFormat = new WritableCellFormat();
            //设置水平居中
            cellFormat.setAlignment(jxl.format.Alignment.CENTRE);
            //设置垂直居中
            cellFormat.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            //设置自动换行
            cellFormat.setWrap(true);
            //设置显示的字体样式，字体，字号，是否粗体，字体颜色
            cellFormat.setFont(new WritableFont(WritableFont.createFont("楷体_GB2312"), 12, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.RED));
            //设置单元格背景色
            cellFormat.setBackground(jxl.format.Colour.BRIGHT_GREEN);

            //创建一个单元格，并按行列坐标进行指定的内容写入 ，最后加入显示的样式
            Label label = new Label(0, 0, "Excel", cellFormat);

            Label labe2 = new Label(0, 0, "test22222222");

            //插入图片
//            File file=new File("D:\\My Documents\\My Pictures\\test.png");
            //设置图片位置，前两个参数为插入图片的单元格坐标，后面是设置从插入的单元格开始横向和纵向分别要占用的单元格个数，最后传入图片文件
//            WritableImage image=new WritableImage(6, 0, 3, 3,file);

            //将行列的值写入页面
//            sheet1.addCell(label);
            //将图片插入页面
//            sheet1.addImage(image);
            sheet2.addCell(labe2);

            //创建数字类型的行列值
            Number number1 = new Number(4, 0, 789.123);
            Number number2 = new Number(4, 0, 789.12345678910);

            //将数字类型的行列值插入指定的页面
            sheet1.addCell(number1);
            sheet2.addCell(number2);

            //创建日期类型数据，并添加
            jxl.write.DateTime dateTime = new DateTime(5, 0, new Date());
            sheet1.addCell(dateTime);

            //开始执行写入操作
            book.write();
            //关闭流
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generate(JSONArray arr, JSONObject total, String time) {
        String webpath = getClass().getResource("/").getPath().replaceAll("/WEB-INF/classes/", "") + "/export/";

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = new GregorianCalendar();

        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime();
        time = format.format(date);

        String filepath = webpath + "yjcc" + time + ".xls";
        File f = new File(filepath);
        if (f.exists()) {
            return;
        }
        try {
            Element root = readProperties();

            WritableWorkbook book = Workbook.createWorkbook(f);
            String sheet_name = root.element("sheet").getText();
            WritableSheet sheet1 = book.createSheet(sheet_name, 0);

            int col_num = Integer.parseInt(root.element("col").getText());
            //Title
            sheet1.mergeCells(0, 0, col_num, 0);
            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setAlignment(jxl.format.Alignment.CENTRE);
            cellFormat.setFont(new WritableFont(WritableFont.createFont("宋体"), 10, WritableFont.NO_BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, Colour.BLACK));

            String title = root.element("title").getText();
            Label label = new Label(0, 0, title, cellFormat);
            sheet1.addCell(label);

            int[] coord = {0, 1, 2, 5, 8, 11, 14, 17, 20, 23, 26, 29};
            //Merge Cells
            sheet1.mergeCells(0, 1, 0, 2);
            sheet1.mergeCells(1, 1, 0, 2);
            int l = 1;
            for (int k = 2; k < coord.length; k++) {
                sheet1.mergeCells(coord[k], 1, l + 3, 0);
                l += 3;
            }
            //Caption
            Element ca = root.element("caption_0");
            List<Element> childElements = ca.elements();

            int i = 0;
            for (Element child : childElements) {
                String tmp_cell = child.getText();
                Label row2 = new Label(coord[i], 1, tmp_cell, cellFormat);
                sheet1.addCell(row2);
                i++;
            }
            Element ca1 = root.element("caption_1");
            List<Element> p2ce = ca1.elements();
            for (int j = 2; j < 32; j++) {
                String cell_1 = p2ce.get(0).getText();
                Label row3 = new Label(j, 2, cell_1, cellFormat);
                sheet1.addCell(row3);
                String cell_2 = p2ce.get(1).getText();
                Label row3_1 = new Label(j + 1, 2, cell_2, cellFormat);
                sheet1.addCell(row3_1);
                String cell_3 = p2ce.get(2).getText();
                Label row3_2 = new Label(j + 2, 2, cell_3, cellFormat);
                sheet1.addCell(row3_2);
                j += 2;
            }
            //Data
            int n = 3;
            for (int g = 0; g < arr.size(); g++) {
                JSONObject data = (JSONObject) arr.get(g);
                Iterator<?> obj = data.keys();
                int m = 0;
                while (obj.hasNext()) {// 遍历JSONObject
                    String key = (String) obj.next().toString();
                    String cell_val = data.getString(key);
                    Label row4 = new Label(m, n, cell_val);
                    sheet1.addCell(row4);
                    m++;
                }
                n++;
            }

            //Total
            sheet1.mergeCells(0, 0, 2, 0);
            Label row5_h = new Label(0, n + 1, "合计");
            sheet1.addCell(row5_h);

            Iterator<?> iter = total.keys();
            int m = 2;
            while (iter.hasNext()) {
                String key = (String) iter.next().toString();
                String cell_val = total.getString(key);
                Label row5 = new Label(m, n + 1, cell_val);
                sheet1.addCell(row5);
                m++;
            }

            book.write();
            book.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element readProperties() {
        Element root = null;
        try {
            SAXReader reader = new SAXReader();
            String path = getClass().getResource("").getPath();
            Document document = reader.read(new File(path + "/yjcc.xml"));
            root = document.getRootElement();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return root;
    }

    public static void main(String[] args) {
        GenerateExcel ge = new GenerateExcel();
        System.out.println(ge.getClass().getResource("/").getPath().replaceAll("/WEB-INF/classes/", ""));

//        ge.generate();
    }
}
