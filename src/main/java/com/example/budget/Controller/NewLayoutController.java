package com.example.budget.Controller;

import javafx.event.ActionEvent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class NewLayoutController {
    public DatePicker datePicker;
    public TextField descriptionField;
    public ComboBox categoryComboBox;
    public TextField amountField;
    public Text reservationCount1;
    public BarChart barChart;
    public PieChart pieChart;

    public void handleAddExpense(ActionEvent actionEvent) {
    }
}
