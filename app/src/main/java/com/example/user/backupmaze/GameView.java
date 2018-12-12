package com.example.user.backupmaze;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GameView extends View implements SensorEventListener{



    private enum Direction{
        UP, DOWN, LEFT, RIGHT
    }

    private int score;
    public int count;

    private Cell[][] cells;
    private static final int COLS = 7, ROWS = 10;

    private Cell player, exit;

    private float cellSize, hMargin, vMargin;
    private Paint wallPaint, playerPaint, exitPaint;
    private float sensorX, sensorY, sensorZ;
    private Direction direction;


    private Random random;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    public GameView(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(6);

        playerPaint = new Paint();
        playerPaint.setColor(Color.LTGRAY);

        exitPaint = new Paint();
        exitPaint.setColor(Color.GREEN);

        random = new Random();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);



        createMaze();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorX = event.values[0];
        sensorY = event.values[1];
      //  sensorZ = event.values[2];

       // Log.d("X Y Z", "x=" + sensorX + " y=" + sensorY + " z=" + sensorZ);
        float x, y;
        x = Math.abs(sensorX);
        y = Math.abs(sensorY);


        if(x > y){
            if(sensorX > 0)
                direction = Direction.LEFT;
            else
                direction = Direction.RIGHT;
        }else{
            if(sensorY > 0)
                direction = Direction.DOWN;
            else
                direction = Direction.UP;

        }
        movePlayer(direction);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public Cell getNeighbour(Cell cell){
        ArrayList<Cell> neighbours = new ArrayList<>();

        //left neigh
        if(cell.col>0) {
            if (!cells[cell.col - 1][cell.row].visited)
                neighbours.add(cells[cell.col - 1][cell.row]);
        }
        //right neigh
        if(cell.col < COLS-1) {
            if (!cells[cell.col + 1][cell.row].visited)
                neighbours.add(cells[cell.col + 1][cell.row]);
        }
        //top neigh
        if(cell.row>0) {
            if (!cells[cell.col][cell.row - 1].visited)
                neighbours.add(cells[cell.col][cell.row-1]);
        }
        //bottom neigh
        if(cell.row <ROWS-1) {
            if (!cells[cell.col][cell.row + 1 ].visited)
                neighbours.add(cells[cell.col][cell.row + 1]);
        }

        if(neighbours.size() > 0) {
            int index = random.nextInt(neighbours.size());
            return neighbours.get(index);
        }

        return null;
    }

    private void removeWall(Cell current, Cell next){
        if(current.col == next.col && current.row == next.row+1){
            current.topWall = false;
            next.bottomWall= false;
        }

        if(current.col == next.col && current.row == next.row-1){
            current.bottomWall = false;
            next.topWall= false;
        }

        if(current.col == next.col+1 && current.row == next.row){
            current.leftWall = false;
            next.righWall= false;
        }

        if(current.col == next.col-1 && current.row == next.row){
            current.righWall = false;
            next.leftWall= false;
        }
    }

    private void createMaze(){
        Stack<Cell> stack = new Stack<>();
        Cell current, next;

        cells = new Cell[COLS][ROWS];

        for(int x= 0; x<COLS; x++){
            for(int y=0;y<ROWS; y++){
                cells[x][y] = new Cell(x,y);
            }
        }

        player = cells[0][0];
        exit = cells[COLS-1][ROWS-1];

        current = cells[0][0];
        current.visited = true;

        do {
            next = getNeighbour(current);
            if (next != null) {
                removeWall(current, next);
                stack.push(current);
                current = next;
                current.visited = true;
            } else
                current = stack.pop();
        }while(!stack.isEmpty());
    }



    protected void onDraw(Canvas canvas){
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.game_bg),0,0,null);



        int width = getWidth();
        int height = getHeight();

        if(width/height < COLS/ROWS)
            cellSize = width/(COLS+1);
        else
            cellSize = height/(ROWS+1);

        hMargin = (width - COLS*cellSize)/2;
        vMargin = (height - ROWS*cellSize)/2;

        canvas.translate(hMargin,vMargin);

        for(int x= 0; x<COLS; x++){
            for(int y=0;y<ROWS; y++){
               if(cells[x][y].topWall)
                   canvas.drawLine(x*cellSize,y*cellSize, (x+1)*cellSize, y*cellSize, wallPaint);
                if(cells[x][y].leftWall)
                    canvas.drawLine(x*cellSize,y*cellSize, x*cellSize, (y+1)*cellSize, wallPaint);
                if(cells[x][y].bottomWall)
                    canvas.drawLine(x*cellSize,(y+1)*cellSize, (x+1)*cellSize, (y+1)*cellSize, wallPaint);
                if(cells[x][y].righWall)
                    canvas.drawLine((x+1)*cellSize,y*cellSize, (x+1)*cellSize, (y+1)*cellSize, wallPaint);
            }
        }

        float margin = cellSize/10;

        canvas.drawRect(
                player.col*cellSize+margin,
                player.row*cellSize+margin,
                (player.col+1)*cellSize-margin,
                (player.row+1)*cellSize-margin,
                playerPaint);

        canvas.drawRect(
                exit.col*cellSize+margin,
                exit.row*cellSize+margin,
                (exit.col+1)*cellSize-margin,
                (exit.row+1)*cellSize-margin,
                exitPaint);
    }

    public void movePlayer(Direction direction){



        switch (direction){
            case UP:
                if(!player.topWall)
                    player = cells[player.col][player.row-1];
                break;
            case DOWN:
                if(!player.bottomWall)
                    player = cells[player.col][player.row+1];
                break;
            case LEFT:
                if(!player.leftWall)
                    player = cells[player.col-1][player.row];
                break;
            case RIGHT:
                if(!player.righWall)
                    player = cells[player.col+1][player.row];
                break;
        }
        
        checkWin();
        invalidate();
    }

    private void checkWin(){
        if(player == exit) {
            if(count != 10) {
                count++;
                score += 10;
                Toast.makeText(getContext(), "Current score: " + score, Toast.LENGTH_LONG).show();
                createMaze();
            }
            else{

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            return true;

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            float x = event.getX();
            float y = event.getY();

            float playerCenterX = hMargin + (player.col+0.5f)*cellSize;
            float playerCenterY = vMargin + (player.row+0.5f)*cellSize;

            float dx = x - playerCenterX;
            float dy = y - playerCenterY;

            float absDx = Math.abs(dx);
            float absDy = Math.abs(dy);

            if(absDx > cellSize || absDy > cellSize){
                if(absDx > absDy){
                    //move x direction
                    if(dx > 0){
                        //move right
                        movePlayer(Direction.RIGHT);
                    }
                    else {
                        //move left
                        movePlayer(Direction.LEFT);
                    }
                }
                else {
                    // move y direction
                    if(dy > 0){
                        //move down
                        movePlayer(Direction.DOWN);
                    }
                    else{
                        //move up
                        movePlayer(Direction.UP);
                    }
                }
            }
            return true;
        }

        return super.onTouchEvent(event);

    }

    private class Cell{
        boolean topWall = true,
            leftWall = true,
            bottomWall = true,
            righWall = true,
            visited = false;

        int col, row;

        public Cell(int col, int row){
            this.col = col;
            this.row = row;
        }
    }


}
