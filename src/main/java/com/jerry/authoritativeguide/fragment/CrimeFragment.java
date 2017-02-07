package com.jerry.authoritativeguide.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.activity.BaseActivity;
import com.jerry.authoritativeguide.activity.DatePickerActivity;
import com.jerry.authoritativeguide.activity.PhotoActivity;
import com.jerry.authoritativeguide.activity.TimePickerActivity;
import com.jerry.authoritativeguide.modle.Crime;
import com.jerry.authoritativeguide.permission.PermissionListener;
import com.jerry.authoritativeguide.util.ActivityCollector;
import com.jerry.authoritativeguide.util.CrimeLab;
import com.jerry.authoritativeguide.util.DeviceUtil;
import com.jerry.authoritativeguide.util.PictureUtil;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Jerry on 2016/12/30.
 */

public class CrimeFragment extends Fragment {

    public interface CallBack {
        /**
         * 在修改完详细界面的Crime之后，要更新列表界面
         *
         * @param crime
         */
        void onCrimeUpdate(Crime crime);
    }

    private static final String TAG = "CrimeFragment";

    private static final String ARGS_CRIME_ID = "args_crime_id";

    private static final String DIALOG_DATE = "dialog_date";
    private static final String DIALOG_PHOTO = "dialog_photo";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_PHOTO = 3;


    private Crime mCrime;

    private File mPhotoFile;

    private EditText mTitleEditText;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;

    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private Button mReportButton;

    private ImageView mPhotoImageView;
    private ImageButton mCameraButton;

    private CallBack mCallBack;

