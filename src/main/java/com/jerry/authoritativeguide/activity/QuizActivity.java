package com.jerry.authoritativeguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.modle.Question;

public class QuizActivity extends BaseActivity {

    private static final String TAG = "QuizActivity";

    private static final String KEY_INDEX = "key_index";
    private static final String KEY_IS_CHEATER = "key_is_cheater";
    private static final String KEY_CHEAT_RESULT_ARRAY = "key_cheat_result_array";

    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private TextView mQuestionTextView;

    private boolean mIsCheater;

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

    /**
     * 所有作弊题目的序号
     */
    private boolean[] mCheatResult = new boolean[mQuestions.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER, false);
            mCheatResult = savedInstanceState.getBooleanArray(KEY_IS_CHEATER);
        }
        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.tv_question);
        mTrueButton = (Button) findViewById(R.id.btn_true);
        mFalseButton = (Button) findViewById(R.id.btn_false);
        mCheatButton = (Button) findViewById(R.id.btn_cheat);
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
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CheatActivity.newIntent(QuizActivity.this, mQuestions[mCurrentIndex]
                        .isAnswerTrue());
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = mCurrentIndex - 1;
                // 切换到上一个问题
                mIsCheater = false;
                mCurrentIndex = index < 0 ? mQuestions.length - 1 : index;
                updateQuestion();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 切换到下一个问题
                mIsCheater = false;
                mCurrentIndex = (mCurrentIndex + 1) % mQuestions.length;
                updateQuestion();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBoolean(KEY_IS_CHEATER, mIsCheater);
        outState.putBooleanArray(KEY_CHEAT_RESULT_ARRAY, mCheatResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHEAT) {
                if (data != null) {
                    mIsCheater = wasAnswerShow(data);
                    mCheatResult[mCurrentIndex] = mIsCheater;
                }
            }
        }
    }

    /**
     * 是否已经作弊了
     *
     * @param data
     * @return
     */
    private boolean wasAnswerShow(Intent data) {
        return data.getBooleanExtra(CheatActivity.EXTRA_IS_CHEATER, false);
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
        if (mCheatResult[mCurrentIndex]) {
            msg = R.string.judgment_toast;
        } else if (isTrue != pressTrue) {
            msg = R.string.incorrect_toast;
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
