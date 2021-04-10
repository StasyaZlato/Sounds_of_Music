package controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class GeneralStatisticsController {
    @FXML TableView frequencyTable;

    public void initialize() {
        frequencyTable.columnResizePolicyProperty();
        ObservableList<TableColumn> columns = frequencyTable.getColumns();
        columns.get(0).prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));
        columns.get(1).prefWidthProperty().bind(frequencyTable.widthProperty().multiply(0.5));

        columns.get(0).setResizable(false);
        columns.get(1).setResizable(false);
    }
}
