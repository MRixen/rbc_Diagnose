package com.example.manuelrixen.abbtestapp.Drawing;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.example.manuelrixen.abbtestapp.BaseData;
import com.example.manuelrixen.abbtestapp.R;

import java.util.Arrays;

/*
 * Not implemented yet
 * With ths class it's possible to draw omething (like a graph for the cycle time)
 */
public class CycleTimeDrawThread extends android.view.SurfaceView implements Runnable {

    private final Context context;
    private final int MAX_X_VALUES = 10;
    private final int MAX_Y_TIME = 10; // in second
    private final int scaleXaxis, scaleYaxis;
    private float[] cycleTimeData = new float[] {2.3f, 2.3f, 2.3f, 2.3f, 2.3f, 2.3f, 2.3f, 2.3f, 2.3f, 4.0f};
    private SurfaceView surface;
    private SurfaceHolder surfaceHolder;
    private Thread drawThread;
    private Handler threadHandler;
    private Canvas canvas;
    Paint graphPaint, axisPaint, bgPaint, pointPaint, linePaint;
    private int dialogWidth, dialogHeight;
    private int[] xAxis;
    private int[] yAxis;
    private int lineStroke = 2;
    private int pointStroke = 10;
    private int borderOffset = 10;
    private int lineLengthY = 165;
    private int lineLengthX = 275;
    private int MAX_CYCLE_TIME = 4; // in second
    private Activity activity = new Activity();
    private TextView minValue, maxValue;
    private int cntr = 0;
    private float[] pointData = new float[] {0,0,0,0,0,0,0,0,0,0};

    public CycleTimeDrawThread(Context context) {
        super(context);
        this.context = context;

        scaleXaxis = lineLengthX/MAX_X_VALUES;
        scaleYaxis = lineLengthY/MAX_Y_TIME;

//        xAxis = new int[] {baseClass.calcDimDP("width", borderOffset, context),
//                                baseClass.calcDimDP("height", borderOffset, context),
//                                baseClass.calcDimDP("width", borderOffset+lineStroke, context),
//                                baseClass.calcDimDP("width", lineLengthY+borderOffset, context)};
//
//        yAxis = new int[] {baseClass.calcDimDP("width", borderOffset, context),
//                baseClass.calcDimDP("height", lineLengthY+borderOffset-lineStroke, context),
//                baseClass.calcDimDP("width", borderOffset+lineLengthX, context),
//                baseClass.calcDimDP("height", lineLengthY+borderOffset, context)};

        yAxis = new int[] {calcDimDP("width", borderOffset, context),
                calcDimDP("height", borderOffset, context),
                 calcDimDP("width", borderOffset+ lineStroke, context),
                 calcDimDP("width", lineLengthY+borderOffset, context)};

        xAxis = new int[] {calcDimDP("width", borderOffset, context),
                calcDimDP("height", lineLengthY+borderOffset, context),
                calcDimDP("width", borderOffset+lineLengthX, context),
                calcDimDP("height", lineLengthY+borderOffset, context)};
    }

    public void init() {
        graphPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        graphPaint.setStyle(Paint.Style.FILL);
        graphPaint.setColor(this.getResources().getColor(R.color.Red));
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setStyle(Paint.Style.FILL);
        axisPaint.setStrokeWidth(lineStroke);
        axisPaint.setColor(this.getResources().getColor(R.color.Black));
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(lineStroke);
        linePaint.setColor(this.getResources().getColor(R.color.ModernOrange));
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(pointStroke);
        pointPaint.setColor(this.getResources().getColor(R.color.ModernOrange));
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(this.getResources().getColor(R.color.White));
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
    }

    public void stopDrawThread(){
        try {
            threadHandler.getLooper().quit();
            threadHandler = null;
            // Blocks drawThread until all operations are finished
            drawThread.join();
        }catch(Exception e){
            Log.d("CycleTimeDrawT:init(): ", String.valueOf(e));
        }
        drawThread = null;
    }

    public void startDrawThread(SurfaceView surface, int dialogWidth, int dialogHeight, TextView textFieldMinCycleTime, TextView textFieldMaxCycleTime){
        this.surface = surface;
        this.dialogWidth = dialogWidth;
        this.dialogHeight = dialogHeight;
        this.minValue = textFieldMinCycleTime;
        this.maxValue = textFieldMaxCycleTime;

        stopDrawThread();


        // Set surfaceHolder
        this.surface.setZOrderOnTop(false);
        surfaceHolder = this.surface.getHolder();

        init();

        // Stop last draw drawThread and execute a new one
        if (drawThread == null) {
            drawThread = new Thread(this);
            drawThread.start();
        }
    }

