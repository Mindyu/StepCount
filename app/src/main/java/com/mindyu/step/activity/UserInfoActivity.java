package com.mindyu.step.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mindyu.step.R;
import com.mindyu.step.parameter.SystemParameter;
import com.mindyu.step.user.bean.Info;
import com.mindyu.step.user.bean.Result;
import com.mindyu.step.user.bean.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "UserInfoActivity";

    private View inflate;
    private TextView takePhoto;
    private TextView chooseFromAlbum;
    private TextView cancel;
    private Dialog dialog;

    private EditText name_tv;
    private EditText birth_tv;
    private EditText height_tv;
    private EditText weight_tv;
    private EditText address_tv;
    private EditText intro_tv;
    private RadioGroup sexRadio;
    private RadioButton male_btn;
    private RadioButton female_btn;
    private String sexString="";

    private LinearLayout avator_layout;
    private RoundedImageView show_photo;
    private Uri imageUri;

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_info);

        initView();
        initData();

        avator_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoDialog(view);
            }
        });
        sexRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.male:
                        sexString = "男";
                        break;
                    case R.id.female:
                        sexString = "女";
                        break;
                }
            }
        });
    }

    private void initView(){
        show_photo = findViewById(R.id.avator_iv);
        avator_layout = findViewById(R.id.avator_layout);
        name_tv = findViewById(R.id.name_tv);
        birth_tv = findViewById(R.id.birth_tv);
        height_tv = findViewById(R.id.height_tv);
        weight_tv = findViewById(R.id.weight_tv);
        address_tv = findViewById(R.id.address_tv);
        intro_tv = findViewById(R.id.intro_tv);
        sexRadio = findViewById(R.id.rg);
        male_btn = findViewById(R.id.male);
        female_btn = findViewById(R.id.female);
    }

    private void initData() {
        Integer userId = SystemParameter.user.getId();
        name_tv.setText(SystemParameter.user.getUserName());
        refreshView();
        if (SystemParameter.info==null) new UserInfoTask().execute(userId);
    }

    @SuppressLint("StaticFieldLeak")
    public class UserInfoTask extends AsyncTask<Integer, Void, Info> {

        @Override
        protected Info doInBackground(Integer... integers) {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(SystemParameter.ip + "/info/" + integers[0])
                    .build();
            Log.d(TAG, "request url: "+ request);
            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                if (response.body()==null) {
                    Log.d(TAG, "onResponse: 获取用户信息失败");
                    return null;
                }
                String data = response.body().string();
                Log.d(TAG, "onResponse: "+data);

                Gson gson = new Gson();
                Info result = gson.fromJson(data, new TypeToken<Info>() {
                }.getType());

                if (result!= null){
                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Info info) {
            SystemParameter.info = info;
            refreshView();
        }
    }

    private void refreshView(){
        if (SystemParameter.info==null) return;
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//显示2017-10-27格式
        birth_tv.setText(sdf.format(SystemParameter.info.getBirthday()));
        height_tv.setText(String.valueOf(SystemParameter.info.getHeight()));
        weight_tv.setText(String.valueOf(SystemParameter.info.getWeight()));
        address_tv.setText(SystemParameter.info.getAddress());
        intro_tv.setText(SystemParameter.info.getIntro());
        sexRadio.clearCheck();
        if ("男".equals(SystemParameter.info.getSex()))
            male_btn.setChecked(true);
        else
            female_btn.setChecked(true);
    }

    public void showPhotoDialog(View view) {
        dialog = new Dialog(this, R.style.BottomDialogTheme);
        //填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.choose_user_avatar, null);
        //初始化控件
        takePhoto = (TextView) inflate.findViewById(R.id.camera);
        chooseFromAlbum = (TextView) inflate.findViewById(R.id.pic);
        cancel = (TextView) inflate.findViewById(R.id.cancel);
        //设置监听
        takePhoto.setOnClickListener(this);
        chooseFromAlbum.setOnClickListener(this);
        cancel.setOnClickListener(this);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //设置Dialog距离底部的距离
        lp.y = 50;
        //将属性设置给窗体
        dialogWindow.setAttributes(lp);
        //显示对话框
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                takePhoto();
                dialog.dismiss();
                break;
            case R.id.pic:
                chooseFromAlbum();
                dialog.dismiss();
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.save_btn:     // 监听菜单按钮
                // 保存用户详细信息
                if (!"".equals(sexString)) {
                    SystemParameter.info.setSex(sexString);
                }
                SystemParameter.info.setHeight(Double.valueOf(height_tv.getText().toString()));
                SystemParameter.info.setWeight(Double.valueOf(weight_tv.getText().toString()));
                String dateStr = birth_tv.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = sdf.parse(dateStr);
                    SystemParameter.info.setBirthday(new java.sql.Date(date.getTime()));
                    Log.d(TAG, SystemParameter.info.getBirthday().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SystemParameter.info.setAddress(address_tv.getText().toString());
                SystemParameter.info.setIntro(intro_tv.getText().toString());

                Log.d(TAG, SystemParameter.info.toString());
                new UserInfoSaveTask().execute(SystemParameter.info);

                UserInfoActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //调用摄像头拍照
    public void takePhoto() {
        //创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(UserInfoActivity.this, "com.mindyu.step.fileprovider", outputImage);
        } else {

            imageUri = Uri.fromFile(outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //	将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        show_photo.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }


    //从相册中选择照片
    public void chooseFromAlbum() {
        if (ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);    //	打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    //4.4及以上系统使用这个方法处理图片
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];  //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果不是document类型的Uri,则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);   //根据图片路径显示图片
    }

    //4.4以下系统使用这个方法处理图片
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);   //根据图片路径显示图片
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            show_photo.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class UserInfoSaveTask extends AsyncTask<Info, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Info... infos) {
            OkHttpClient okHttpClient = new OkHttpClient();

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Gson gson=new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd")
                    .create();
            //将对象转换为诶JSON格式字符串
            String jsonStr=gson.toJson(infos[0]);
            RequestBody body = RequestBody.create(JSON, jsonStr);

            Request request = new Request.Builder()
                    .url(SystemParameter.ip + "/info/")
                    .post(body)
                    .build();
            Log.d(TAG, "request url: "+ request);
            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                if (response.body()==null) {
                    Log.d(TAG, "onResponse: 保存用户信息失败");
                    return null;
                }
                String data = response.body().string();
                Log.d(TAG, "onResponse: "+data);

                Result result = gson.fromJson(data, new TypeToken<Result>() {
                }.getType());
                if (result.getCode() == 200){
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result){
                Log.d(TAG, "onPostExecute: 保存成功");
                Toast.makeText(UserInfoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(UserInfoActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}


