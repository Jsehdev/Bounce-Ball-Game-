package jumpgame;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BounceBall extends Application {

    private int lives = 3;
    private int score = 0;

    private Timeline obstacleTimeline;
    private double obstacleSpeed = 400; 

    private GridPane grid;
    private StackPane player;
    private StackPane obstacle;

    private Label livesLabel = new Label("Lives: 3");
    private Label scoreLabel = new Label("Score: 0");
    private Label gameOverText = new Label("game over");   

    private boolean isJumping = false;
    private boolean gameOver = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // GRID CREATION
        grid = new GridPane();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(100, 100);
                cell.setBorder(new Border(new BorderStroke(
                        Color.HOTPINK,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(1)
                )));
                grid.add(cell, j, i);
            }
        }

        // PLAYER
        player = new StackPane();
        player.setPrefSize(100, 100);
        player.setBackground(new Background(new BackgroundFill(
                Color.MAGENTA, new CornerRadii(100), null
        )));
        grid.add(player, grid.getColumnCount() / 2, grid.getRowCount() - 1);

        // OBSTACLE
        obstacle = new StackPane();
        obstacle.setPrefSize(100, 100);
        obstacle.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        grid.add(obstacle, grid.getColumnCount() - 1, grid.getRowCount() - 1);

        // START MOVEMENT
        moveObstacle();

        // UI TOP BAR
        HBox top = new HBox(20, livesLabel, scoreLabel, gameOverText);
        top.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(grid);
        root.setTop(top);

        Scene scene = new Scene(root, 800, 500);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                jump();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("CSC 225 Final Project");
        primaryStage.show();
    }

   
    private void restartObstacleTimeline() {
        moveObstacle();
    }

   
    private void moveObstacle() {
        if (obstacleTimeline != null) {
            obstacleTimeline.stop();
        }

        obstacleTimeline = new Timeline(new KeyFrame(Duration.millis(obstacleSpeed), e -> {

            Integer col = GridPane.getColumnIndex(obstacle);
            Integer row = GridPane.getRowIndex(obstacle);

            if (col == null || row == null) return;

            if (col > 0) {
                grid.getChildren().remove(obstacle);
                grid.add(obstacle, col - 1, row);
            } else {
               
                grid.getChildren().remove(obstacle);
                grid.add(obstacle, grid.getColumnCount() - 1, row);

                // SPEED UP
                obstacleSpeed = Math.max(100, obstacleSpeed - 50);
                restartObstacleTimeline();
            }

            checkCollision();

        }));

        obstacleTimeline.setCycleCount(Animation.INDEFINITE);
        obstacleTimeline.play();
    }

    /** Player jump */
    private void jump() {
        if (isJumping || gameOver) return;

        isJumping = true;
        double jumpPixels = player.getHeight() * 5;

        TranslateTransition up = new TranslateTransition(Duration.millis(350), player);
        up.setToY(-jumpPixels);

        TranslateTransition pause = new TranslateTransition(Duration.millis(75), player);
        pause.setToY(-jumpPixels);

        TranslateTransition down = new TranslateTransition(Duration.millis(350), player);
        down.setToY(0);

        SequentialTransition jump = new SequentialTransition(up, pause, down);
        jump.setOnFinished(e -> isJumping = false);
        jump.play();
    }

    /** Collision check */
    private void checkCollision() {
        Integer pCol = GridPane.getColumnIndex(player);
        Integer oCol = GridPane.getColumnIndex(obstacle);

        if (pCol == null || oCol == null) return;

        // Collision occurs only if columns match
        if (pCol.equals(oCol)) {
            if (!isJumping) {
                lives--;
                livesLabel.setText("Lives: " + lives);
            } else {
                score++;
                scoreLabel.setText("Score: " + score);
            }

            checkGameOver();
        }
    }

    /** GAME OVER LOGIC */
    private void checkGameOver() {
        if (lives <= 0) {
            gameOver = true;

            if (obstacleTimeline != null) obstacleTimeline.stop();

            gameOverText.setText("GAME OVER!");
            livesLabel.setText("Lives: 0");
            scoreLabel.setText("Final Score: " + score);
        }
    }
}

                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
