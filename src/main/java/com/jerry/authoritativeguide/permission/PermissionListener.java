package com.jerry.authoritativeguide.permission;

import java.util.List;

/**
 * 申请访问权限回调的接口
 */
public interface PermissionListener {
    /**
     * 访问权限通过
     */
    void onGranted();

    /**
     * 访问权限被拒绝
     *
     * @param deniedPermissions 被拒绝的权限集合
     */
    void onDenied(List<String> deniedPermissions);
}