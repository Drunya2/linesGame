import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Andrey on 04.02.2019.
 */
public class Start extends Application {
    private Label scoreLabel;
    private Circle circles[][] = new Circle[9][9];
    private Color colorsOfCircles[][] = new Color[9][9];
    private static boolean startGame = true;
    private static boolean transport = false;
    private Circle activateCircle = null;
    private static int score = 0;  //текущий счет игры
    private ArrayList<Circle> circlesToDelete = new ArrayList<>();
    private static final int startXOfRectangle = 175; //начальная позиция квадрата по X
    private static int startYOfRectangle = 175; //начальная позиция квадрата по Y

    public static void main(String[] args) {
        launch(args);
    }

    private void startGame(Pane root) {
        createField(root);
        checkCirclesToDelete();
        startGame = false;
    }

    private Rectangle setRectangleParameters(Rectangle rectangle) {
        rectangle.setStrokeWidth(1);
        rectangle.setStroke(Paint.valueOf("#000000"));
        rectangle.setFill(Paint.valueOf("#00000000"));
        return rectangle;
    }

    //создание поля(квадратов)
    private void createField(Pane root) {
        Rectangle transientRectangle = null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setHeight(50);
                rectangle.setWidth(50);
                if (j == 0) rectangle.setTranslateX(startXOfRectangle);
                else rectangle.setTranslateX(transientRectangle.getTranslateX() + rectangle.getWidth());
                rectangle.setTranslateY(startYOfRectangle);
                rectangle = setRectangleParameters(rectangle);
                transientRectangle = rectangle;
                createAllCircles(rectangle, i, j, root);
                root.getChildren().add(rectangle);
                rectangle.toBack();
            }
            startYOfRectangle += transientRectangle.getHeight();
        }

    }

    //создать начальные шары
    private void createAllCircles(Rectangle rectangle, int i, int j, Pane root) {
        Circle circle = new Circle();
        circle.setRadius(18);
        circle.setCenterX(rectangle.getTranslateX() + 25);
        circle.setCenterY(rectangle.getTranslateY() + 25);
        root.getChildren().add(circle);
        colorsOfCircles[i][j] = randomColor();
        circle.setFill(colorsOfCircles[i][j]);
        circle.toFront();
        circle.setOnMouseClicked(event -> {
            circleMouseClick(circle);
        });
        circles[i][j] = circle;
    }

    //вернет рандомный цвет
    private Color randomColor() {
        Random random = new Random();
        int randomIndexOfColor = random.nextInt(5);
        if (randomIndexOfColor == 0) return Color.RED;
        else if (randomIndexOfColor == 1) return Color.BLUE;
        else if (randomIndexOfColor == 2) return Color.PURPLE;
        else if (randomIndexOfColor == 3) return Color.YELLOW;
        else if (randomIndexOfColor == 4) return Color.DARKBLUE;
        return Color.RED;
    }

    //событие нажатия на круг
    private void circleMouseClick(Circle circle) {
        if (transport == false) {
            activateCircle = circle;
            transport = true;
        } else {
            Circle thisCircle = circle;
            int coordinateICircle = 0;
            int coordinateJCircle = 0;
            int coordinateIActivate = 0;
            int coordinateJActivate = 0;

            for (int i = 0; i < circles.length; i++) {
                for (int j = 0; j < circles[i].length; j++) {
                    if (circles[i][j] == thisCircle) {
                        coordinateICircle = i;
                        coordinateJCircle = j;
                    }
                    if (circles[i][j] == activateCircle) {
                        coordinateIActivate = i;
                        coordinateJActivate = j;
                    }
                }
            }

            //механизм самой смены шаров
            changeCircles(coordinateIActivate, coordinateJActivate, coordinateICircle,
                    coordinateJCircle, circle);
            transport = false;
        }
    }

    //смена шаров на соседние
    private void changeCircles(int coordinateIActivate, int coordinateJActivate, int coordinateICircle, int coordinateJCircle,
                               Circle circle) {
        boolean down = (coordinateIActivate == coordinateICircle - 1) && (coordinateJActivate == coordinateJCircle);
        boolean up = (coordinateIActivate == coordinateICircle + 1) && (coordinateJActivate == coordinateJCircle);
        boolean left = (coordinateIActivate == coordinateICircle) && (coordinateJActivate == coordinateJCircle + 1);
        boolean right = (coordinateIActivate == coordinateICircle) && (coordinateJActivate == coordinateJCircle - 1);

        if (down || up || left || right) {
            //смена кругов местами
            double circleXCenter = circle.getCenterX();
            double circleYCenter = circle.getCenterY();
            circle.setCenterX(activateCircle.getCenterX());
            circle.setCenterY(activateCircle.getCenterY());
            activateCircle.setCenterX(circleXCenter);
            activateCircle.setCenterY(circleYCenter);
            circles[coordinateIActivate][coordinateJActivate] = circle;
            circles[coordinateICircle][coordinateJCircle] = activateCircle;

            //смена цветов местами
            Color colorActivateCircle = colorsOfCircles[coordinateIActivate][coordinateJActivate];
            Color colorCircle = colorsOfCircles[coordinateICircle][coordinateJCircle];
            colorsOfCircles[coordinateIActivate][coordinateJActivate] = colorCircle;
            colorsOfCircles[coordinateICircle][coordinateJCircle] = colorActivateCircle;
            checkCirclesToDelete();
        }
    }

    //поиск шаров для удаления
    private void checkCirclesToDelete() {
        for (int i = 0; i < circles.length; i++) {
            for (int j = 0; j < circles[i].length; j++) {
                checkRight(i, j);
                checkDown(i, j);
            }
        }
    }

    //возращает возможность на просмотр следующих шаров
    private boolean canToCheck(Color color, int nextI, int nextJ) {
        if ((nextI >= circles.length || nextI < 0) || (nextJ >= circles.length || nextJ < 0)) return false;
        else {
            if (color == colorsOfCircles[nextI][nextJ]) return true;
            else return false;
        }
    }

    //просмотр правого элемента
    private void checkRight(int i, int j) {
        int nextIndex = j + 1;
        if (canToCheck(colorsOfCircles[i][j], i, nextIndex)) {
            circlesToDelete.add(circles[i][j]);
            circlesToDelete.add(circles[i][nextIndex]);
            nextIndex++;
            while (canToCheck(colorsOfCircles[i][j], i, nextIndex) == true) {
                circlesToDelete.add(circles[i][nextIndex]);
                nextIndex++;
            }
            finishChange(circlesToDelete.size(), startGame);
            circlesToDelete.clear();
        }
    }

    //просмотр нижнего элемента
    private void checkDown(int i, int j) {
        int nextIndex = i + 1;
        if (canToCheck(colorsOfCircles[i][j], nextIndex, j)) {
            circlesToDelete.add(circles[i][j]);
            circlesToDelete.add(circles[nextIndex][j]);
            nextIndex++;
            while (canToCheck(colorsOfCircles[i][j], nextIndex, j) == true) {
                circlesToDelete.add(circles[i][nextIndex]);
                nextIndex++;
            }
            finishChange(circlesToDelete.size(), startGame);
            circlesToDelete.clear();
        }
    }

    //проверка на кол-во шаров для удаления и само удаление
    private void finishChange(int sizeToDelete, boolean startGame) {
        if (sizeToDelete >= 3) {
            for (Circle circle : circlesToDelete) {
                for (int k = 0; k < colorsOfCircles.length; k++) {
                    for (int h = 0; h < colorsOfCircles[k].length; h++) {
                        if (circle == circles[k][h]) {
                            colorsOfCircles[k][h] = randomColor();
                            circle.setFill(colorsOfCircles[k][h]);
                            if (startGame != true) {
                                score += 10;
                                scoreLabel.setText("Счёт - " + score);
                            }
                        }
                    }
                }
            }
            circlesToDelete.clear();
            checkCirclesToDelete();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane root = new Pane();
        startGame(root);
        scoreLabel = new Label();
        scoreLabel.setText("Счёт - " + String.valueOf(score));
        scoreLabel.setFont(new Font("Arial", 30));
        scoreLabel.setTranslateX(340);
        scoreLabel.setTranslateY(100);
        root.getChildren().add(scoreLabel);
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.show();
    }
}
