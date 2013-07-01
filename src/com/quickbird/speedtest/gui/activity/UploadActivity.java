//package com.quickbird.speedtest.gui.activity;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageButton;
//
//import com.quickbird.controls.Constants;
//import com.quickbird.speedtest.R;
//import com.quickbird.speedtestengine.FormFile;
//import com.quickbird.speedtestengine.utils.DebugUtil;
//
//public class UploadActivity extends Activity{
//    
//    private File file;
//    private Handler handler;
//    private static final String TAG="MainActivity";
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.location);
//        DebugUtil.i(TAG, "onCreate");
//        file = new File(Constants.PIC_PRE_PATH_NAME);
//        DebugUtil.i(TAG, "照片文件是否存在："+file.exists());
//        handler = new Handler();
//        ImageButton upload = (ImageButton) findViewById(R.id.upload_button);
//        upload.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handler.post(runnable);
//            }
//        });
//    }
//    
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            DebugUtil.i(TAG, "runnable run");
//            uploadFile(file);
//            // handler.postDelayed(runnable, 5000);
//        }
//    };
//    
//    /**
//     * 上传图片到服务器
//     * 
//     * @param imageFile 包含路径
//     */
//    public void uploadFile(File imageFile) {
//        DebugUtil.i(TAG, "upload start");
//        try {
//            String requestUrl = "http://www.quickbird.com/";
//            //请求普通信息
//            Map<String, String> params = new HashMap<String, String>();
//            params.put("username", "张三");
//            params.put("pwd", "zhangsan");
//            params.put("age", "21");
//            params.put("fileName", imageFile.getName());
//            //上传文件
//            FormFile formfile = new FormFile(imageFile.getName(), imageFile, "image", "application/octet-stream");
//            boolean ifsuccess = SocketHttpRequester.post(requestUrl, params, formfile);
//            DebugUtil.i(TAG, "upload success:"+ifsuccess);
//        } catch (Exception e) {
//            DebugUtil.i(TAG, "upload error");
//            e.printStackTrace();
//        }
//        DebugUtil.i(TAG, "upload end");
//    }
//
//}
