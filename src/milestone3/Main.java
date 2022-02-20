/*
 * Name: Dongseok Cho,Zheyuan Xu
 * ID: 3021090
 * Date: 2020/11/8
 * Assignment: Milestone 3
 * Course: CMPT 305 AS01
 * Instructor: Indratmo Indratmo
 * Description: Creates a window to display the assessment values and search specific values.
 */
package milestone3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.JSONArray;
import org.json.JSONObject;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Main extends Application {
    private TableView<AssessmentValue> table;
    private TableView<Playground> playgroundTable;
    private TableView<NeighbourhoodAverage> neighbourhoodAverageTable;
    private ObservableList<AssessmentValue> tableElements;
    private ObservableList<Playground> playgroundTableElements;
    private ObservableList<NeighbourhoodAverage> neighbourhoodAverageElements;
    private ObservableList<String> comOption=FXCollections.observableArrayList();;

    private TextField accountNumField;
    private TextField addressField ;
    private TextField neighbourhoodField ;
    private TextArea statTextArea = new TextArea();
    private PropertyAssessment assessment;
    private static boolean sourceNoEnd = true;
    private boolean combSelected = true;
    private boolean accountNumFieldEmpty = true;
    private boolean addressFieldEmpty = true;
    private boolean neighbourhoodFieldEmpty = true;
    private static List<AssessmentValue> values= new ArrayList<>();// list for values in CSV File
    private static List<Playground> playgrounds = new ArrayList<>();// list of playgrounds in edmonton.
    private List<NeighbourhoodAverage> neighbourhoodAverages = new ArrayList<>();//list of neighbourhood assessment value average.
    private Alert alert;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage stage) throws Exception {
        List<String> comboBoxOption = new ArrayList<>();// list for options that presents in combo box
        stage.setTitle("Edmonton Property Assessments");
        VBox displayBox;
        VBox playgroundTableBox;
        VBox accountNumBox;
        VBox addressBox;
        VBox neighbourhoodBox;
        Button searchBtn ;
        Button resetBtn;
        Button mapViewBtn;
        Button pieChartBtn;
        alert=setAlert();
        VBox comboBox = new VBox(10);
        ComboBox combo = new ComboBox(comOption);

        configureTable();
        configurePlaygroundTable();

        int offset =10000;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://data.edmonton.ca/resource/q7d6-ambg.json?$limit=10000")).build();//connecting to the assessment data
        System.out.println("Processing Assessment Values...");
        while(sourceNoEnd){//get data until there is no more
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(Main::parse)
                    .join();
            request = HttpRequest.newBuilder().uri(URI.create("https://data.edmonton.ca/resource/q7d6-ambg.json?$limit=10000&$offset="+offset)).build();//getting next data
            offset += 10000;
        }
        tableElements.addAll(values);

        int pOffset = 100;
        sourceNoEnd = true;//reset the detector

        request = HttpRequest.newBuilder().uri(URI.create("https://data.edmonton.ca/resource/9nqb-w48x.json?$limit=100")).build();//connecting the playground data
        System.out.println("Processing Playground values...");
        while(sourceNoEnd){
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(Main::playgroundParse)
                .join();
         request = HttpRequest.newBuilder().uri(URI.create("https://data.edmonton.ca/resource/9nqb-w48x.json?$limit=100&$offset="+pOffset)).build();//getting next data
        pOffset += 100;
        }

        playgroundTableElements.addAll(playgrounds);

        comboBoxOption = comboBoxAdder(comboBoxOption);

        assessment = new PropertyAssessment(values);

        comOption.addAll(comboBoxOption);//adding all of the options to combo box

        final Label propAssLabel = new Label("Find Property Assessment");//Label
        propAssLabel.setFont(new Font("Arial",22));
        propAssLabel.setLayoutX(10);

        accountNumBox = setAccBox();// setting width,height, x,y location, and tile for account search box.
        setStatTextArea(values);//setting the width,height, x,y location, text in text area.
        addressBox = setAddressBox();// setting width,height, x,y location, and tile for address search box.
        neighbourhoodBox = setNeighbourhoodBox();// setting width,height, x,y location, and tile for neighbourhood search box.

        searchBtn = setSearchBtn();
        searchBtn.setOnAction(event ->{
            combSelected = true;
            accountNumFieldEmpty = true;
            addressFieldEmpty = true;
            neighbourhoodFieldEmpty = true;
            table.getItems().clear();
            statTextArea.clear();
            List<AssessmentValue> searchList = values;
            List<AssessmentValue> classList;

            classList = assessmentClassSearch(searchList,combo);// returning the list that matches the class search
            searchList = accountNumSearch(classList);// returning the list that matches the account number search
            searchList = addressSearch(searchList);// returning the list that matches the address search
            searchList = neighbourhoodSearch(searchList);// returning the list that matches the neighbourhood search

            tableElements.clear();
            comboDetector(searchList,classList);
        });

        resetBtn = setResetBtn();
        resetBtn.setOnAction(event ->{
            table.getItems().clear();
            accountNumField.clear();
            addressField.clear();
            neighbourhoodField.clear();
            combo.setValue(null);
            assessment.stat(values);
            statTextArea.setText(assessment.toString());
            tableElements.clear();
            tableElements.addAll(values);
        });// resetting all values to default.

        mapViewBtn = setMapViewBtn();
        mapViewBtn.setOnAction(event -> {
            ObservableList<AssessmentValue> selectedValue;
            selectedValue = table.getSelectionModel().getSelectedItems();

            Desktop desktop = Desktop.getDesktop();
            try {
                if(!selectedValue.isEmpty()) {
                    desktop.browse(new URI("http://www.google.com/maps/search/?api=1&query="+selectedValue.get(0).getLatitude()+","+selectedValue.get(0).getLongitude()));//pop up google map with selected row location.
                }
                else{desktop.browse(new URI("http://www.google.com/maps"));}//pop up google map.
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
        pieChartBtn = setPieChartBtn();
        pieChartBtn.setOnAction(event ->{
            List<AssessmentValue> searchList = values;
            searchList = assessmentClassSearch(searchList,combo);// returning the list that matches the class search
            searchList = accountNumSearch(searchList);// returning the list that matches the account number search
            searchList = addressSearch(searchList);// returning the list that matches the address search
            searchList = neighbourhoodSearch(searchList);
            int size = searchList.size();
            int size1=0;
            int size2=0;
            int size3=0;
            int size4=0;
            int size5=0;
            for (int i=0; i<size; i++){
                AssessmentValue valueChart = searchList.get(i);
                int value = valueChart.getAssessedValue();
                System.out.println(value);
                if (value<=100000){
                    size1++;
                }
                else if (100000<value && value<=500000){
                    size2++;
                }
                else if (value>500000 && value<=9000000){
                    size3++;
                }
                else if (value>9000000 && value<=100000000){
                    size4++;
                }
                else if (value>100000000){
                    size5++;
                }
            }
            double size6 = size1+size2+size3+size4+size5;
            double partChartsize1 = size1*100/(size6);
            double partChartsize2 = size2*100/(size6);
            double partChartsize3 = size3*100/(size6);
            double partChartsize4 = size4*100/(size6);
            double partChartsize5 = size5*100/(size6);
            Pane root = new Pane();
            Stage secondStage = new Stage();
            Scene secondScene = new Scene(new Group(),500,500);
            ObservableList<PieChart.Data> pieChartData =
                    FXCollections.observableArrayList(
                            new PieChart.Data("$0-100,000", partChartsize1),
                            new PieChart.Data("$100,001-500,000", partChartsize2),
                            new PieChart.Data("$500,001-9,000,00", partChartsize3),
                            new PieChart.Data("$900,001-100,000,000", partChartsize4),
                            new PieChart.Data("$100,000,001 and higher", partChartsize5));
            final PieChart chart = new PieChart(pieChartData);
            chart.setTitle("Assessed Value For Searching Data");

            final Label caption = new Label("");
            caption.setTextFill(Color.BLACK);
            caption.setStyle("-fx-font: 24 arial;");
            DoubleBinding total = Bindings.createDoubleBinding(() ->
                    pieChartData.stream().collect(Collectors.summingDouble(PieChart.Data::getPieValue)), pieChartData);
            for (final PieChart.Data data : chart.getData()) {
                data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                        e -> {
                            caption.setTranslateX(e.getSceneX());
                            caption.setTranslateY(e.getSceneY());
                            String text = String.format("%.1f%%", 100*data.getPieValue()/total.get()) ;
                            caption.setText(text);
                        }
                );
            }
            root.getChildren().addAll(chart, caption);
            ((Group) secondScene.getRoot()).getChildren().add(root);
            secondStage.setScene(secondScene);
            secondStage.show();
        });

        setNeighbourhoodAverageTable();
        Button neighbourhoodAverageButton = setNeighbourhoodAverageBtn();
        averageAssessmentValue();// average out the assessment value for each neighbourhood.
        neighbourhoodAverageButton.setOnAction(even ->{
            popTableNHAverage(setNHAverageBox());//show the result on table high to low.
        });

        final Label comboLabel = new Label("Assessment Class:");
        comboLabel.setFont(new Font("Arial",16));
        comboBox.getChildren().addAll(comboLabel,combo);
        comboBox.setLayoutX(10);
        comboBox.setLayoutY(230);

        playgroundTableBox = playgroundTableSet();
        playgroundTableBox.setLayoutX(10);
        playgroundTableBox.setLayoutY(560);
        displayBox = tableSet();
        displayBox.setLayoutX(300);
        Group group = new Group();// group for all labels, boxes, and buttons.
        group.getChildren().addAll(neighbourhoodAverageButton,
                displayBox,accountNumBox,addressBox,neighbourhoodBox,
                statTextArea,comboBox,searchBtn,resetBtn,propAssLabel,
                mapViewBtn,pieChartBtn,playgroundTableBox);
        Scene scene = new Scene(group,1500,800);
        stage.setScene(scene);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            public void handle(WindowEvent event) {
                Platform.exit();
            }
        });
        stage.show();

    }

    /**
     * Description: creates a columns for the table
     */
    private void configureTable(){
        table = new TableView();
        tableElements = FXCollections.observableArrayList();
        table.setItems(tableElements);

        TableColumn<AssessmentValue, Integer> accountCol = new TableColumn<>("Account");
        accountCol.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        accountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));

        TableColumn<AssessmentValue, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        addressCol.prefWidthProperty().bind(table.widthProperty().multiply(0.16));

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        TableColumn<AssessmentValue, Integer> assessedValueCol = new TableColumn<>("Assessed Value");
        assessedValueCol.setCellValueFactory(new PropertyValueFactory<>("assessedValue"));

        assessedValueCol.setCellFactory(tc -> new TableCell<>(){
            @Override
            protected void updateItem(Integer value, boolean empty){
                super.updateItem(value,empty);
                currencyFormat.setMaximumFractionDigits(0);
                setText(empty ? "" : currencyFormat.format(value));
            }
        });//changing the format to currency format

        assessedValueCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));

        TableColumn<AssessmentValue, String> assessmentClassCol = new TableColumn<>("Assessment Class");
        assessmentClassCol.setCellValueFactory(new PropertyValueFactory<>("assessmentClass"));
        assessmentClassCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));

        TableColumn<AssessmentValue, String> neighbourhoodCol = new TableColumn<>("Neighbourhood");
        neighbourhoodCol.setCellValueFactory(new PropertyValueFactory<>("neighbourhood"));
        neighbourhoodCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));

        TableColumn<AssessmentValue, Double> latitudeCol = new TableColumn<>("Latitude");
        latitudeCol.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        latitudeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));

        TableColumn<AssessmentValue, Double> longitudeCol = new TableColumn<>("LongitudeCol");
        longitudeCol.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        longitudeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.14));

        table.getColumns().setAll(accountCol,addressCol,assessedValueCol,assessmentClassCol,neighbourhoodCol,latitudeCol,longitudeCol);
    }

    /**
     * creates columns for playground table
     */
    private void configurePlaygroundTable(){
        playgroundTable = new TableView<>();
        playgroundTableElements = FXCollections.observableArrayList();
        playgroundTable.setItems(playgroundTableElements);

        TableColumn<Playground, String> neighbourhoodCol = new TableColumn<>("Neighbourhood");
        neighbourhoodCol.setCellValueFactory(new PropertyValueFactory<>("neighbourhood"));
        neighbourhoodCol.prefWidthProperty().bind(playgroundTable.widthProperty().multiply(0.50));

        TableColumn<Playground, Integer> playgroundNumberCol = new TableColumn<>("Playground Numbers");
        playgroundNumberCol.setCellValueFactory(new PropertyValueFactory<>("playgroundNumber"));
        playgroundNumberCol.prefWidthProperty().bind(playgroundTable.widthProperty().multiply(0.50));

        playgroundTable.getColumns().setAll(neighbourhoodCol,playgroundNumberCol);
    }

    /**
     * Creates VBox and sets the size of the VBox that in containing the table.
     * @return VBox with set values.
     */
    private VBox tableSet(){
        VBox box = new VBox(10);
        box.setMinHeight(800);
        box.setMinWidth(1500);
        final Label label = new Label("Edmonton Property Assessments");
        label.setFont(new Font("Arial",26));
        table.setMaxWidth(1200);
        table.setMinHeight(690);
        box.getChildren().addAll(label,table);
        return box;
    }

    /**
     * create VBox for the playground table.
     * @return VBox containing playground table.
     */
    private VBox playgroundTableSet(){
        VBox box = new VBox(10);
        box.setMinWidth(280);
        box.setMinHeight(200);
        final Label label = new Label("Edmonton Playgrounds in Neighbourhood");
        label.setFont(new Font("Arial",14));
        playgroundTable.setMaxWidth(270);
        playgroundTable.setMaxHeight(190);
        box.getChildren().addAll(label,playgroundTable);
        return box;
    }

    /**
     * Sets the TextArea size and value
     * @param searchList
     */
    private  void setStatTextArea(List<AssessmentValue> searchList){
        assessment.stat(searchList);
        statTextArea = new TextArea();
        statTextArea.setPrefSize(280,170);
        statTextArea.setLayoutY(380);
        statTextArea.setLayoutX(10);
        statTextArea.setEditable(false);
        statTextArea.setText(assessment.toString());
    }


    /**
     * Creates a VBox for account search and set size and title.
     * @return VBox with changed value
     */
    private VBox setAccBox(){
        accountNumField = new TextField();
        VBox box = new VBox(10);
        final Label accLabel = new Label("Account Number:");
        accLabel.setFont(new Font("Arial",16));
        box.getChildren().addAll(accLabel,accountNumField);
        box.setMinWidth(220);
        box.setLayoutX(10);
        box.setLayoutY(50);
        return box;
    }

    /**
     * reates VBox fot address search and set the size and title.
     * @return VBox with changed value
     */
    private VBox setAddressBox(){
        addressField = new TextField();
        VBox box = new VBox(10);
        final Label addressLabel = new Label("Address (#suit, #house street):");
        addressLabel.setFont(new Font("Arial",16));
        box.getChildren().addAll(addressLabel,addressField);
        box.setMinWidth(220);
        box.setLayoutY(110);
        box.setLayoutX(10);
        return box;
    }

    /**
     * creates VBox for neighbourhood search box and set the size, title and location.
     * @return VBox with changed value
     */
    private VBox setNeighbourhoodBox(){
        neighbourhoodField = new TextField();
        VBox box = new VBox(10);
        final Label neighbourhoodLabel = new Label("Neighbourhood:");
        neighbourhoodLabel.setFont(new Font("Arial",16));
        box.getChildren().addAll(neighbourhoodLabel,neighbourhoodField);
        box.setMinWidth(220);
        box.setLayoutY(170);
        box.setLayoutX(10);
        return box;
    }

    /**
     * creates search Button and set the size and location.
     * @return Button with custom value
     */
    private Button setSearchBtn(){
        Button btn = new Button("Search");
        btn.setLayoutX(10);
        btn.setLayoutY(310);
        return btn;
    }

    /**
     * creates reset Button and set the size and location.
     * @return Button with custom value
     */
    private Button setResetBtn(){
        Button btn = new Button("Reset");
        btn.setLayoutY(310);
        btn.setLayoutX(70);
        return btn;
    }

    /**
     * creates map view btn and set the size and location.
     * @return Button with custom value
     */
    private Button setMapViewBtn(){
        Button btn = new Button("Map View");
        btn.setLayoutY(310);
        btn.setLayoutX(130);
        return btn;
    }

    /**
     * creates pie chart button and set the size and location.
     * @return Button with custom value
     */
    private Button setPieChartBtn(){
        Button btn = new Button("PieChart");
        btn.setLayoutX(210);
        btn.setLayoutY(310);
        return btn;
    }

    /**
     * creates alert and set the alert value.
     * @return Alert with custom value.
     */
    private Alert setAlert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Search Results");
        alert.setHeaderText(null);
        alert.setContentText("Oops, did not find anything!");
        return alert;
    }
    /**
     * add distinct assessment class from multiple AssessmentValue to the list.
     * @param comboBoxOption
     * @return List of combo Box option.
     */
    private List<String> comboBoxAdder(List<String> comboBoxOption){
        for(int i=0;i<values.size();i++) {
            if (!comboBoxOption.contains(values.get(i).getAssessmentClass())) {
                comboBoxOption.add(values.get(i).getAssessmentClass());
            }
        }
        return comboBoxOption;
    }

    /**
     * determines if combo box is selected.
     * @param searchList
     * @param classList
     */
    private void comboDetector(List<AssessmentValue> searchList,List<AssessmentValue> classList) {
        if (!combSelected) {//if combo box not is selected.
            resultDetector(searchList,values);
        }
        else {
            if (!accountNumFieldEmpty || !neighbourhoodFieldEmpty || !addressFieldEmpty) {//if all of text fields are not empty.
                resultDetector(searchList,classList);
            }
            else{
                tableElements.clear();
                tableElements.addAll(classList);
                assessment.stat(classList);//calculating the new stat value based on the search.
                statTextArea.setText(assessment.toString());
            }
        }
    }

    /**
     * founds out if any result found or not
     * @param searchList
     * @param classList
     */
    private void resultDetector(List<AssessmentValue> searchList, List<AssessmentValue> classList){
        if (!searchList.equals(classList)) {// if original list then nothing is matching, so don't creat table

            tableElements.addAll(searchList);
            if (searchList.size() > 1) {//only show the result when there is more then one values.
                assessment.stat(searchList);//calculating the new stat value based on the search.
                statTextArea.setText(assessment.toString());
            }
        }
        else {
            //tableElements.clear();
            statTextArea.setText("");
            alert.showAndWait();
        }
    }

    /**
     * Searches matching account number from the assessment value list.
     * @param searchList
     * @return if matching exist return matched list if not return original list.
     */
    private List<AssessmentValue> accountNumSearch(List<AssessmentValue> searchList){
        if(accountNumField.getText().equals("")){return searchList;}//if nothing matched, return the given list.
        accountNumFieldEmpty=false;
        return assessment.assessAccountNum(searchList,accountNumField.getText());
    }

    /**
     * Searches matching address from the assessment value list.
     * @param searchList
     * @return if matching exist return matched list if not return original list.
     */
    private List<AssessmentValue> addressSearch(List<AssessmentValue> searchList){
        if(addressField.getText().equals("")){return searchList;}//if nothing matched, return the given list.
        addressFieldEmpty = false;
        return assessment.assessAddress(searchList,addressField.getText().toLowerCase());
    }

    /**
     * searches matching neighbourhood from the assessment value list.
     * @param searchList
     * @return if matching exist return matched list if not return original list.
     */
    private List<AssessmentValue> neighbourhoodSearch(List<AssessmentValue> searchList){
        if(neighbourhoodField.getText().equals("")){return searchList;}//if nothing matched, return the given list.
        neighbourhoodFieldEmpty = false;
        return assessment.assessNeighbourhood(searchList,neighbourhoodField.getText().toLowerCase());
    }

    /**
     * searches matching assessment class from the assessment value list.
     * @param searchList
     * @param combo
     * @return if matching exist return matched list if not return original list.
     */
    private List<AssessmentValue> assessmentClassSearch(List<AssessmentValue> searchList, ComboBox combo){
        if(combo.getValue()==null){combSelected=false; return searchList;}//if nothing matched, return the given list.
        return assessment.assessClass(searchList,combo.getValue().toString().toLowerCase());
    }

    /**
     * Parse Json and set the values of AssessmentValue class then add them to list of Assessment Value.
     * @param responseBody
     * @return  if source is empty false if not true
     */
    public static boolean parse(String responseBody) {
        JSONArray sources = new JSONArray(responseBody);
        if(sources.isEmpty()){sourceNoEnd=false;}
        for(int i=0;i<sources.length();i++){
            AssessmentValue value = new AssessmentValue();
            JSONObject source = sources.getJSONObject(i);
            value.setAccountNumber(source.getInt("account_number"));
            value.setAssessmentClass(source.getString("mill_class_1"));
            value.setAssessedValue(source.getInt("assessed_value"));
            value=neighbourhoodAdder(source,value);
            value.setLocation(source.getDouble("latitude"),source.getDouble("longitude"));
            value = addressAdder(source,value);
            values.add(value);
        }

        return true;
    }

    /**
     * Parse Json and find out how many playground is in each neighbourhood.
     * @param responseBody
     * @return true if there is neighbourhood with playground, false if not.
     */
    public static boolean playgroundParse(String responseBody){
        JSONArray sources = new JSONArray(responseBody);
        if(sources.isEmpty()){sourceNoEnd=false;}
        for(int i=0;i<sources.length();i++){
            Playground playground = new Playground();
            JSONObject source = sources.getJSONObject(i);
            if(!isNeighbourhoodExist(source)) {
                playground.setPlayground(source.getString("neighbourhood_name"));
                playgrounds.add(playground);
            }
        }
        return true;
    }

    /**
     * finds out if the neighbourhood is already exist in the list and if exist increment the playground number.
     * @param source
     * @return true if it is in the list, false if not.
     */
    public static boolean isNeighbourhoodExist(JSONObject source){
        for(int i=0;i<playgrounds.size();i++){
            if(playgrounds.get(i).getNeighbourhood().equals(source.getString("neighbourhood_name"))){
                playgrounds.get(i).incrementPlayground();
                return true;
            }
        }
        return false;
    }

    /**
     * See if the Json value has full address, part address or no address.
     * @param source
     * @param value
     * @return if value exist return assessmentValue with value if not with spaces.
     */
    public static AssessmentValue addressAdder(JSONObject source,AssessmentValue value){
        if(!source.has("suite")){
            if(source.has("house_number")){
                value.setAddress(source.getString("street_name"),"",source.getString("house_number"));
            }
            else{value.setAddress("","","");}
        }
        else{value.setAddress(source.getString("street_name"),source.getString("suite"),source.getString("house_number"));}
        return value;
    }

    /**
     * see if Json value has neighbourhood or not.
     * @param source
     * @param value
     * @return if value exist return assessmentValue with value if not with spaces.
     */
    public static AssessmentValue neighbourhoodAdder(JSONObject source,AssessmentValue value){
        if(!source.has("neighbourhood")){ value.setNeighbourhood(""); }
        else{value.setNeighbourhood(source.getString("neighbourhood"));}
        return value;
    }

    /**
     *
     */
    private void averageAssessmentValue(){
        NeighbourhoodAverage nHA = new NeighbourhoodAverage();
        nHA.setNeighbourhood(values.get(0).getNeighbourhood(),values.get(0).getAssessedValue());
        neighbourhoodAverages.add(nHA);//Adding first row value to the list.


        for(int i=1; i<values.size();i++){//start from second row and search all neighbourhood in the assessment value list.
            nHA = new NeighbourhoodAverage();
            if(!averageNeighbourhoodExist(i)){//if neighbourhood does not exist in the list.
                nHA.setNeighbourhood(values.get(i).getNeighbourhood(),values.get(i).getAssessedValue());
                neighbourhoodAverages.add(nHA);
            }
        }
        insertionSort();//sorting values high to low
        neighbourhoodAverageElements.addAll(neighbourhoodAverages);// adding to the table

    }

    /**
     * finds out if the neighbourhood exists in the list.
     * @param i
     * @return true if exist in list, false if not.
     */
    private boolean averageNeighbourhoodExist(int i){
        for(int j=0; j<neighbourhoodAverages.size();j++) {//look if the neighbourhood is already in the list
            if (values.get(i).getNeighbourhood().equals(neighbourhoodAverages.get(j).getNeighbourhood())) {//if exist increment
                neighbourhoodAverages.get(j).incrementCount(values.get(i).getAssessedValue());
                return true;
            }
        }
        return false;
    }

    /**
     * creating columns for the neighbourhood assessment value average table.
     */
    private void setNeighbourhoodAverageTable(){
        neighbourhoodAverageTable = new TableView<>();
        neighbourhoodAverageElements = FXCollections.observableArrayList();
        neighbourhoodAverageTable.setItems(neighbourhoodAverageElements);

        TableColumn<NeighbourhoodAverage, String> neighbourhoodCol = new TableColumn<>("Neighbourhood");
        neighbourhoodCol.setCellValueFactory(new PropertyValueFactory<>("neighbourhood"));
        neighbourhoodCol.prefWidthProperty().bind(neighbourhoodAverageTable.widthProperty().multiply(0.5));

        TableColumn<NeighbourhoodAverage,String> averageCol = new TableColumn<>("Average Value");
        averageCol.setCellValueFactory(new PropertyValueFactory<>("average"));
        averageCol.prefWidthProperty().bind(neighbourhoodAverageTable.widthProperty().multiply(0.5));

        neighbourhoodAverageTable.getColumns().setAll(neighbourhoodCol,averageCol);

    }

    /**
     * creating VBox for neighbourhoodAverageTable.
     * @return VBox with neighbourhood average table.
     */
    private VBox setNHAverageBox(){
        VBox box = new VBox(10);
        final Label label = new Label("Neighbourhood Assessment Value Average");
        box.setMinWidth(500);
        box.setMinHeight(550);
        box.setLayoutX(30);
        label.setFont(new Font("Arial",26));
        neighbourhoodAverageTable.setMaxWidth(490);
        neighbourhoodAverageTable.setMaxHeight(540);
        neighbourhoodAverageTable.setLayoutX(20);
        box.getChildren().addAll(label,neighbourhoodAverageTable);

        return box;
    }

    /**
     * creates a pop up window and show.
     * @param box
     */
    private void popTableNHAverage(VBox box){
        Stage thirdStage = new Stage();
        Group group = new Group(box);
        Scene thirdScene = new Scene(group,550,470);
        thirdStage.setScene(thirdScene);
        thirdStage.show();
    }

    /**
     * setting button property for neighbourhood average table button.
     * @return button with custom property
     */
    private Button setNeighbourhoodAverageBtn(){
        Button btn = new Button("Average");
        btn.setLayoutY(340);
        btn.setLayoutX(10);
        return btn;
    }

    /**
     * sorting neighbourhoodAverages List with insertionSort.
     */
    private void insertionSort(){
        NeighbourhoodAverage[] n =  new NeighbourhoodAverage[neighbourhoodAverages.size()];
        n = neighbourhoodAverages.toArray(n);

        for(int i=1; i < n.length;i++){
            NeighbourhoodAverage current = n[i];
            int j = i-1;
            while(j >=0 && current.getAverageNum() > n[j].getAverageNum()){
                n[j+1] = n[j];
                j--;
            }
            n[j+1] = current;
        }
        neighbourhoodAverages = Arrays.asList(n);
    }
}
