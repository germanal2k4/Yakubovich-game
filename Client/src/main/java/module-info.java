module com.example.hw3_albershteyn {
    requires javafx.controls;
    requires javafx.fxml;



    opens com.example.hw3_albershteyn to javafx.fxml;
    exports com.example.hw3_albershteyn;
}