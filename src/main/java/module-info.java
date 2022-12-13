module com.example.cis296project4 {
	requires javafx.controls;
	requires javafx.fxml;


	opens com.example.cis296project4 to javafx.fxml;
	exports com.example.cis296project4;
}