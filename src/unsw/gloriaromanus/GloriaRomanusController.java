package unsw.gloriaromanus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.esri.arcgisruntime.data.Feature;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.util.Pair;

public class GloriaRomanusController{

  @FXML
  private MapView mapView;

  @FXML
  private StackPane stackPaneMain;

  // could use ControllerFactory?
  private ArrayList<Pair<MenuController, VBox>> controllerParentPairs;

  private ArcGISMap map;

  private Map<String, String> provinceToOwningFactionMap;

  private Map<String, Integer> provinceToNumberTroopsMap;

  private String humanFaction;

  private Feature selectedProvince1;
  private Feature selectedProvince2;

  private FeatureLayer featureLayer_provinces;

  private Pair<MenuController, VBox> startMenu;
  private Pair<MenuController, VBox> victoryMenu;

  private Game game;

  @FXML
  private void initialize() throws JsonParseException, JsonMappingException, IOException, InterruptedException {
    this.game = new Game();

    provinceToOwningFactionMap = getProvinceToOwningFactionMap();

    provinceToNumberTroopsMap = new HashMap<String, Integer>();
    Random r = new Random();
    for (String provinceName : provinceToOwningFactionMap.keySet()) {
      provinceToNumberTroopsMap.put(provinceName, r.nextInt(500));
    }
    
    humanFaction = "Rome";

    selectedProvince1 = null;
    selectedProvince2 = null;

    String []menus = {"start_menu.fxml", "invasion_menu.fxml", "basic_menu.fxml", "recruitment_menu.fxml", "movement_menu.fxml"};
    controllerParentPairs = new ArrayList<Pair<MenuController, VBox>>();
    for (String fxmlName : menus){
      System.out.println(fxmlName);
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
      VBox root = (VBox)loader.load();
      MenuController menuController = (MenuController)loader.getController();
      menuController.setParent(this);
      if (fxmlName.equals("start_menu.fxml")) {
        startMenu = new Pair<MenuController, VBox>(menuController, root);
        controllerParentPairs.add(startMenu);
        continue;
      }
      controllerParentPairs.add(new Pair<MenuController, VBox>(menuController, root));
    }

    stackPaneMain.getChildren().add(controllerParentPairs.get(0).getValue());
    initializeProvinceLayers();
  }

  public void clickedInvadeButton(ActionEvent e) throws IOException {
    InvasionMenuController controller = ((InvasionMenuController)controllerParentPairs.get(0).getKey());
    String base = controller.getInvadingProvince();
    String destination = controller.getOpponentProvince();
    System.out.println("Dest: " +destination+" Base: "+base);
    if (selectedProvince1 != null && selectedProvince2 != null){
      if (confirmIfProvincesConnected(base, destination)){
        String result = game.getCampaign().invadeProvince(destination, base);
        System.out.println("result: " + result);
        updateProvinceOwnership();
        printMessageToTerminal(result);
        resetSelections();  // reset selections in UI
        addAllPointGraphics(); // reset graphics
      }
      else{
        printMessageToTerminal("Provinces not adjacent, cannot invade!");
      }
    } else {
      printMessageToTerminal("Please select provinces.");
    }
  }

  public void clickedMoveButton(ActionEvent e) throws IOException {
    MovementMenuController controller =  ((MovementMenuController)controllerParentPairs.get(0).getKey());
    String origin = controller.getOrigin();
    String destination = controller.getDestination();
    if (origin != null && destination != null){
      System.out.println("Origin: " + origin + " Dest: " + destination);
      if (confirmIfProvincesConnected(origin, destination)){
        boolean result = game.getCampaign().moveTroops(origin, destination);
        String message = result ? "Moved troops" : "Troops stuck";
        System.out.println("message:" + message);
        printMessageToTerminal(message);
        resetSelections();  // reset selections in UI
        addAllPointGraphics(); // reset graphics
      }
      else{
        printMessageToTerminal("Provinces not adjacent, cannot move!");
      }
    } else {
      printMessageToTerminal("Please select a province.");
    }
  }

  public void clickedRecruitButton(ActionEvent e, String unitType, String unitName) throws IOException {
    if (selectedProvince1 != null && !unitType.isEmpty() && !unitName.isEmpty()){
      String province = ((RecruitmentMenuController)controllerParentPairs.get(0).getKey()).getProvince();
      boolean result = game.getCampaign().recruitTroops(province, unitName, unitType);
      String message = result ? "Recruited troops" : "Unable to recruit troops";
      printMessageToTerminal(message);
      System.out.println("message: " + message);
      resetSelections();  // reset selections in UI
      addAllPointGraphics(); // reset graphics
    } else {
      printMessageToTerminal("Error recruiting troops");
    }
  }

