package bewegendesspielfeld.com.test_1_bewegendesspielfeld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Enno on 14.02.2017.
 */

public class Figure extends View {
    Rect figure = new Rect();
    Paint figurePaint = new Paint();

    private int width = 62;
    private int height = 62;

    public Figure(Context context) {
        super(context);
        setLayoutParams(new FrameLayout.LayoutParams(width, height));
    }

    public int giveWidth() {
        return width;
    }

    public int giveHeight() {
        return height;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //figure = new Rect(0, 0, 60, 60);

        figurePaint.setStrokeWidth(3.5f);
        figurePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        figurePaint.setColor(Color.rgb(12, 52, 56));
        figurePaint.setAntiAlias(true);

        //canvas.drawRect(figure, figurePaint);
        canvas.drawCircle(31, 31, 30, figurePaint);
    }
}
