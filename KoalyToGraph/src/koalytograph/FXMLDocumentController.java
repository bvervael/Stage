/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koalytograph;

import java.awt.Checkbox;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author cmaricou
 */
public class FXMLDocumentController implements Initializable {
    
    private static final ObservableList list = FXCollections.observableArrayList();
    private static String[][] importData;
    private LocalDate[] datum;
    private Map<LocalDate,Integer> months;
    private double width=854,height=456;
    
    @FXML
    private Button button;
    
    @FXML
    private TextField textField;
    
    @FXML
    private ListView listView;
    
    @FXML
    private LineChart graph;
    
    @FXML
    private CheckBox checkbox;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        String word=textField.getText().toLowerCase();
        textField.setText("");
        listView.setItems(list);
        wordCount(word,checkbox.isSelected());
    }
    
    public void wordCount(String word, Boolean exclusive){
        int counter=0;
        XYChart.Series series = new XYChart.Series();
        series.setName(word);
        
        months = new TreeMap<>();
        for(int i = 24; i <importData[0].length; i++){
            if(importData[7][i]!=null&&datum[i]!=null){
                if(importData[7][i].toLowerCase().contains(word)){
                    if(months.containsKey(datum[i])){
                        months.put(datum[i],months.get(datum[i])+1);
                        counter ++;
                    } else {
                        months.put(datum[i],1);
                        counter ++;
                    }
                }
            }
        }
        if (!months.isEmpty()) {
           list.add(word);
            months.keySet().forEach((it) -> {
                //System.out.println(it.toString() + ": " + months.get(it));
                series.getData().add(new XYChart.Data(it.toString().substring(0, 7), months.get(it)));
            });

            graph.getData().add(series);
        }
        System.out.println(word + " komt in " + counter + " titels voor.");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        int rowNum=0,colNum=0;
        try ( //Create Workbook from Existing File
            InputStream fileIn = new FileInputStream("C:\\tmp\\ticket.xls")) {
            Workbook wb = WorkbookFactory.create(fileIn);
            Sheet sheet = wb.getSheetAt(0);
            
            importData = new String[100][sheet.getLastRowNum()+1];
            Cell cell; 
            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                rowNum ++;
                colNum=0;
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
                    colNum ++;
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            //System.out.print(cell.getStringCellValue());
                            importData[colNum][rowNum]= cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            //System.out.print(cell.getBooleanCellValue());
                            importData[colNum][rowNum]= cell.getBooleanCellValue() ? "true" : "false";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            //System.out.print(cell.getNumericCellValue());
                            importData[colNum][rowNum]= ""+ cell.getNumericCellValue();
                            break;
                    }
                    //System.out.print(" - ");
                }
                //System.out.println();
            }
            fileIn.close();
        } catch (Exception ex) {
            System.out.println("FOUT: " + ex);
        } 
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        datum =new LocalDate[importData[0].length];
        for(int u=24;u<importData[0].length;u++){
            if(importData[13][u]!=null){
                LocalDate date = LocalDate.parse(importData[13][u].substring(0,7)+"-01",format);
                datum[u]=date;
            }else{
                datum[u]=null;
            }
        }
    }  
}
