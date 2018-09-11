package bewegendesspielfeld.com.test_1_bewegendesspielfeld;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.View;
import android.graphics.Canvas;
import android.widget.FrameLayout;

/**
 * Created by Enno on 17.02.2017.
 */

public class Test_EButton extends View {
    Rect border;
    Paint borderPaint;

    Rect body;
    Paint bodyPaint;

    Paint textPaint;

    Typeface font = Typeface.create("Tahoma", Typeface.BOLD);

    String text = "Button";
    Float textSize = 24f;

    int width = 100;
    int height = 45;

    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);

    public Test_EButton(Context context) {
        super(context);
        this.setLayoutParams(layoutParams);
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void setSize(int width, int height) {
        //layoutParams.width = width;
        //layoutParams.height = height;

        this.width = width;
        this.height = height;

        layoutParams.width = this.width;
        layoutParams.height = this.height;

        invalidate();
        bringToFront();
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        border = new Rect();
        border.set(0, 0, width, height);
        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(7);
        borderPaint.setColor(Color.parseColor("#125256"));

        Float strokeWidthFloat = borderPaint.getStrokeWidth();
        int strokeWidth = strokeWidthFloat.intValue();

        body = new Rect();
        body.set(strokeWidth / 2, strokeWidth / 2, width - strokeWidth / 2, height - strokeWidth / 2);
        bodyPaint = new Paint();
        bodyPaint.setColor(Color.WHITE);

        Rect textBounds = new Rect();
        textPaint = new Paint();
        textPaint.setColor(Color.rgb(12, 52, 56));
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(font);
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        textPaint.setAntiAlias(true);

        int boundsTop = (int) textPaint.getFontMetrics().top;
        int boundsBottom = (int) textPaint.getFontMetrics().bottom;

        int textWidth = textBounds.width();
        int textHeight = textBounds.height() + boundsTop - boundsBottom;

        canvas.drawRect(border, borderPaint);
        canvas.drawRect(body, bodyPaint);

        canvas.drawText(text, (width - textWidth) / 2, (height - textHeight) / 2, textPaint);

        System.out.println("textsachen:" + textWidth + "|" + textHeight + "||" + textHeight);
    }
}
