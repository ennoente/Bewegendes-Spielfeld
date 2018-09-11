package bewegendesspielfeld.com.test_1_bewegendesspielfeld;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Enno on 21.02.2017.
 */

public class Gold_Coin extends View {
    private int width = 65;
    private int height = 65;

    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);

    public Gold_Coin(Context context) {
        super(context);

        setLayoutParams(layoutParams);
        //setLayoutParams(new FrameLayout.LayoutParams(45, 45));
    }

    public int giveWidth() {
        return this.width;
    }

    public int giveHeight() {
        return this.height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;

        layoutParams.width = width;
        layoutParams.height = height;

        postInvalidate();
        //bringToFront();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint coinPaint = new Paint();
        coinPaint.setColor(Color.YELLOW);

        int drawingParameter = width / 2 - 2;

        canvas.drawCircle(drawingParameter, drawingParameter, drawingParameter, coinPaint);
    }
}
