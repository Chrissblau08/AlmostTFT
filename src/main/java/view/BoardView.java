package view;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import listener.ViewEventListener;
import model.Unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BoardView extends Pane {
    private static final int SIZE = 10;
    private GridPane grid;
    private Map<UUID, StackPane> unitSprites;
    private int clickedIndex = -1;
    private ViewEventListener eventListener;

    public BoardView() {
        grid = new GridPane();
        unitSprites = new HashMap<>();
        initializeGrid();
        getChildren().add(grid);
    }

    private void initializeGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid.add(createCell(row * SIZE + col, row >= SIZE / 2), col, row);
            }
        }
    }

    private Pane createCell(int index, boolean isInteractable) {
        Pane cell = new Pane();
        cell.setPrefSize(50, 50);

        Rectangle border = new Rectangle(50, 50);
        border.setFill(isInteractable ? Color.LIGHTBLUE : Color.TRANSPARENT);
        border.setStroke(Color.GRAY);
        border.setStrokeWidth(1);
        cell.getChildren().add(border);

        if (isInteractable) {
            cell.setOnMouseClicked(createCellClickHandler(index));
        }
        return cell;
    }

    private EventHandler<MouseEvent> createCellClickHandler(int index) {
        return mouseEvent -> {
            clickedIndex = index;
            eventListener.onBoardCellSelected(clickedIndex, "Board");
        };
    }

    public void setEventListener(ViewEventListener listener) {
        this.eventListener = listener;
    }

    public void highlightSelectedUnit(int index) {
        for (int i = 0; i < grid.getChildren().size(); i++) {
            Node cell = grid.getChildren().get(i);

            int rowIndex = GridPane.getRowIndex(cell) != null ? GridPane.getRowIndex(cell) : 0;
            int colIndex = GridPane.getColumnIndex(cell) != null ? GridPane.getColumnIndex(cell) : 0;

            int cellIndex = rowIndex * SIZE + colIndex;

            boolean isInteractable = rowIndex >= SIZE / 2;
            cell.setStyle(cellIndex == index && isInteractable ?
                    "-fx-border-color: blue; -fx-border-width: 2; -fx-background-color: lightblue;" : "");
        }
    }

    public void resetHighlight() {
        if (clickedIndex != -1) {
            Node slot = grid.getChildren().get(clickedIndex);
            slot.setStyle("");
        }
    }

    public void update(List<Unit> units) {
        resetGrid();
        units.forEach(this::placeUnit);
    }

    private void resetGrid() {
        grid.getChildren().clear();
        initializeGrid();
    }

    private void placeUnit(Unit unit) {
        StackPane unitPane = createUnitPane(unit);
        grid.add(unitPane, unit.getPosX(), unit.getPosY());
        unitSprites.put(unit.getUuid(), unitPane);
    }

    private StackPane createUnitPane(Unit unit)
    {
        ImageView unitSprite = new ImageView(new Image(getClass().getResourceAsStream("/sprites/pikachu.png")));
        unitSprite.setFitWidth(40);
        unitSprite.setFitHeight(40);

        ProgressBar hpBar = new ProgressBar();
        hpBar.setPrefWidth(40);
        hpBar.setProgress(unit.getHp() / (double) unit.getMaxHp());
        hpBar.setTranslateY(20);

        StackPane unitPane = new StackPane(hpBar, unitSprite);
        unitPane.setPrefSize(50, 50);
        unitPane.setOnMouseClicked(createCellClickHandler(unit.getPosY() * SIZE + unit.getPosX()));
        return unitPane;
    }

    public void updateUnit(Unit unit) {
        StackPane unitPane = unitSprites.get(unit.getUuid());
        if (unitPane != null) {
            if (!grid.getChildren().contains(unitPane) ||
                    GridPane.getRowIndex(unitPane) != unit.getPosY() ||
                    GridPane.getColumnIndex(unitPane) != unit.getPosX()) {
                grid.getChildren().remove(unitPane);
                grid.add(unitPane, unit.getPosX(), unit.getPosY());
            }
            ProgressBar hpBar = (ProgressBar) unitPane.getChildren().get(0);
            Platform.runLater(() -> hpBar.setProgress(unit.getHp() / (double) unit.getMaxHp()));
        } else {
            placeUnit(unit);
        }
    }

    public void updateUnitHP(Unit unit) {
        StackPane unitPane = unitSprites.get(unit.getUuid());
        if (unitPane != null) {
            ProgressBar hpBar = (ProgressBar) unitPane.getChildren().get(0);
            hpBar.setProgress(unit.getHp() / (double) unit.getMaxHp());
        }
    }
}
