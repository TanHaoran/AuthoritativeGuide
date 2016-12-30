package com.jerry.authoritativeguide.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.jerry.authoritativeguide.R;

public class CheatActivity extends BaseActivity {

    public static final String EXTRA_IS_ANSWER_TRUE = "com.jerry.authoritativeguide.extra_is_answer_true";
    public static final String EXTRA_IS_CHEATER = "com.jerry.authoritativeguide.extra_is_cheater";

    private static final String KEY_IS_CHEATER = "key_is_cheater";

    private TextView mAnswerTextView;
    private Button mAnswerButton;

    private boolean mIsAnswerTrue;
    private boolean mIsCheater;

    private TextView mApiLevelTextView;


    /**
     * 创建一个启动到这个Activity的Intent
     *
     * @param context
     * @param answerIsTrue
     * @return
     */
    public static Intent newIntent(Context context, boolean answerIsTrue) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_IS_ANSWER_TRUE, answerIsTrue);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if (savedInstanceState != null) {
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
            setCheatResult(mIsCheater);
        }

        mIsAnswerTrue = getIntent().getBooleanExtra(EXTRA_IS_ANSWER_TRUE, true);

        mAnswerTextView = (TextView) findViewById(R.id.tv_answer);
        mAnswerButton = (Button) findViewById(R.id.btn_answer);
        mApiLevelTextView = (TextView) findViewById(R.id.tv_api_level);

        int apiLevel = android.os.Build.VERSION.SDK_INT;
        mApiLevelTextView.setText("API level " + apiLevel);

        mAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsCheater = true;
                if (mIsAnswerTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setCheatResult(mIsCheater);

                int cx = mAnswerButton.getWidth() / 2;
                int cy = mAnswerButton.getHeight() / 2;
                float radius = mAnswerButton.getWidth();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(mAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_CHEATER, mIsCheater);
    }

    /**
     * 设置是否已经作弊
     *
     * @param isCheater
     */
    private void setCheatResult(boolean isCheater) {
        Intent data = new Intent();
        data.putExtra(EXTRA_IS_CHEATER, isCheater);
        setResult(RESULT_OK, data);
    }
}
