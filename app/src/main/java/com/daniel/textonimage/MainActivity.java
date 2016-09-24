package com.daniel.textonimage;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.cover_caption)
    protected EditText coverCaption;
    @BindView(R.id.cover_photo)
    protected ImageView coverPhoto;
    @BindView(R.id.result_photo)
    protected ImageView resultPhoto;

    protected float dX, dY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.submit_button)
    protected void submitOnClick() {
        // Get position of Cover Caption on Screen
        int[] coverCaptionXY = new int[2];
        this.coverCaption.getLocationOnScreen(coverCaptionXY);
        // Get position of Cover Photo on Screen
        int[] coverPhotoXY = new int[2];
        this.coverPhoto.getLocationOnScreen(coverPhotoXY);

        // (x,y) is top-left point of Cover Caption
        int x = coverCaptionXY[0] - coverPhotoXY[0];
        int y = coverCaptionXY[1] - coverPhotoXY[1];

        Bitmap resultBitmap = this.drawTextToBitmap(
            this.coverCaption.getText().toString(), R.drawable.bg_image, x, y);
        this.resultPhoto.setImageBitmap(resultBitmap);
    }

    @OnTouch(R.id.cover_caption)
    protected boolean coverCaptionOnTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.dX = view.getX() - event.getRawX();
                this.dY = view.getY() - event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                view.animate()
                    .x(event.getRawX() + this.dX)
                    .y(event.getRawY() + this.dY)
                    .setDuration(0)
                    .start();
                break;
            case MotionEvent.ACTION_UP:
                this.dX = event.getRawX();
                this.dY = event.getRawY();
                return false;
            default:
                return false;
        }
        return true;
    }

    public Bitmap drawTextToBitmap(String gText, int resId, float x, float y) {
        Resources resources = super.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
        // Calculate scale between bitmap and photo
        float scale = bitmap.getWidth() / (float) this.resultPhoto.getWidth();

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(this.coverCaption.getCurrentTextColor());
        float textSize = super.getResources().getDimensionPixelSize(R.dimen.text_size);
        paint.setTextSize(textSize * scale);

        x = x * scale;
        y = (y + this.coverCaption.getBaseline()) * scale;
        canvas.drawText(gText, x, y, paint);

        return bitmap;
    }
}