    @Override
    public void run() {
        long timeElapsed;
        long startTime = System.nanoTime();

        while (!surfaceHolder.getSurface().isValid()) {
            timeElapsed = (System.nanoTime() - startTime) / 1000000000;
            if (timeElapsed >= 20) {
                break;
            }
        }

        Looper.prepare();
        threadHandler = new Handler();

//        try {
//            canvas = surfaceHolder.lockCanvas();
//            drawLayout(true, 0);
//            surfaceHolder.unlockCanvasAndPost(canvas);
//        }
//        catch(Exception e){
//            Log.e("@GlobalLayout#run: ", "Exception: " + e);
//        }

        // Handler to receive and visualize sensor data
        BaseData.sendToVisualization = new Handler() {
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();

                float[] data;

                if (bundle != null) {
                    if(bundle.containsKey("cycleTimeData")) {
                        data = bundle.getFloatArray("cycleTimeData");
                            try {
                                canvas = surfaceHolder.lockCanvas();
                                drawLayout(false, data);
                                surfaceHolder.unlockCanvasAndPost(canvas);
                            } catch (Exception e) {
                                Log.e("handleMessage: ", "Exception: " + e);
                            }
                    }
                }
            }
        };
        Looper.loop();
    }

    public int calcDimDP(String dimType, int dimDp, Context context){

        // Calculate display size
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        int pxWidth = displayMetrics.widthPixels;
        int pxHeight = displayMetrics.heightPixels;
        float dpWidth = pxWidth / displayMetrics.density;
        float dpHeight = pxHeight / displayMetrics.density;

        // Factor dp to px
        float factorHeight = (pxHeight / dpHeight);
        float factorWidth = (pxWidth / dpWidth);
        Log.d("factorHeight", String.valueOf(factorHeight));
        Log.d("factorWidth", String.valueOf(factorWidth));

        if (dimType.equals("width")) return Math.round(factorWidth*dimDp);
        if (dimType.equals("height")) return Math.round(factorHeight*dimDp);
        else return 0;
    }

    public void drawLayout(boolean showBackground, float[] yData) {
//        Paint paint = new Paint();
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        canvas.drawPaint(paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        // Draw axis
        canvas.drawLine(yAxis[0], yAxis[1], yAxis[0], yAxis[3], axisPaint);
        canvas.drawLine(xAxis[0], xAxis[1], xAxis[2], xAxis[1], axisPaint);
        // Draw red line for max limit of cycle time
        canvas.drawLine(borderOffset, ((MAX_CYCLE_TIME*scaleYaxis)+borderOffset), xAxis[2]+borderOffset, ((MAX_CYCLE_TIME*scaleYaxis)+borderOffset), graphPaint);

        for (int i=0;i<=MAX_X_VALUES-1;i++){
            if (yData[i] != 0) canvas.drawPoint(((i*scaleXaxis)+borderOffset+(pointStroke/2)), ((yData[i]*scaleYaxis)+borderOffset+(pointStroke/2)), pointPaint);
            if (i<yData.length-1 && yData[i] != 0) canvas.drawLine(((i*scaleXaxis)+borderOffset+(pointStroke/2)), ((yData[i]*scaleYaxis)+borderOffset+(pointStroke/2)), (((i+1)*scaleXaxis)+borderOffset+(pointStroke/2)), ((pointData[i+1]*scaleYaxis)+borderOffset+(pointStroke/2)), linePaint);
        }

        // Draw cycle time data
//        for (int i=0;i<=cycleTimeData.length-1;i++) {
//            canvas.drawPoint(((i*scaleXaxis)+borderOffset+(pointStroke/2)), ((cycleTimeData[i]*scaleYaxis)+borderOffset+(pointStroke/2)), pointPaint);
//            if (i<cycleTimeData.length-1) canvas.drawLine(((i*scaleXaxis)+borderOffset+(pointStroke/2)), ((cycleTimeData[i]*scaleYaxis)+borderOffset+(pointStroke/2)), (((i+1)*scaleXaxis)+borderOffset+(pointStroke/2)), ((cycleTimeData[i+1]*scaleYaxis)+borderOffset+(pointStroke/2)), linePaint);
//        }

        // Get min / max values of cycle data
        final float[] pointDataTemp = yData;
        Arrays.sort(pointDataTemp);
        Log.d("pointDataTemp", String.valueOf(pointDataTemp[0]));

        // Show min / max values of cycle time
        activity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                minValue.setText("Min.: " + String.valueOf(pointDataTemp[0]));
                maxValue.setText("Max.: " + String.valueOf(pointDataTemp[pointDataTemp.length - 1]));
            }
        });

    }
}
