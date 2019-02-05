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
public class Main extends Application {
    private Label scoreLabel;
    private MyCircle circles[][] = new MyCircle[9][9];
    private static boolean startGame = true;
    private static boolean transport = false;
    private MyCircle activateCircle = null;
    private static int score = 0;  //текущий счет игры
    private ArrayList<MyCircle> circlesToDelete = new ArrayList<>();
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
        MyCircle circle = new MyCircle(rectangle.getTranslateX() + 25, rectangle.getTranslateY() + 25,
                18, i, j, randomColor());
        circle.setFill(circle.getColor());
        circle.toFront();
        circle.setOnMouseClicked(event -> {
            circleMouseClick(circle);
        });
        circles[i][j] = circle;
        root.getChildren().add(circle);
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
    private void circleMouseClick(MyCircle thisCircle) {
        if (transport == false) {
            activateCircle = thisCircle;
            transport = true;
        } else {
            //механизм самой смены шаров
            changeCircles(activateCircle, thisCircle);
            transport = false;
        }
    }

    //смена шаров на соседние
    private void changeCircles(MyCircle activateCircle, MyCircle thisCircle) {
        boolean down = (activateCircle.getI() == thisCircle.getI() - 1) && (activateCircle.getJ() == thisCircle.getJ());
        boolean up = (activateCircle.getI() == thisCircle.getI() + 1) && (activateCircle.getJ() == thisCircle.getJ());
        boolean left = (activateCircle.getI() == thisCircle.getI()) && (activateCircle.getJ() == thisCircle.getJ() + 1);
        boolean right = (activateCircle.getI() == thisCircle.getI()) && (activateCircle.getJ() == thisCircle.getJ() - 1);

        if (down || up || left || right) { //проверка на нижний, верхний, левый, правый шар
            //визуальная смена кругов местами
            double thisCircleXCenter = thisCircle.getCenterX();
            double thisCircleYCenter = thisCircle.getCenterY();
            thisCircle.setCenterX(activateCircle.getCenterX());
            thisCircle.setCenterY(activateCircle.getCenterY());
            activateCircle.setCenterX(thisCircleXCenter);
            activateCircle.setCenterY(thisCircleYCenter);

            //смена кругов местами
            int iOfThisCircle = thisCircle.getI();
            int jOfThisCircle = thisCircle.getJ();
            circles[activateCircle.getI()][activateCircle.getJ()] = thisCircle;
            circles[iOfThisCircle][jOfThisCircle] = activateCircle;
            thisCircle.setI(activateCircle.getI());
            thisCircle.setJ(activateCircle.getJ());
            activateCircle.setI(iOfThisCircle);
            activateCircle.setJ(jOfThisCircle);

            checkCirclesToDelete();
        }
    }

    private MyCircle getCircleByCoordinates(int i, int j) {
        MyCircle circle = null;
        for (int k = 0; k < circles.length; k++) {
            for (int h = 0; h < circles[k].length; h++) {
                if (circles[k][h].getI() == i && circles[k][h].getJ() == j) {
                    circle = circles[k][h];
                }
            }
        }
        return circle;
    }

    //поиск шаров для удаления
    private void checkCirclesToDelete() {
        for (int i = 0; i < circles.length; i++) {
            for (int j = 0; j < circles[i].length; j++) {
                checkRight(getCircleByCoordinates(i, j));
                checkDown(getCircleByCoordinates(i, j));
            }
        }
    }

    //просмотр правого элемента
    private void checkRight(MyCircle circle) {
        int nextIndex = circle.getJ() + 1;
        if (canToCheck(circle.getColor(), circle.getI(), nextIndex)) {
            circlesToDelete.add(circle);
            circlesToDelete.add(circles[circle.getI()][nextIndex]);
            nextIndex++;
            while (canToCheck(circle.getColor(), circle.getI(), nextIndex) == true) {
                circlesToDelete.add(getCircleByCoordinates(circle.getI(), nextIndex));
                nextIndex++;
            }
            finishChange(circlesToDelete.size(), startGame);
            circlesToDelete.clear();
        }
    }

    //просмотр нижнего элемента
    private void checkDown(MyCircle circle) {
        int nextIndex = circle.getI() + 1;
        if (canToCheck(circle.getColor(), nextIndex, circle.getJ())) {
            circlesToDelete.add(circle);
            circlesToDelete.add(circles[nextIndex][circle.getJ()]);
            nextIndex++;
            while (canToCheck(circle.getColor(), nextIndex, circle.getJ()) == true) {
                circlesToDelete.add(circles[circle.getI()][nextIndex]);
                nextIndex++;
            }
            finishChange(circlesToDelete.size(), startGame);
            circlesToDelete.clear();
        }
    }

    //возращает возможность на просмотр следующих шаров
    private boolean canToCheck(Color color, int nextI, int nextJ) {
        if ((nextI >= circles.length || nextI < 0) || (nextJ >= circles.length || nextJ < 0)) return false;
        else {
            if (color == getCircleByCoordinates(nextI, nextJ).getColor()) return true;
            else return false;
        }
    }

    //проверка на кол-во шаров для удаления и само удаление
    private void finishChange(int sizeToDelete, boolean startGame) {
        if (sizeToDelete >= 3) {
            for (MyCircle circle : circlesToDelete) {
                circle.setColor(randomColor());
                circle.setFill(circle.getColor());
                if (startGame != true) {
                    score += 10;
                    scoreLabel.setText("Счёт - " + score);
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
