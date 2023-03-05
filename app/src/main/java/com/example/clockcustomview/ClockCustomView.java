package com.example.clockcustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class ClockCustomView extends View {
    //region Переменные с параметрами часов
    float ClockTotalRadius, ClockFrameRadius, NumberSize, FrameToDividerDistance,
            DividerToNumberDistance, DividerDefaultRadius, DividerWithNumberRadius, HourHandLength,
            HourHandWidth, MinuteHandLength, MinuteHandWidth, SecondHandLength, SecondHandWidth;
    String ClockTimeZone;
    //endregion
    //region Переменные с заданными параметрами часов
    float customClockTotalRadius, customClockFrameRadius, customNumberSize, customFrameToDividerDistance,
            customDividerToNumberDistance, customDividerDefaultRadius, customDividerWithNumberRadius,
            customHourHandLength, customHourHandWidth, customMinuteHandLength, customMinuteHandWidth,
            customSecondHandLength, customSecondHandWidth;
    String customClockTimeZone;
    //endregion
    //region Цвета и кисти
    int ClockInnerColor, ClockFrameColor, NumberColor, DividerDefaultColor,
            DividerWithNumberColor, HourHandColor, MinuteHandColor, SecondHandColor;
    Paint NumberPaint, InnerClockPaint, HourHandPaint, MinuteHandPaint, SecondHandPaint,
            FramePaint, DividerDefaultPaint, DividerWithNumberPaint;
    //endregion
    Rect tempNumberBounds; //переменная для выставления границ чисел при отрисовке
    int savedState;

    public ClockCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                invalidate();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);

        try {
            initCustomAttributes(context, attrs);
        } finally {
            Log.e("Передача данных", "custom");
        }
        initPaint();
        tempNumberBounds = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        //Диаметр часов будет равен минимальному из ширины/высоты View или подстроится под заданный программно радиус
        if (customClockTotalRadius == 0) {
            ClockTotalRadius = Math.min(widthSpecSize, heightSpecSize) / 2f;
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        } else {
            setMeasuredDimension((int)customClockTotalRadius * 2, (int)customClockTotalRadius * 2);
        }
        initClockSize();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(ClockTotalRadius, ClockTotalRadius);
        canvas.drawCircle(0, 0, ClockTotalRadius, FramePaint);
        canvas.drawCircle(0, 0, ClockTotalRadius - ClockFrameRadius, InnerClockPaint);

        float distanceToDivider = ClockTotalRadius - (ClockFrameRadius + FrameToDividerDistance);
        float distanceToNumber = ClockTotalRadius - (ClockFrameRadius + FrameToDividerDistance + DividerToNumberDistance);

        String text; //переменная для чисел
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                canvas.drawCircle(
                        distanceToDivider * (float) Math.cos(i * Math.PI / 30),
                        distanceToDivider * (float) Math.sin(i * Math.PI / 30),
                        DividerWithNumberRadius, DividerWithNumberPaint);
                if (i == 45)
                    text = "12";
                else text = Integer.toString(((i / 5) + 3) % 12); //+3 т.к. положительные xy начинаются с 90 градусов

                NumberPaint.getTextBounds(text, 0, text.length(), tempNumberBounds);
                canvas.drawText(
                        text,
                        (distanceToNumber) * (float) Math.cos(i * Math.PI / 30),
                        (distanceToNumber) * (float) Math.sin(i * Math.PI / 30)
                                + tempNumberBounds.height() / 2f, //выравниваем числа относительно центра
                        NumberPaint);
            }
            else
                canvas.drawCircle(
                        distanceToDivider * (float)Math.cos(i * Math.PI / 30),
                        distanceToDivider * (float)Math.sin(i * Math.PI / 30),
                        DividerDefaultRadius, DividerDefaultPaint);
        }

        Calendar calendar = Calendar.getInstance();
        if (customClockTimeZone != null && !customClockTimeZone.equals(""))
            calendar.setTimeZone(TimeZone.getTimeZone(ClockTimeZone));
        else calendar.setTimeZone(TimeZone.getDefault());

        int currentHours = calendar.get(Calendar.HOUR);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentSecond = calendar.get(Calendar.SECOND);

        //корректные xy координаты для отрисовки движения стрелок
        double currentSecondHandAngle = currentSecond * Math.PI / 30;
        double currentMinuteHandAngle = currentMinute * Math.PI / 30 + currentSecondHandAngle / 60;
        double currentHourHandAngle = 5 * (currentHours * Math.PI / 30 + currentMinuteHandAngle / 60 + currentSecondHandAngle / 3600);

        float currentSecondHandEndX = SecondHandLength * (float)Math.cos(currentSecondHandAngle);
        float currentSecondHandEndY = SecondHandLength * (float)Math.sin(currentSecondHandAngle);
        float currentMinuteHandEndX = MinuteHandLength * (float)Math.cos(currentMinuteHandAngle);
        float currentMinuteHandEndY = MinuteHandLength * (float)Math.sin(currentMinuteHandAngle);
        float currentHourHandEndX = HourHandLength * (float)Math.cos(currentHourHandAngle);
        float currentHourHandEndY = HourHandLength * (float)Math.sin(currentHourHandAngle);;

        canvas.rotate(-90);
        canvas.drawLine(0, 0,
                currentHourHandEndX,
                currentHourHandEndY,
                HourHandPaint);
        canvas.drawLine(0, 0,
                currentMinuteHandEndX,
                currentMinuteHandEndY,
                MinuteHandPaint);
        canvas.drawLine(0, 0,
                currentSecondHandEndX,
                currentSecondHandEndY,
                SecondHandPaint);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("SavedState", this.savedState);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            this.savedState = bundle.getInt("SavedState");
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    protected void initClockSize() {
        ClockTotalRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, ClockTotalRadius,
                getContext().getResources().getDisplayMetrics());

        //заданные программно значения, либо значения по умолчанию
        if (customClockTotalRadius != 0)
             ClockTotalRadius = customClockTotalRadius;
        if (customClockFrameRadius == 0)
            ClockFrameRadius = 0.075f * ClockTotalRadius;
        else ClockFrameRadius = customClockFrameRadius;
        if (customNumberSize == 0)
            NumberSize = 0.25f * ClockTotalRadius;
        else NumberSize = customNumberSize;
        if (customFrameToDividerDistance == 0)
            FrameToDividerDistance = 0.04f * ClockTotalRadius;
        else FrameToDividerDistance = customFrameToDividerDistance;
        if (customDividerToNumberDistance == 0)
            DividerToNumberDistance = 0.185f * ClockTotalRadius;
        else DividerToNumberDistance = customDividerToNumberDistance;
        if (customDividerDefaultRadius == 0)
            DividerDefaultRadius = 0.015f * ClockTotalRadius;
        else DividerDefaultRadius = customDividerDefaultRadius;
        if (customDividerWithNumberRadius == 0)
            DividerWithNumberRadius = 0.025f * ClockTotalRadius;
        else DividerWithNumberRadius = customDividerWithNumberRadius;
        if (customHourHandLength == 0)
            HourHandLength = 0.475f * ClockTotalRadius;
        else HourHandLength = customHourHandLength;
        if (customHourHandWidth == 0)
            HourHandWidth = 0.05f * ClockTotalRadius;
        else HourHandWidth = customHourHandWidth;
        if (customMinuteHandLength == 0)
            MinuteHandLength = 0.7f * ClockTotalRadius;
        else MinuteHandLength = customMinuteHandLength;
        if (customMinuteHandWidth == 0)
            MinuteHandWidth = 0.025f * ClockTotalRadius;
        else MinuteHandWidth = customMinuteHandWidth;
        if (customSecondHandLength == 0)
            SecondHandLength = 0.9f * ClockTotalRadius;
        else SecondHandLength = customSecondHandLength;
        if (customSecondHandWidth == 0)
            SecondHandWidth = 0.0125f * ClockTotalRadius;
        else SecondHandWidth = customSecondHandWidth;
        if (customClockTimeZone == null || customClockTimeZone.equals(""))
            ClockTimeZone = "";
        else ClockTimeZone = customClockTimeZone;

        initPaint();
    }

    protected void initPaint() {
        NumberPaint = new Paint();
        NumberPaint.setColor(Color.BLACK);
        NumberPaint.setTextSize(NumberSize);
        NumberPaint.setTextAlign(Paint.Align.CENTER);

        FramePaint = new Paint();
        FramePaint.setColor(ClockFrameColor);

        DividerDefaultPaint = new Paint();
        DividerDefaultPaint.setColor(DividerDefaultColor);

        DividerWithNumberPaint = new Paint();
        DividerWithNumberPaint.setColor(DividerWithNumberColor);

        InnerClockPaint = new Paint();
        InnerClockPaint.setColor(ClockInnerColor);

        HourHandPaint = new Paint();
        HourHandPaint.setColor(HourHandColor);
        HourHandPaint.setStrokeWidth(HourHandWidth);

        MinuteHandPaint = new Paint();
        MinuteHandPaint.setColor(MinuteHandColor);
        MinuteHandPaint.setStrokeWidth(MinuteHandWidth);

        SecondHandPaint = new Paint();
        SecondHandPaint.setColor(SecondHandColor);
        SecondHandPaint.setStrokeWidth(SecondHandWidth);
    }

    private void initCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClockCustomView);
        customClockTotalRadius = a.getDimension(R.styleable.ClockCustomView_ClockTotalRadius, 0);
        customClockFrameRadius = a.getDimension(R.styleable.ClockCustomView_ClockFrameRadius, 0);
        customNumberSize = a.getDimension(R.styleable.ClockCustomView_NumberSize, 0);
        customFrameToDividerDistance = a.getDimension(R.styleable.ClockCustomView_FrameToDividerDistance, 0);
        customDividerToNumberDistance = a.getDimension(R.styleable.ClockCustomView_DividerToNumberDistance, 0);
        customDividerDefaultRadius = a.getDimension(R.styleable.ClockCustomView_DividerDefaultRadius, 0);
        customDividerWithNumberRadius = a.getDimension(R.styleable.ClockCustomView_DividerWithNumberRadius, 0);
        customHourHandLength = a.getDimension(R.styleable.ClockCustomView_HourHandLength, 0);
        customHourHandWidth = a.getDimension(R.styleable.ClockCustomView_HourHandWidth, 0);
        customMinuteHandLength = a.getDimension(R.styleable.ClockCustomView_MinuteHandLength, 0);
        customMinuteHandWidth = a.getDimension(R.styleable.ClockCustomView_MinuteHandWidth, 0);
        customSecondHandLength = a.getDimension(R.styleable.ClockCustomView_SecondHandLength, 0);
        customSecondHandWidth = a.getDimension(R.styleable.ClockCustomView_SecondHandWidth, 0);

        ClockInnerColor = a.getColor(R.styleable.ClockCustomView_ClockInnerColor, Color.WHITE);
        ClockFrameColor = a.getColor(R.styleable.ClockCustomView_ClockFrameColor, Color.BLACK);
        NumberColor = a.getColor(R.styleable.ClockCustomView_NumberColor, Color.BLACK);
        DividerDefaultColor = a.getColor(R.styleable.ClockCustomView_DividerDefaultColor, Color.BLACK);
        DividerWithNumberColor = a.getColor(R.styleable.ClockCustomView_DividerWithNumberColor, Color.BLACK);
        HourHandColor = a.getColor(R.styleable.ClockCustomView_HourHandColor, Color.BLACK);
        MinuteHandColor = a.getColor(R.styleable.ClockCustomView_MinuteHandColor, Color.BLACK);
        SecondHandColor = a.getColor(R.styleable.ClockCustomView_SecondHandColor, Color.BLACK);

        customClockTimeZone = a.getString(R.styleable.ClockCustomView_TimeZone);

        initClockSize();

        a.recycle();
    }
}
