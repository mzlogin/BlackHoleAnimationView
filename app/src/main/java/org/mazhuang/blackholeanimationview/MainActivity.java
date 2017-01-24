package org.mazhuang.blackholeanimationview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.mazhuang.blackholeanimation.BlackHoleAnimationView;

public class MainActivity extends AppCompatActivity {

    private BlackHoleAnimationView mBlackHoleAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBlackHoleAnimationView = (BlackHoleAnimationView) findViewById(R.id.black_hole_animation);
        int[] iconResIds = new int[] {
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher,
                R.mipmap.ic_launcher
        };
        mBlackHoleAnimationView.setIcons(iconResIds);
        mBlackHoleAnimationView.startAnimation(1000L);
    }
}
