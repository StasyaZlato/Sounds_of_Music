package controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.TableModel;

public class GeneralStatisticsController {
    @FXML TableColumn<TableModel, String> chordColumn;
    @FXML TableColumn<TableModel, Number> frequencyColumn;
    @FXML TableView<TableModel> frequencyTable;
    ObservableList<TableModel> rows = FXCollections.observableArrayList();

    public void initialize() {
        frequencyTable.setEditable(true);
        //ObservableList<TableColumn<TableModel, ?>> columns = frequencyTable.getColumns();
        chordColumn.prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));
        frequencyColumn.prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));

        chordColumn.setResizable(false);
        frequencyColumn.setResizable(false);

        chordColumn.setCellValueFactory(c -> new SimpleStringProperty("A"));
        frequencyColumn.setCellValueFactory(c -> new SimpleDoubleProperty(0));

        //columns.get(0).setCellValueFactory(new PropertyValueFactory<TableModel, String>("chord"));

        frequencyTable.setItems(rows);
    }

    public void onFileSelected(Object selectedFile) {
        System.out.println(selectedFile);
        TableModel element = new TableModel(selectedFile.toString(), 0);
        rows.add(element);
       // frequencyTable.getItems().add(element);
       // frequencyTable.setItems(rows);
    }
}
