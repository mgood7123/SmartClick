package screen.utils;

import android.app.Activity;
import android.os.Bundle;

import androidx.constraintlayout.motion.widget.MotionLayout;

import smallville7123.smartclick.R;

public class BouncingBallActivity extends Activity {

    MotionLayout motionLayout;

    private LogUtils log = new LogUtils(
            "BouncingBallActivity", "a bug has occurred, this should not happen"
    );

    void transition(MotionLayout motionLayout) {
        int start_state = motionLayout.getStartState();
        motionLayout.transitionToState(
                motionLayout.getCurrentState() == start_state ?
                        motionLayout.getEndState() : start_state
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bouncing_ball);
        motionLayout = findViewById(R.id.motionLayout);
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {
                transition(motionLayout);
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {
            }
        });
        transition(motionLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}