    /**
     * 通过陋习id创建一个自己的实例
     *
     * @param crimeId
     * @return
     */
    public static Fragment newInstance(UUID crimeId) {
        // 保存陋习id
        Bundle args = new Bundle();
        args.putSerializable(ARGS_CRIME_ID, crimeId);

        // 创建实例
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 以动画的方式过度
     *
     * @param activity
     * @param intent
     * @param sourceView
     */
    public static void startWithTransition(Activity activity, Intent intent, View sourceView) {
        ViewCompat.setTransitionName(sourceView, "image");

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, sourceView, "image");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.startActivity(intent, options.toBundle());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // 这里通过Arguments来获取陋习id，从而脱离的activity的限制
        UUID crimeId = (UUID) getArguments().getSerializable(ARGS_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        setResult();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleEditText = (EditText) v.findViewById(R.id.et_title);
        mDateButton = (Button) v.findViewById(R.id.btn_date);
        mTimeButton = (Button) v.findViewById(R.id.btn_time);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.cb_solved);

        mSuspectButton = (Button) v.findViewById(R.id.btn_suspect);
        mCallSuspectButton = (Button) v.findViewById(R.id.btn_call_suspect);
        mReportButton = (Button) v.findViewById(R.id.btn_report);

        mPhotoImageView = (ImageView) v.findViewById(R.id.iv_photo);
        mCameraButton = (ImageButton) v.findViewById(R.id.ib_camera);

        // 设置标题
        mTitleEditText.setText(mCrime.getTitle());
        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 设置日期
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DeviceUtil.isPad(getActivity())) {
                    DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());
                    datePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    datePickerFragment.show(getFragmentManager(), DIALOG_DATE);
                } else {
                    Intent intent = DatePickerActivity.newIntent(getActivity(), mCrime.getDate());
                    startActivityForResult(intent, REQUEST_DATE);
                }
            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DeviceUtil.isPad(getActivity())) {
                    TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(mCrime.getDate());
                    timePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    timePickerFragment.show(getFragmentManager(), DIALOG_DATE);
                } else {
                    Intent intent = TimePickerActivity.newIntent(getActivity(), mCrime.getDate());
                    startActivityForResult(intent, REQUEST_DATE);
                }
            }
        });

        // 设置是否解决
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        // 嫌疑人
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        final Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(contactIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity activity = (BaseActivity) getActivity();
                activity.requestRuntimePermission(new String[]{Manifest.permission.READ_CONTACTS},
                        new PermissionListener() {
                            @Override
                            public void onGranted() {
                                startActivityForResult(contactIntent, REQUEST_CONTACT);
                            }

                            @Override
                            public void onDenied(List<String> deniedPermissions) {

                            }
                        });
            }
        });

        // 打嫌疑人电话
        if (mCrime.getSuspectPhone() != null) {
            mCallSuspectButton.setText(mCrime.getSuspectPhone());
            mCallSuspectButton.setEnabled(true);
        } else {
            mCallSuspectButton.setText(R.string.crime_call_suspect);
            mCallSuspectButton.setEnabled(false);
        }
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CALL_PHONE},
                        new PermissionListener() {
                            @Override
                            public void onGranted() {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + mCrime.getSuspectPhone()));
                                startActivity(intent);
                            }

                            @Override
                            public void onDenied(List<String> deniedPermissions) {

                            }
                        });
            }
        });

        // 发送报告
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });

        // 设置图片
        ViewTreeObserver viewTreeObserver = mPhotoImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.i(TAG, "全局更新照片");
                updatePhotoView();
            }
        });
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DeviceUtil.isPad(getActivity())) {
                    PhotoFragment photoFragment = PhotoFragment.newInstance(mPhotoFile.getPath());
                    photoFragment.setTargetFragment(CrimeFragment.this, REQUEST_PHOTO);
                    photoFragment.show(getFragmentManager(), DIALOG_PHOTO);
                } else {
                    Intent intent = PhotoActivity.getIntent(getActivity(), mPhotoFile.getPath());
                    startWithTransition(getActivity(), intent, mPhotoImageView);
                }
            }
        });

        // 设置拍照按钮
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 检测照片文件不为空，并且有响应的照相启动
        boolean canTakePhoto = mPhotoFile != null && cameraIntent.resolveActivity
                (packageManager) != null;
        mCameraButton.setEnabled(canTakePhoto);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseActivity.requestRuntimePermission(new String[]{Manifest
                        .permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new
                        PermissionListener() {
                            @Override
                            public void onGranted() {
                                ContentValues contentValues = new ContentValues(1);
                                contentValues.put(MediaStore.Images.Media.DATA, mPhotoFile.getAbsolutePath());
                                Uri uri = getActivity().getContentResolver().insert(MediaStore.Images
                                        .Media.EXTERNAL_CONTENT_URI, contentValues);

                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(cameraIntent, REQUEST_CAMERA);
                            }

                            @Override
                            public void onDenied(List<String> deniedPermissions) {

                            }
                        });
            }
        });

        return v;
    }

    /**
     * 接受从DatePickerFragment传过来的日期数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            // 更新日期数据
            mCrime.setDate(date);
            updateDate();
            updateCrime();
        } else if (requestCode == REQUEST_CONTACT) {
            // 根据返回结果获取联系人姓名和手机号
            Uri contactUri = data.getData();

            // 获取联系人姓名
            String suspect = getContactName(contactUri);
            if (suspect != null) {
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                updateCrime();
            }

            // 获取联系人手机号
            String phone = getContactPhone(contactUri);
            mCrime.setSuspectPhone(phone);
            if (phone != null) {
                mCallSuspectButton.setEnabled(true);
                mCallSuspectButton.setText(phone);
                updateCrime();
            } else {
                mCallSuspectButton.setEnabled(false);
                mCallSuspectButton.setText(R.string.crime_call_suspect);
            }
        } else if (requestCode == REQUEST_CAMERA) {
            updatePhotoView();
            updateCrime();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.get(getActivity()).delete(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 在屏幕失去焦点时更新数据
        CrimeLab.get(getActivity()).update(mCrime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallBack = (CallBack) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    /**
     * 更新日期数据
     */
    private void updateDate() {
        mDateButton.setText(mCrime.getDateString());
        mTimeButton.setText(mCrime.getTimeString());
    }

    /**
     * 设置返回的数据
     */
    private void setResult() {
        Intent data = new Intent();
        getActivity().setResult(Activity.RESULT_OK, data);
    }

    /**
     * 拼凑报告信息
     *
     * @return
     */
    private String getCrimeReport() {
        // 是否解决
        String solved;
        if (mCrime.isSolved()) {
            solved = getString(R.string.crime_report_solved);
        } else {
            solved = getString(R.string.crime_report_unsolved);
        }
        // 嫌疑人
        String suspect;
        if (mCrime.getSuspect() == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, mCrime.getSuspect());
        }

        return getString(R.string.crime_report, mCrime.getTitle(), mCrime.getDateString(),
                suspect, solved);
    }


    /**
     * 获取联系人姓名
     *
     * @param contactUri
     * @return
     */
    private String getContactName(Uri contactUri) {
        String name = null;
        String[] cols = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        Cursor nameCursor = getActivity().getContentResolver().query(contactUri, cols, null, null, null);
        try {
            if (nameCursor.getCount() > 0) {
                nameCursor.moveToFirst();
                // 获取联系人姓名
                name = nameCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            nameCursor.close();
            return name;
        }
    }

    /**
     * 获取联系人手机号
     *
     * @param contactUri
     * @return
     */
    private String getContactPhone(Uri contactUri) {
        String phone = null;
        Cursor cursor = getActivity().getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                // 说明有电话号码
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{id}, null);
                    if (phoneCursor.getCount() > 0) {
                        phoneCursor.moveToFirst();
                        phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            return phone;
        }
    }

    /**
     * 更新相片
     */
    private void updatePhotoView() {
        if (mPhotoFile != null && mPhotoFile.exists()) {
            Bitmap bitmap = PictureUtil.getScaleBitmap(mPhotoFile
                    .getPath(), ActivityCollector.getTop());
            mPhotoImageView.setImageBitmap(bitmap);
        } else {
            mPhotoImageView.setImageBitmap(null);
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).update(mCrime);
        mCallBack.onCrimeUpdate(mCrime);
    }
}