  /**
   * Returns the Game object.
   * @return Game
   */
  public Game getGame() {
    return game;
  }

  /**
   * run this initially to update province owner, change feature in each
   * FeatureLayer to be visible/invisible depending on owner. Can also update
   * graphics initially
   */
  private void initializeProvinceLayers() throws JsonParseException, JsonMappingException, IOException {

    Basemap myBasemap = Basemap.createImagery();
    // myBasemap.getReferenceLayers().remove(0);
    map = new ArcGISMap(myBasemap);
    mapView.setMap(map);

    // note - tried having different FeatureLayers for AI and human provinces to
    // allow different selection colors, but deprecated setSelectionColor method
    // does nothing
    // so forced to only have 1 selection color (unless construct graphics overlays
    // to give color highlighting)
    GeoPackage gpkg_provinces = new GeoPackage("src/unsw/gloriaromanus/provinces_right_hand_fixed.gpkg");
    gpkg_provinces.loadAsync();
    gpkg_provinces.addDoneLoadingListener(() -> {
      if (gpkg_provinces.getLoadStatus() == LoadStatus.LOADED) {
        // create province border feature
        featureLayer_provinces = createFeatureLayer(gpkg_provinces);
        map.getOperationalLayers().add(featureLayer_provinces);

      } else {
        System.out.println("load failure");
      }
    });

    addAllPointGraphics();
  }

  private void addAllPointGraphics() throws JsonParseException, JsonMappingException, IOException {
    mapView.getGraphicsOverlays().clear();

    InputStream inputStream = new FileInputStream(new File("src/unsw/gloriaromanus/provinces_label.geojson"));
    FeatureCollection fc = new ObjectMapper().readValue(inputStream, FeatureCollection.class);

    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

    for (org.geojson.Feature f : fc.getFeatures()) {
      if (f.getGeometry() instanceof org.geojson.Point) {
        org.geojson.Point p = (org.geojson.Point) f.getGeometry();
        LngLatAlt coor = p.getCoordinates();
        Point curPoint = new Point(coor.getLongitude(), coor.getLatitude(), SpatialReferences.getWgs84());
        PictureMarkerSymbol s = null;
        String province = (String) f.getProperty("name");
        String faction = provinceToOwningFactionMap.get(province);

        TextSymbol t = new TextSymbol(10,
            faction + "\n" + province + "\n" + provinceToNumberTroopsMap.get(province), 0xFFFF0000,
            HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);

        switch (faction) {
          case "Gaul":
            // note can instantiate a PictureMarkerSymbol using the JavaFX Image class - so could
            // construct it with custom-produced BufferedImages stored in Ram
            // http://jens-na.github.io/2013/11/06/java-how-to-concat-buffered-images/
            // then you could convert it to JavaFX image https://stackoverflow.com/a/30970114

            // you can pass in a filename to create a PictureMarkerSymbol...
            s = new PictureMarkerSymbol(new Image((new File("images/Celtic_Druid.png")).toURI().toString()));
            break;
          case "Rome":
            // you can also pass in a javafx Image to create a PictureMarkerSymbol (different to BufferedImage)
            s = new PictureMarkerSymbol("images/legionary.png");
            break;
        }
        t.setHaloColor(0xFFFFFFFF);
        t.setHaloWidth(2);
        Graphic gPic = new Graphic(curPoint, s);
        Graphic gText = new Graphic(curPoint, t);
        graphicsOverlay.getGraphics().add(gPic);
        graphicsOverlay.getGraphics().add(gText);
      } else {
        System.out.println("Non-point geo json object in file");
      }

    }

    inputStream.close();
    mapView.getGraphicsOverlays().add(graphicsOverlay);
  }

