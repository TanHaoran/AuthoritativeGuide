package com.jerry.authoritativeguide.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jerry.authoritativeguide.permission.PermissionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerry on 2017/1/6.
 */

public class BaseActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 0;

    private PermissionListener mListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 将所有权限进行申请
     *
     * @param permissions
     * @param listener
     */
    public void requestRuntimePermission(String[] permissions, PermissionListener listener) {
        mListener = listener;

        // 创建需要申请权限的集合
        List<String> needPermissions = new ArrayList<>();

        // 如果申请权限遭拒绝就添加到需要申请权限列表的集合中
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                needPermissions.add(permission);
            }
        }

        // 如果权限都通过，那么需要申请权限的集合就为空，那么就调用权限通过的接口，否则就去申请需要申请的权限
        if (needPermissions.isEmpty()) {
            mListener.onGranted();
        } else {
            // 调用申请权限的方法就会产生回调，就需要接收回调
            ActivityCompat.requestPermissions(this, needPermissions.toArray(new String[needPermissions.size()]),
                    REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            List<String> deniedPermissions = new ArrayList<>();
            // 如果申请没有通过就要添加到被拒绝的集合中
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    deniedPermissions.add(permissions[i]);
                }
            }
            if (deniedPermissions.isEmpty()) {
                mListener.onGranted();
            } else {
                mListener.onDenied(deniedPermissions);
                for (String deniedPermission : deniedPermissions) {
                    Toast.makeText(this, deniedPermission + "权限被拒绝了", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
