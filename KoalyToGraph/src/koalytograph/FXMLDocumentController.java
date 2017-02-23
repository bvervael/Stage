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
    private Map<LocalDate, Integer> months;
    private Map<String, XYChart.Series> graphs = new HashMap<>();
    private List<LocalDate> allMonths = new ArrayList();

    @FXML
    private Button button;

    @FXML
    private TextField textField;

    @FXML
    private ListView listView;

    @FXML
    private LineChart graph;

    @FXML
    private ChoiceBox choiceBox;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        String word = textField.getText().toLowerCase();
        textField.setText("");
        listView.setItems(list);
        wordCount(word, true);
    }

    @FXML
    private void actionDelete(ActionEvent event) {
        int selectedIdx = listView.getSelectionModel().getSelectedIndex();
        if (selectedIdx != -1) {
            String word = (String) listView.getSelectionModel().getSelectedItem();
            listView.getItems().remove(selectedIdx);
            graph.getData().remove(graphs.get(word));
        }
    }

    public void wordCount(String word, Boolean addToList) {
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
        } else {
            w2 = w1;
        }
        if (!graphs.containsKey(w2)) {
            XYChart.Series series = new XYChart.Series();
            months = new TreeMap<>();
            for (LocalDate date : allMonths) {
                months.put(date, 0);
            }
            for (int i = 24; i < importData[0].length; i++) {
                if (importData[col][i] != null && datum[i] != null) {
                    totaal ++;
                    if (importData[col][i].toLowerCase().contains(w1)) {
                        //if(months.containsKey(datum[i])){
                        months.put(datum[i], months.get(datum[i]) + 1);
                        counter++;
                    }
                }
            }
            if (counter != 0) {
                if (addToList) {
                    System.out.println(totaal);
                    int percent =(int) counter*100/totaal;
                    list.add(w2+" ("+percent+"%)");
                }
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
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choiceBox.setItems(FXCollections.observableArrayList("Title", "Terms", "Repl/Last exchange"));
        choiceBox.getSelectionModel().selectFirst();
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());
        listView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> t) {
                String word = (String) listView.getItems().set(t.getIndex(), t.getNewValue().toLowerCase());
                if (graphs.containsKey(listView.getItems().set(t.getIndex(), t.getNewValue()))) {
                    listView.getItems().set(t.getIndex(), word);
                } else {
                    graph.getData().remove(graphs.get(word));
                    graphs.remove(word);
                    wordCount((String) listView.getItems().set(t.getIndex(), t.getNewValue().toLowerCase()), false);
                }
            }

        });
        int rowNum = 0, colNum = 0;
        try ( //Create Workbook from Existing File
                InputStream fileIn = new FileInputStream("C:\\tmp\\ticket.xls")) {
            Workbook wb = WorkbookFactory.create(fileIn);
            Sheet sheet = wb.getSheetAt(0);

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
                            //System.out.print(cell.getStringCellValue());
                            importData[colNum][rowNum] = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_BOOLEAN:
                            //System.out.print(cell.getBooleanCellValue());
                            importData[colNum][rowNum] = cell.getBooleanCellValue() ? "true" : "false";
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            //System.out.print(cell.getNumericCellValue());
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
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
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
    }
}
