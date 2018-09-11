package bewegendesspielfeld.com.test_1_bewegendesspielfeld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.icu.text.DisplayContext;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Enno on 17.02.2017.
 */

public class GameOver extends View {
    private int width;
    private int height;

    private final int X;
    private final int Y;
    private final int CENTER_HOR;
    private final int CENTER_VER;

    public String statusText = "Game Over!";

    public Test_EButton restart;

    public GameOver(Context context) {
        super(context);

        this.width = (int) (MainActivity.width / 1.2);
        this.height = (MainActivity.height / 2);

        setLayoutParams(new FrameLayout.LayoutParams(width, height));

        setX((MainActivity.width - width) / 2);
        setY((MainActivity.height - height) / 2);

        X = (int) getX();
        Y = (int) getY();

        CENTER_HOR = width / 2 + X;
        CENTER_VER = height / 2 + Y;

        restart = new Test_EButton(context);
        restart.setSize(330, 100);
        restart.setText("RESTART");
        restart.setX(CENTER_HOR - (restart.width() / 2));
        restart.setY(CENTER_VER + (restart.height() / 2));

        System.out.println(restart.width() + "|" + restart.height());
        System.out.println(CENTER_HOR + "|" + CENTER_VER);

        restart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ASDF YAYAYAAY");
            }
        });

        MainActivity.frameLayout.addView(this);
        MainActivity.frameLayout.addView(restart);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect borders = new Rect();
        Paint bordersPaint = new Paint();

        bordersPaint.setColor(Color.parseColor("#125256"));
        bordersPaint.setStrokeWidth(15);
        bordersPaint.setStyle(Paint.Style.STROKE);

        int strokeWidth = (int) bordersPaint.getStrokeWidth();
        borders.set(0, 0, width, height);

        Rect body = new Rect();
        Paint bodyPaint = new Paint();

        bodyPaint.setColor(Color.parseColor("#ecf0f1"));
        bodyPaint.setStyle(Paint.Style.FILL);

        body.set(strokeWidth / 2, strokeWidth / 2, width - strokeWidth / 2, height - strokeWidth / 2);

        canvas.drawRect(borders, bordersPaint);
        canvas.drawRect(body, bodyPaint);

        Paint textPaint = new Paint();
        textPaint.setTextSize(70);
        textPaint.setTypeface(Typeface.create("Helvetica", Typeface.BOLD));
        textPaint.setColor(Color.parseColor("#125256"));
        textPaint.setLetterSpacing(0.1f);
        textPaint.setAntiAlias(true);

        Rect textBounds = new Rect();
        textPaint.getTextBounds(statusText, 0, statusText.length(), textBounds);
        int textWidth = textBounds.width();

        canvas.drawText(statusText, (width - textWidth) / 2, textPaint.getTextSize() + bordersPaint.getStrokeWidth(), textPaint);

        Paint scoreTextPaint = new Paint();
        scoreTextPaint.setColor(Color.parseColor("#125256"));
        scoreTextPaint.setLetterSpacing(0.2f);
        scoreTextPaint.setTextSize(80);
        scoreTextPaint.setUnderlineText(true);
        scoreTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        scoreTextPaint.setAntiAlias(true);

        String score = "" + MainActivity.score;

        Rect scoreTextBounds = new Rect();
        scoreTextPaint.getTextBounds(score, 0, score.length(), scoreTextBounds);
        int scoreTextWidth = scoreTextBounds.width();



        canvas.drawText(score, (width - scoreTextWidth) / 2, height / 2.7f, scoreTextPaint);
    }
}
