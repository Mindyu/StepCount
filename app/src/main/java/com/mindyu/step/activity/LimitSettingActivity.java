package com.mindyu.step.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.mindyu.step.R;
import com.mindyu.step.util.CommonUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class LimitSettingActivity extends SwipeBackActivity {

    private CommonTitleBar topbar;
    private Button quickBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_limit_setting);

        initView();
        initEvents();
    }

    private void initView(){
        topbar = findViewById(R.id.topbar);
        quickBtn = findViewById(R.id.quick_setting);
        topbar.setBackgroundResource(R.drawable.shape_gradient);
    }

    private void initEvents(){
        quickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                try {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Log.e("HLQ_Struggle", "******************当前手机型号为：" + CommonUtil.getMobileType());
                    ComponentName componentName = null;
                    if (CommonUtil.getMobileType().equals("Xiaomi")) { // 红米Note4测试通过
                        componentName = new ComponentName("com.miui.securitycenter",
                                "com.miui.permcenter.autostart.AutoStartManagementActivity");
                    } else if (CommonUtil.getMobileType().equals("Letv")) { // 乐视2测试通过
                        intent.setAction("com.letv.android.permissionautoboot");
                    } else if (CommonUtil.getMobileType().equals("samsung")) { // 三星Note5测试通过
                        componentName = new ComponentName("com.samsung.android.sm_cn",
                                "com.samsung.android.sm.ui.ram.AutoRunActivity");
                    } else if (CommonUtil.getMobileType().equals("HUAWEI")) { // 华为测试通过
                        componentName = new ComponentName("com.huawei.systemmanager",
                                "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                    } else if (CommonUtil.getMobileType().equals("vivo")) { // VIVO测试通过
                        componentName = ComponentName.unflattenFromString("com.iqoo.secure" +
                                "/.safeguard.PurviewTabActivity");
                    } else if (CommonUtil.getMobileType().equals("Meizu")) { //万恶的魅族
                        // 通过测试，发现魅族是真恶心，也是够了，之前版本还能查看到关于设置自启动这一界面，
                        // 系统更新之后，完全找不到了，心里默默Fuck！
                        // 针对魅族，我们只能通过魅族内置手机管家去设置自启动，
                        // 所以我在这里直接跳转到魅族内置手机管家界面，具体结果请看图
                        componentName = ComponentName.unflattenFromString("com.meizu.safe" +
                                "/.permission.PermissionMainActivity");
                    } else if (CommonUtil.getMobileType().equals("OPPO")) { // OPPO R8205测试通过
                        componentName = ComponentName.unflattenFromString("com.oppo.safe" +
                                "/.permission.startup.StartupAppListActivity");
                        Intent intentOppo = new Intent();
                        intentOppo.setClassName("com.oppo.safe/.permission.startup",
                                "StartupAppListActivity");
                        if (getPackageManager().resolveActivity(intentOppo, 0) == null) {
                            componentName = ComponentName.unflattenFromString("com.coloros.safecenter" +
                                    "/.startupapp.StartupAppListActivity");
                        }
                    } else if (CommonUtil.getMobileType().equals("ulong")) { // 360手机 未测试
                        componentName = new ComponentName("com.yulong.android.coolsafe",
                                ".ui.activity.autorun.AutoRunListActivity");
                    } else {
                        // 以上只是市面上主流机型，由于公司你懂的，所以很不容易才凑齐以上设备
                        // 针对于其他设备，我们只能调整当前系统app查看详情界面
                        // 在此根据用户手机当前版本跳转系统设置界面
                        if (9 <= Build.VERSION.SDK_INT) {
                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                        } else{
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setClassName("com.android.settings",
                                    "com.android.settings.InstalledAppDetails");
                            intent.putExtra("com.android.settings.ApplicationPkgName",
                                    getPackageName());
                        }
                    }
                    intent.setComponent(componentName);
                    startActivity(intent);
                } catch (Exception e) {     // 抛出异常就直接打开设置页面
                    intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
            }
        });
    }

}
