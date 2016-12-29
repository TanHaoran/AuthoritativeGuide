package com.jerry.authoritativeguide.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.modle.Question;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";

    private static final String KEY_INDEX = "key_index";

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;

    private Question[] mQuestions = new Question[]{
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    /**
     * 当前题目序号
     */
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.tv_question);
        mTrueButton = (Button) findViewById(R.id.btn_true);
        mFalseButton = (Button) findViewById(R.id.btn_false);
        mPrevButton = (ImageButton) findViewById(R.id.btn_prev);
        mNextButton = (ImageButton) findViewById(R.id.btn_next);

        updateQuestion();

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换到下一个问题
                mCurrentIndex = (mCurrentIndex + 1) % mQuestions.length;
                updateQuestion();
            }
        });
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mCurrentIndex - 1;
                // 切换到上一个问题
                mCurrentIndex = index < 0 ? mQuestions.length - 1 : index;
                updateQuestion();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换到下一个问题
                mCurrentIndex = (mCurrentIndex + 1) % mQuestions.length;
                updateQuestion();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    /**
     * 更新问题
     */
    private void updateQuestion() {
        int question = mQuestions[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    /**
     * 检测回答是否正确
     */
    private void checkAnswer(boolean pressTrue) {
        boolean isTrue = mQuestions[mCurrentIndex].isAnswerTrue();
        int msg = R.string.correct_toast;
        if (isTrue != pressTrue) {
            msg = R.string.incorrect_toast;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