  private FeatureLayer createFeatureLayer(GeoPackage gpkg_provinces) {
    FeatureTable geoPackageTable_provinces = gpkg_provinces.getGeoPackageFeatureTables().get(0);

    // Make sure a feature table was found in the package
    if (geoPackageTable_provinces == null) {
      System.out.println("no geoPackageTable found");
      return null;
    }

    // Create a layer to show the feature table
    FeatureLayer flp = new FeatureLayer(geoPackageTable_provinces);

    // https://developers.arcgis.com/java/latest/guide/identify-features.htm
    // listen to the mouse clicked event on the map view
    mapView.setOnMouseClicked(e -> {
      // was the main button pressed?
      if (e.getButton() == MouseButton.PRIMARY) {
        // get the screen point where the user clicked or tapped
        Point2D screenPoint = new Point2D(e.getX(), e.getY());

        // specifying the layer to identify, where to identify, tolerance around point,
        // to return pop-ups only, and
        // maximum results
        // note - if select right on border, even with 0 tolerance, can select multiple
        // features - so have to check length of result when handling it
        final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp,
            screenPoint, 0, false, 25);

        // add a listener to the future
        identifyFuture.addDoneListener(() -> {
          try {
            // get the identify results from the future - returns when the operation is
            // complete
            IdentifyLayerResult identifyLayerResult = identifyFuture.get();
            // a reference to the feature layer can be used, for example, to select
            // identified features
            if (identifyLayerResult.getLayerContent() instanceof FeatureLayer) {
              FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
              // select all features that were identified
              List<Feature> features = identifyLayerResult.getElements().stream().map(f -> (Feature) f).collect(Collectors.toList());

              if (features.size() > 1){
                printMessageToTerminal("Have more than 1 element - you might have clicked on boundary!");
              }
              else if (features.size() == 1){
                // note maybe best to track whether selected...
                Feature f = features.get(0);
                String province = (String)f.getAttributes().get("name");

                if (provinceToOwningFactionMap.get(province).equals(game.getCampaign().getFaction())){
                  // province owned by currPlayer
                  if (selectedProvince1 != null){
                    featureLayer.unselectFeature(selectedProvince1);
                  }
                  selectedProvince1 = f;
                  if (controllerParentPairs.get(0).getKey() instanceof InvasionMenuController){
                    ((InvasionMenuController)controllerParentPairs.get(0).getKey()).setInvadingProvince(province);
                  } else if (controllerParentPairs.get(0).getKey() instanceof MovementMenuController){
                    if (((MovementMenuController)controllerParentPairs.get(0).getKey()).containsOrigin() &&
                        ((MovementMenuController)controllerParentPairs.get(0).getKey()).containsDestination()) {
                      resetSelections();
                    } else if (((MovementMenuController)controllerParentPairs.get(0).getKey()).containsOrigin()) {
                        System.out.println("setting destination");
                      ((MovementMenuController)controllerParentPairs.get(0).getKey()).setDestination(province);
                    } else {
                        System.out.println("setting origin");
                      ((MovementMenuController)controllerParentPairs.get(0).getKey()).setOrigin(province);
                    }
                  } else if (controllerParentPairs.get(0).getKey() instanceof RecruitmentMenuController) {
                    ((RecruitmentMenuController)controllerParentPairs.get(0).getKey()).setProvince(province);
                    ((RecruitmentMenuController)controllerParentPairs.get(0).getKey()).displayTroops();
                  }

                } else {
                  if (selectedProvince2 != null){
                    featureLayer.unselectFeature(selectedProvince2);
                  }
                  selectedProvince2 = f;
                  if (controllerParentPairs.get(0).getKey() instanceof InvasionMenuController){
                    ((InvasionMenuController)controllerParentPairs.get(0).getKey()).setOpponentProvince(province);
                  }
                }

                featureLayer.selectFeature(f);
              }


            }
          } catch (InterruptedException | ExecutionException ex) {
            // ... must deal with checked exceptions thrown from the async identify
            // operation
            System.out.println("InterruptedException occurred");
          }
        });
      }
    });
    return flp;
  }

  private void updateProvinceOwnership() {
    JSONObject json = new JSONObject();
    JSONArray array = new JSONArray();
    for (Province p : game.getCampaign().getProvinces()) {
      array.put(p.getName());
    }
    json.put(game.getCampaign().getFaction(), array);
    array = new JSONArray();
    for (Province p : game.getCampaign().getEnemyProvinces()) {
      array.put(p.getName());
    }
    json.put(game.getCampaign().getEnemyFaction(), array);
    System.out.println(json.toString());
    Map<String, String> m = new HashMap<String, String>();
    for (String key : json.keySet()) {
      // key will be the faction name
      JSONArray ja = json.getJSONArray(key);
      // value is province name
      for (int i = 0; i < ja.length(); i++) {
        String value = ja.getString(i);
        m.put(value, key);
      }
    }

    provinceToOwningFactionMap = m;

  }

  private Map<String, String> getProvinceToOwningFactionMap() throws IOException {
    String content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership.json"));
    JSONObject ownership = new JSONObject(content);
    Map<String, String> m = new HashMap<String, String>();
    for (String key : ownership.keySet()) {
      // key will be the faction name
      JSONArray ja = ownership.getJSONArray(key);
      // value is province name
      for (int i = 0; i < ja.length(); i++) {
        String value = ja.getString(i);
        m.put(value, key);
      }
    }
    return m;
  }


  private ArrayList<String> getHumanProvincesList() throws IOException {
    // https://developers.arcgis.com/labs/java/query-a-feature-layer/

    String content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership.json"));
    JSONObject ownership = new JSONObject(content);
    return ArrayUtil.convert(ownership.getJSONArray(humanFaction));
  }

  /**
   * returns query for arcgis to get features representing human provinces can
   * apply this to FeatureTable.queryFeaturesAsync() pass string to
   * QueryParameters.setWhereClause() as the query string
   */
  private String getHumanProvincesQuery() throws IOException {
    LinkedList<String> l = new LinkedList<String>();
    for (String hp : getHumanProvincesList()) {
      l.add("name='" + hp + "'");
    }
    return "(" + String.join(" OR ", l) + ")";
  }

  private boolean confirmIfProvincesConnected(String province1, String province2) throws IOException {
    String content = Files.readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
    JSONObject provinceAdjacencyMatrix = new JSONObject(content);
    return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
  }

  private void resetSelections(){
    if (selectedProvince1 != null && selectedProvince2 != null) {
      featureLayer_provinces.unselectFeatures(Arrays.asList(selectedProvince2, selectedProvince1));
    }
    selectedProvince2 = null;
    selectedProvince1 = null;
    if (controllerParentPairs.get(0).getKey() instanceof InvasionMenuController){
      ((InvasionMenuController)controllerParentPairs.get(0).getKey()).setInvadingProvince("");
      ((InvasionMenuController)controllerParentPairs.get(0).getKey()).setOpponentProvince("");
    } else if (controllerParentPairs.get(0).getKey() instanceof MovementMenuController){
      ((MovementMenuController)controllerParentPairs.get(0).getKey()).setDestination("");
      ((MovementMenuController)controllerParentPairs.get(0).getKey()).setOrigin("");
    } else if (controllerParentPairs.get(0).getKey() instanceof RecruitmentMenuController){
      ((RecruitmentMenuController)controllerParentPairs.get(0).getKey()).setProvince("");
    }
  }

  private void printMessageToTerminal(String message){
    if (controllerParentPairs.get(0).getKey() instanceof MenuController){
      ((MenuController)controllerParentPairs.get(0).getKey()).appendToTerminal(message);
    } 
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }

  public void switchMenu() throws JsonParseException, JsonMappingException, IOException {
    System.out.println("trying to switch menu");
    stackPaneMain.getChildren().remove(controllerParentPairs.get(0).getValue());
    controllerParentPairs.remove(startMenu);
    Collections.rotate(controllerParentPairs, -1);
    MenuController controller = controllerParentPairs.get(0).getKey();
    if (controller instanceof BasicMenuController) {
      ((BasicMenuController)controller).update();
    } else if (controller instanceof RecruitmentMenuController) {
      ((RecruitmentMenuController)controller).displayTreasury();
    }
    stackPaneMain.getChildren().add(controllerParentPairs.get(0).getValue());
  }

  public void openMainMenu() {
    System.out.println("trying to switch menu");
    stackPaneMain.getChildren().remove(controllerParentPairs.get(0).getValue());
    controllerParentPairs.add(0, startMenu);
    stackPaneMain.getChildren().add(startMenu.getValue());
  }

  public void openVictoryMenu() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("victory_menu.fxml"));
    VBox root = (VBox)loader.load();
    MenuController menuController = (MenuController)loader.getController();
    menuController.setParent(this);
    victoryMenu = new Pair<MenuController, VBox>(menuController, root);
    
    stackPaneMain.getChildren().remove(controllerParentPairs.get(0).getValue());
    stackPaneMain.getChildren().add(victoryMenu.getValue());
  }

  public void closeVictoryMenu() {
    stackPaneMain.getChildren().remove(victoryMenu.getValue());
  }

  public void openMenus() {
    stackPaneMain.getChildren().add(controllerParentPairs.get(0).getValue());
  }

  public boolean hasWon() {
    return game.getCampaign().hasWon();
  }
}
