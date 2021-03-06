package smallville7123.smartclick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import screen.utils.BouncingBallActivity;
import screen.utils.ScreenUtils;

public class DemoActivity extends Activity {

    ScreenUtils SU = new ScreenUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SU.onCreate(this);

        setContentView(R.layout.activity_main);


        findViewById(R.id.StartFloatingServiceButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SU.createFloatingWidget();
            }
        });

        findViewById(R.id.BouncingBall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pIntent = new Intent(DemoActivity.this, BouncingBallActivity.class);
                pIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(pIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SU.variables.log.logMethodName();
        SU.createFloatingWidget();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SU.variables.log.logMethodName();
        SU.onActivityResult(requestCode, resultCode, data);
    }
}