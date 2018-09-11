package bewegendesspielfeld.com.test_1_bewegendesspielfeld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.Button;

/**
 * Created by Enno on 13.02.2017.
 */

public class Game extends View {
    public Rect obstacle;

    /*
    The paint obstaclePaint is used for all obstacle rectangles
    since they all look the same
     */
    public Paint obstaclePaint;

    private int height;
    private int width = 25;

    public Game(Context context, int height) {
        super(context);

        this.height = height;

        obstacle = new Rect();
        obstaclePaint = new Paint();
    }

    public int giveHeight() {
        return this.height;
    }

    public int giveWidth() {
        return this.width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        obstacle.set(0, 0, width, height);

        obstaclePaint.setStyle(Paint.Style.STROKE);
        obstaclePaint.setColor(Color.parseColor("#125256"));
        obstaclePaint.setStrokeWidth(4.5f);
        obstaclePaint.setAntiAlias(true);

        canvas.drawRect(obstacle, obstaclePaint);
    }
}
