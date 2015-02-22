package ca.mcgill.amv;

/**
 * Created by Michael on 21/02/2015.
 */
import android.app.Notification;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


class Chart extends View {

    private Paint axisPaint;
    private Paint vectorPaint;

    private double x,y;


    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.BLACK);

        vectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vectorPaint.setColor(Color.RED);
        vectorPaint.setStrokeWidth(3);

    }

    public void setVector(double x, double y) {
        // Multiply by 2 to make small vectors a little more visible
        this.x = x*2;
        this.y = y*2;
        // Force redraw
        this.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int[] center = {width/2, height/2};

        // Move x, y to appropriate reference frame
        int xDraw = (int)x + center[0];
        int yDraw = center[1] - (int)y;

        // Don't draw offscreen, although that's probably not possible with the View restrctions
        if (xDraw > width) {
            xDraw = width;
        }
        else if (xDraw < 0) {
            xDraw = 0;
        }
        if (yDraw > height) {
            yDraw = height;
        }
        else if (yDraw < 0) {
            yDraw = 0;
        }

        // Draw axes + vector
        canvas.drawLine(center[0], center[1], xDraw, yDraw, vectorPaint);
        canvas.drawLine(0,center[1],width, center[1], axisPaint);
        canvas.drawLine(center[0], 0, center[0], height, axisPaint);
    }
}