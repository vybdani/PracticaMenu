module ni.edu.uam.menu {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.menu to javafx.fxml;
    exports ni.edu.uam.menu;
}