
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.ArrayList;
public class Main extends Application {
    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Booking> bookings = new ArrayList<>();
    GridPane roomGrid = new GridPane();
    ListView<String> bookingList = new ListView<>();
    ComboBox<String> roomSelectGlobal = new ComboBox<>();
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        TabPane tabPane = new TabPane();
        Tab roomsTab = new Tab("Rooms");
        Tab bookingTab = new Tab("Booking");
        Tab checkoutTab = new Tab("Checkout");
        roomsTab.setContent(createRoomsUI());
        bookingTab.setContent(createBookingUI());
        checkoutTab.setContent(createCheckoutUI());
        tabPane.getTabs().addAll(roomsTab, bookingTab, checkoutTab);
        Label header = new Label("🏨 GRAND HORIZON HOTEL");
        header.setStyle("-fx-text-fill: gold; -fx-font-size: 26px; -fx-font-weight: bold;");
        VBox root = new VBox(15, header, tabPane);
        root.setPadding(new Insets(15));
        Scene scene = new Scene(root, 900, 650);
        scene.getStylesheets().add("style.css");
        stage.setTitle("Hotel Management System");
        stage.setScene(scene);
        stage.show();
    }
    //  Rooms UI
    private VBox createRoomsUI() {
        Label title = new Label("🏨 Hotel Room Management");
        title.getStyleClass().add("title");
        TextField number = new TextField();
        number.setPromptText("Room Number");

        TextField price = new TextField();
        price.setPromptText("Price");
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("Standard", "Deluxe", "Suite");
        Button add = new Button("Add Room");
        add.setOnAction(e -> {
            if (!number.getText().isEmpty() && type.getValue() != null) {

                rooms.add(new Room(number.getText(), type.getValue(), price.getText(), "Available"));
                updateRooms();
                updateRoomSelect(roomSelectGlobal);
                number.clear();
                price.clear();
            }
        });

        roomGrid.setHgap(20);
        roomGrid.setVgap(20);
        roomGrid.setPadding(new Insets(10));
        roomGrid.setAlignment(Pos.TOP_CENTER);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 15;");
        layout.getChildren().addAll(title, number, type, price, add, roomGrid);
        return layout;
    }
    //  Booking UI
    private VBox createBookingUI() {
        Label title = new Label("📋 Select Room & Book");
        title.getStyleClass().add("title");
        TextField guest = new TextField();
        guest.setPromptText("Guest Name");

        roomSelectGlobal.setPromptText("Select Available Room");

        Button book = new Button("Book Room");
        book.setOnAction(e -> {
            String selectedRoom = roomSelectGlobal.getValue();
            if (selectedRoom != null && !guest.getText().isEmpty()) {
                for (Room r : rooms) {
                    if (r.number.equals(selectedRoom) && r.status.equals("Available")) {
                        r.status = "Booked";
                        bookings.add(new Booking(guest.getText(), r.number));
                        updateRooms();
                        updateBookings();
                        // refresh dropdown
                        updateRoomSelect(roomSelectGlobal);
                        guest.clear();
                        break;
                    }
                }
            }
        });
        updateRoomSelect(roomSelectGlobal);
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 15;");
        layout.getChildren().addAll(title, guest, roomSelectGlobal, book);
        return layout;
    }
    private VBox createCheckoutUI() {

        Label title = new Label("🚪 Checkout");
        title.getStyleClass().add("title");
        Button checkout = new Button("Checkout Selected");
        checkout.setOnAction(e -> {
            int index = bookingList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                Booking b = bookings.get(index);
                for (Room r : rooms) {
                    if (r.number.equals(b.roomNumber)) {
                        r.status = "Available";
                    }
                }
                bookings.remove(index);
                updateRooms();
                updateBookings();
                updateRoomSelect(roomSelectGlobal);
            }
        });
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: rgba(0,0,0,0.3); -fx-background-radius: 15;");
        layout.getChildren().addAll(title, bookingList, checkout);
        return layout;
    }
    private void updateRooms() {
        roomGrid.getChildren().clear();
        int col = 0;
        int row = 0;
        for (Room r : rooms) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(12));
            card.setPrefWidth(180);
            card.setStyle(
                "-fx-background-color: #1e293b;" +
                "-fx-border-color: gold;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;"
            );
            Label roomNo = new Label("🏨 Room " + r.number);
            roomNo.setStyle("-fx-text-fill: gold; -fx-font-weight: bold;");
            Label typeLbl = new Label("Type: " + r.type);
            Label priceLbl = new Label("Price: ₹" + r.price);
            Label statusLbl = new Label("Status: " + r.status);
            statusLbl.setStyle(r.status.equals("Available") ?
                    "-fx-text-fill: lightgreen;" :
                    "-fx-text-fill: red;");
            card.getChildren().addAll(roomNo, typeLbl, priceLbl, statusLbl);
            roomGrid.add(card, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }
    private void updateBookings() {
        bookingList.getItems().clear();
        for (Booking b : bookings) {
            bookingList.getItems().add(
                "👤 " + b.name + " → Room " + b.roomNumber
            );
        }
    }
    private void updateRoomSelect(ComboBox<String> combo) {
        combo.getItems().clear();
        for (Room r : rooms) {
            if (r.status.equals("Available")) {
                combo.getItems().add(r.number);
            }
        }
    }
    class Room {
        String number, type, price, status;
        Room(String n, String t, String p, String s) {
            number = n;
            type = t;
            price = p;
            status = s;
        }
    }
    class Booking {
        String name, roomNumber;
        Booking(String n, String r) {
            name = n;
            roomNumber = r;
        }
    }
}
