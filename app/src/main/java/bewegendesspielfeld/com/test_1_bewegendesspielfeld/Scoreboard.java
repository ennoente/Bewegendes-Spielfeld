package bewegendesspielfeld.com.test_1_bewegendesspielfeld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Enno on 18.02.2017.
 */

public class Scoreboard extends View {
    private int width;
    private int height;

    private String scoreText = "" + MainActivity.score;

    public Scoreboard(Context context, int width, int height) {
        super(context);

        this.width = width;
        this.height = height;

        setLayoutParams(new FrameLayout.LayoutParams(width, height));
    }

    public int giveHeight() {
        return height;
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Paint textPaint = new Paint();
        textPaint.setTextSize(30);
        textPaint.setTypeface(Typeface.create("Tahoma", Typeface.BOLD));
        textPaint.setColor(Color.parseColor("#125256"));
        textPaint.setAntiAlias(true);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(scoreText, 0, scoreText.length(), textBounds);
        int textWidth = textBounds.width();

        Rect borders = new Rect();
        Paint bordersPaint = new Paint();
        bordersPaint.setStrokeWidth(3);
        bordersPaint.setColor(Color.parseColor("#125256"));
        bordersPaint.setStyle(Paint.Style.STROKE);

        int strokeWidth = (int) bordersPaint.getStrokeWidth();

        borders.set(strokeWidth / 2, strokeWidth / 2, width - strokeWidth / 2, height - strokeWidth / 2);

        canvas.drawRect(borders, bordersPaint);
        canvas.drawText("Score: " + MainActivity.score, 15, 45, textPaint);
        canvas.drawText("Coins collected: " + MainActivity.coinScore, width / 2 + 15, 45, textPaint);
    }
}
