package com.tf.surfaceviewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import common.MyConstant;

public class MySurfaceView  extends SurfaceView
                            implements SurfaceHolder.Callback {

    SurfaceHolder g_holder;

    //공의좌표 및 크기정보
    int ball_radius=30;
    int ball_x = MyConstant.GAMEPAN_WIDTH/2;
    int ball_y = MyConstant.GAMEPAN_HEIGHT/2;
    //공의 이동방향 결정하는 변수
    boolean bRight = true;
    boolean bDown  = true;


    //Handler를 Timer용도로 변형해서 사용함
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            //Log.d("MY","--timer--");
            //좌표변환등의 작업
            process();
            //그려라..
            draw();
            //1000(1초)이후에 메시지 전송시켜라..
            this.sendEmptyMessageDelayed(0,1);

        }
    };

    //공의 이동좌표 계산...
    void process(){

        //공의 위치 계산
        move_ball();

    }

    int gan = 10;
    void move_ball(){

        //가로방향 이동
        if(bRight)
            ball_x += gan;
        else
            ball_x -= gan;

        //오른쪽으로 나갔냐?
        if( (ball_x + ball_radius) > MyConstant.GAMEPAN_WIDTH){
            ball_x = MyConstant.GAMEPAN_WIDTH - ball_radius;
            bRight = false;
        }else if((ball_x-ball_radius)<0){//왼쪽벽에 닿았냐?
            //ball_x = 0;
            bRight=true;
        }

        //세로방향
        if(bDown)
            ball_y += gan;
        else
            ball_y -= gan;

        //아래로 나갔냐?
        if( (ball_y + ball_radius) > MyConstant.GAMEPAN_HEIGHT){
            ball_y = MyConstant.GAMEPAN_HEIGHT - ball_radius;
            bDown = false;
        }else if((ball_y-ball_radius)<0){//윗쪽벽에 닿았냐?
            //ball_y = 0;
            bDown=true;
        }



    }

    //현재 화면 그리는 작업
    void draw(){

        Canvas canvas = g_holder.lockCanvas();
        //이전화면 지우기
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0,0,MyConstant.GAMEPAN_WIDTH,MyConstant.GAMEPAN_HEIGHT,paint);

        //공그리기
        paint.setColor(Color.RED);
        canvas.drawCircle(ball_x,ball_y,ball_radius,paint);



        g_holder.unlockCanvasAndPost(canvas);

    }

    public MySurfaceView(Context context) {
        super(context);

        g_holder = getHolder();
        g_holder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d("MY","--surfaceCreated()--");

        //화면 초기화
        Canvas canvas = holder.lockCanvas();
        int w = canvas.getWidth();
        int h = canvas.getHeight();
        //물리적장치크기의 가로세로 비율
        double rate = (double)h/w;
        //가로세로 비율에 따른 논리적 화면높이 설정
        MyConstant.GAMEPAN_HEIGHT = (int)(MyConstant.GAMEPAN_WIDTH * rate);
        holder.unlockCanvasAndPost(canvas);

        //현재뷰의  논리적 크기설정
        holder.setFixedSize(MyConstant.GAMEPAN_WIDTH,MyConstant.GAMEPAN_HEIGHT);

        //화면지우기
        canvas = holder.lockCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0,0,MyConstant.GAMEPAN_WIDTH,MyConstant.GAMEPAN_HEIGHT,paint);
        //사용종료후에는 unlock
        holder.unlockCanvasAndPost(canvas);


        //handler메세시 전송
        handler.sendEmptyMessage(0);


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        String str = String.format("--surfaceChanged() w:%d h:%d--",width,height);
        Log.d("MY",str);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("MY","--surfaceDestroyed()--");

        //핸들러 제거
        handler.removeMessages(0);

    }
}
