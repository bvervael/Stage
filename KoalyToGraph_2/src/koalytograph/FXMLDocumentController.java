/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koalytograph;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author cmaricou
 */
public class FXMLDocumentController implements Initializable {

    private static final ObservableList list = FXCollections.observableArrayList();
    private static String[][] importData;
    private LocalDate[] datum;
    private Map<LocalDate, Integer> months;
    private Map<String, XYChart.Series> graphs = new HashMap<>();
    private List<LocalDate> allMonths = new ArrayList();
    private HashMap<String,ArrayList> groups = new HashMap<>();
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
    private File file;

    @FXML
    private Button button,del;

    @FXML
    private TextField textField;

    @FXML
    private ListView listView;

    @FXML
    private LineChart graph;

    @FXML
    private ChoiceBox choiceBox;

    private void b1Action(ActionEvent event) {
        String word = textField.getText().toLowerCase();
        textField.setText("");
        addWord(word,true);
    }
    
    private int addWord(String s,boolean b){
        listView.setItems(list);
        String[] toks = s.split(":");
        if(toks.length>1){
            ArrayList<String> items = new ArrayList<String>(Arrays.asList(toks[1].split(",")));
            groups.put(toks[0],items);
        }else{
            ArrayList<String> items = new ArrayList<>();
            items.add(toks[0]);
            groups.put(toks[0],items);
        }
        return wordCount(toks[0], b);
    }
    
    private void b2Action(ActionEvent event) {
        int selectedIdx = listView.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            String word = (String) listView.getSelectionModel().getSelectedItem();
            word = word.split("\\(")[0];
            word = word.substring(0, word.length()-1);
            listView.getItems().remove(selectedIdx);
            graph.getData().remove(graphs.get(word));
            graphs.remove(word);
        }
    }
    
    public FXMLDocumentController(File file){
        this.file = file;
    }

    public int wordCount(String word, Boolean addToList) {
        String w1 = word, w2;
        String s = (String) choiceBox.getValue();
        int counter = 0,totaal=0;
        int col=0;
        switch (s) {
            case "Title":
                col = 7;
                break;
            case "Terms":
                col = 8;
                break;
            case "Repl/Last exchange":
                col = 9;
                break;
        }
        if (word.isEmpty()) {
            w2 = "All tickets";
            w1 = "";
        } else {
            w2 = w1;
        }
        if (!graphs.containsKey(w2)) {
            XYChart.Series series = new XYChart.Series();
            months = new TreeMap<>();
            for (LocalDate date : allMonths) {
                months.put(date, 0);
            }
            ArrayList <String> words = groups.get(w1); 
            for (int i = 24; i < importData[0].length; i++) {
                if (importData[col][i] != null && datum[i] != null) {
                    totaal ++;
                    Boolean b=false;
                    if (w1 == "") {
                        b=true;
                    } else {
                        for (String woord : words) {
                            ArrayList<String> items = new ArrayList<String>(Arrays.asList(woord.split("&&")));
                            if(findAnd(importData[col][i].toLowerCase(),items)){
                                b=true;
                            }
                        }
                    }
                    if (b) {
                        months.put(datum[i], months.get(datum[i]) + 1);
                        counter++;
                    }
                }
            }
            LocalDate l = LocalDate.now();
            l = LocalDate.parse(l.toString().substring(0, 7) + "-01", format);
            months.remove(l);
            if (counter != 0) {
                int percent =(int) counter*100/totaal;
                if (!addToList) {
                    list.remove(w2);
                }
                list.add(w2+" ("+percent+"%)");
                months.keySet().forEach((it) -> {
                    series.getData().add(new XYChart.Data(it.toString().substring(0, 7), months.get(it)));
                });
                series.setName(w2);
                graph.getData().add(series);
                graphs.put(w2, series);
            }
            System.out.println(w1 + " komt in " + counter + " titels voor.");
        } else {
            System.out.println("staat al in grafiek");
        }
        return counter;
    }
    
    public Boolean findAnd(String zin, ArrayList list){
        Boolean returnVal = true;
        if(list.isEmpty()){
            return true;
        }else{
            Boolean wordIn=true;
            String w = (String) list.get(0);
            if (w.charAt(0) == '-') {
                w = w.substring(1, w.length());
                wordIn = false;
            }
            if(zin.contains(w)==wordIn){
                list.remove(0);
                returnVal=findAnd(zin,list);
            }else{
                return false;
            }
        }
        return returnVal;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choiceBox.setItems(FXCollections.observableArrayList("Title", "Terms", "Repl/Last exchange"));
        //Make cell's editable
        choiceBox.getSelectionModel().selectFirst();
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> t) {
                String oldWord = (String) listView.getItems().set(t.getIndex(), t.getNewValue().toLowerCase());
                String word = oldWord.split("\\(")[0];
                word = word.substring(0, word.length()-1);
                String newWord = (String) listView.getItems().set(t.getIndex(), t.getNewValue().toLowerCase());
                if (graphs.containsKey(newWord)) {
                    listView.getItems().set(t.getIndex(), oldWord);
                } else {
                    if(addWord(newWord, false)>0){
                        graph.getData().remove(graphs.get(word));
                        graphs.remove(word);
                    }else{
                        listView.getItems().set(t.getIndex(), oldWord);
                    }
                }
            }

        });
        //Read in the full excel file to matrix
        int rowNum = 0, colNum = 0;
        try ( 
            FileInputStream fileIn = new FileInputStream(file)) {
            XSSFWorkbook wb = new XSSFWorkbook (fileIn);
            XSSFSheet sheet = wb.getSheetAt(0);

            importData = new String[100][sheet.getLastRowNum() + 1];
            Cell cell;
            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                rowNum++;
                colNum = 0;
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
                    colNum++;
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            importData[colNum][rowNum] = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            importData[colNum][rowNum] = cell.getBooleanCellValue() ? "true" : "false";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            importData[colNum][rowNum] = "" + cell.getNumericCellValue();
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
        
        datum = new LocalDate[importData[0].length];
        for (int u = 24; u < importData[0].length; u++) {
            if (importData[13][u] != null) {
                LocalDate date = LocalDate.parse(importData[13][u].substring(0, 7) + "-01", format);
                datum[u] = date;
                if (!allMonths.contains(date)) {
                    allMonths.add(date);
                }
            } else {
                datum[u] = null;
            }
        }
        
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                b1Action(event);
            }
        });
        
        del.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                b2Action(event);
            }
        });
    }
}
