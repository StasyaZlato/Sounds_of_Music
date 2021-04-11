package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainMenuController {
    public ScrollPane scrollFiles;
    public ListView files;
    public Button infoTab;
    public Button tonnetzTab;
    public Button tdaTab;
    public Button scrollPaneEnableBtn;

    ObservableList<String> loadedFiles = FXCollections.observableArrayList();

    public void initialize() {
        buttonDesignChange(0);

        scrollPaneEnableBtn.setMaxHeight(Double.MAX_VALUE);

        files.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/general_statistics.fxml"));
            try {
                Parent root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            GeneralStatisticsController controller = loader.getController();
            controller.onFileSelected(newValue);
        });
    }

    public void setFilesList(ObservableList<String> filesLst) {
        loadedFiles = filesLst;
        files.setItems(loadedFiles);
        scrollFiles.setManaged(true);
    }

    public void openNextTab(MouseEvent mouseEvent) throws IOException {
        Button b = (Button) (mouseEvent.getSource());

        switch (b.getId()) {
            case "infoTab":
                buttonDesignChange(0);
//                MainLaunch.openSettings();
                break;
            case "tonnetzTab":
                buttonDesignChange(1);
//                CourseWorkMain.MainLaunch.openProcess();
                break;
            case "tdaTab":
                buttonDesignChange(2);
//                CourseWorkMain.MainLaunch.openUserInput();
                break;
        }
    }

    private void buttonDesignChange(int id) {
        Button[] buttons = {infoTab, tonnetzTab, tdaTab};
        VBox.setVgrow(buttons[id], Priority.ALWAYS);
        buttons[id].setMaxHeight(Double.MAX_VALUE);
        buttons[id].getStyleClass().clear();
        buttons[id].getStyleClass().add("active");
        buttons[id].getStyleClass().add("button");

        for (int i = 0; i < 3; i++) {
            if (i != id) {
                VBox.setVgrow(buttons[i], Priority.NEVER);
                buttons[i].getStyleClass().clear();
                buttons[i].getStyleClass().add("passive");
                buttons[i].getStyleClass().add("button");
            }
        }
    }

    public void manageListView(MouseEvent e) {
        ImageView img;
        if (scrollFiles.isManaged()) {
            img = new ImageView("/images/left-arrow-white.png");
        }
        else {
            img = new ImageView("/images/right-arrow-white.png");
        }
        img.setFitWidth(10);
        img.setPickOnBounds(true);
        img.setPreserveRatio(true);

        scrollPaneEnableBtn.setGraphic(img);

        scrollFiles.setManaged(!scrollFiles.isManaged());
    }
}
