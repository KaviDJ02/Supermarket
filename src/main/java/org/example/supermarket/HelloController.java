package org.example.supermarket;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {
    @FXML
    private TextField itemCodeField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField packSizeField;
    @FXML
    private TextField unitPriceField;
    @FXML
    private TextField qtyOnHandField;
    @FXML
    private TableView<Item> tableView;
    @FXML
    private TableColumn<Item, String> column1;
    @FXML
    private TableColumn<Item, String> column2;
    @FXML
    private TableColumn<Item, String> column3;
    @FXML
    private TableColumn<Item, String> column4;
    @FXML
    private TableColumn<Item, String> column5;
    @FXML
    private Button deleteButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button saveButton;

    private ObservableList<Item> itemList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        column1.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));
        column3.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        column4.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        column5.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        tableView.setItems(itemList);
        loadItemsFromDatabase();
    }

    @FXML
    protected void onSaveButtonClick() {
        String itemCode = itemCodeField.getText();
        String description = descriptionField.getText();
        String packSize = packSizeField.getText();
        String unitPrice = unitPriceField.getText();
        String qtyOnHand = qtyOnHandField.getText();

        Item newItem = new Item(itemCode, description, packSize, unitPrice, qtyOnHand);
        itemList.add(newItem);
        saveItemToDatabase(newItem);
        clearFields();
    }

    @FXML
    protected void onUpdateButtonClick() {
        Item selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (itemCodeField.getText().isEmpty() || descriptionField.getText().isEmpty() || packSizeField.getText().isEmpty() || unitPriceField.getText().isEmpty() || qtyOnHandField.getText().isEmpty()) {
                showAlert("Error", "All fields must be filled out to update an item.");
                return;
            }

            selectedItem.setItemCode(itemCodeField.getText());
            selectedItem.setDescription(descriptionField.getText());
            selectedItem.setPackSize(packSizeField.getText());
            selectedItem.setUnitPrice(unitPriceField.getText());
            selectedItem.setQtyOnHand(qtyOnHandField.getText());

            updateItemInDatabase(selectedItem);
            tableView.refresh();
            clearFields();
        }
    }

    @FXML
    protected void onDeleteButtonClick() {
        Item selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            itemList.remove(selectedItem);
            deleteItemFromDatabase(selectedItem);
            clearFields();
        }
    }

    private void clearFields() {
        itemCodeField.clear();
        descriptionField.clear();
        packSizeField.clear();
        unitPriceField.clear();
        qtyOnHandField.clear();
    }

    private void loadItemsFromDatabase() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT ItemCode, Description, PackSize, UnitPrice, QtyOnHand FROM Item");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String itemCode = resultSet.getString("ItemCode");
                String description = resultSet.getString("Description");
                String packSize = resultSet.getString("PackSize");
                String unitPrice = resultSet.getString("UnitPrice");
                String qtyOnHand = resultSet.getString("QtyOnHand");

                Item item = new Item(itemCode, description, packSize, unitPrice, qtyOnHand);
                itemList.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveItemToDatabase(Item item) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Item (ItemCode, Description, PackSize, UnitPrice, QtyOnHand) VALUES (?, ?, ?, ?, ?)")) {

            statement.setString(1, item.getItemCode());
            statement.setString(2, item.getDescription());
            statement.setString(3, item.getPackSize());
            statement.setString(4, item.getUnitPrice());
            statement.setString(5, item.getQtyOnHand());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateItemInDatabase(Item item) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE Item SET Description = ?, PackSize = ?, UnitPrice = ?, QtyOnHand = ? WHERE ItemCode = ?")) {

            statement.setString(1, item.getDescription());
            statement.setString(2, item.getPackSize());
            statement.setString(3, item.getUnitPrice());
            statement.setString(4, item.getQtyOnHand());
            statement.setString(5, item.getItemCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteItemFromDatabase(Item item) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Item WHERE ItemCode = ?")) {

            statement.setString(1, item.getItemCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}