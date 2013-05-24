package com.quickbird.speedtestengine.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.os.SystemClock;

import com.quickbird.controls.Constants;
import com.quickbird.enums.SpeedTestError;
import com.quickbird.enums.SpeedTestType;
import com.quickbird.speedtestengine.FormFile;
import com.quickbird.speedtestengine.TestParameters;
import com.quickbird.speedtestengine.TestParametersTransfer;
import com.quickbird.speedtestengine.TestTaskCallbacks;
import com.quickbird.speedtestengine.utils.DebugUtil;

public class UploadTestTask extends SpeedTestTask {
    protected static final String LOGTAG = "UploadTestTask";
    private static final double MAXSIZE = 2022400.0D;
    private File file;

    public UploadTestTask(TestTaskCallbacks paramTestTaskCallbacks, int paramInt) {
        super(paramTestTaskCallbacks, paramInt);
        file = new File(Constants.PIC_PRE_PATH_NAME);
        DebugUtil.i("照片文件是否存在：" + file.exists());
    }

    @Override
    protected SpeedTestType getSpeedTestType() {
        return SpeedTestType.Upload;
    }

    @Override
    protected SpeedTestTask.SpeedTask getTaskInstance(int paramInt) {
        return new UploadSpeedTask(paramInt, new TestParametersTransfer(SpeedTestType.Upload));
    }

    protected class UploadSpeedTask extends SpeedTestTask.SpeedTask {
        private int mTestLength = 10000;

        public UploadSpeedTask(int paramTestParametersTransfer, TestParametersTransfer arg3) {
            super(paramTestParametersTransfer, arg3);
        }

        private void processUpload(String path, Map<String, String> params, FormFile[] files , URL paramURL, TestParametersTransfer paramTestParametersTransfer) {
            int totalByte = 0;
            try {
                byte[] arrayOfByte = new byte[1024];
                paramTestParametersTransfer.clearBytes();
                paramTestParametersTransfer.clearProgress();
                publishProgress(new Void[0]);
                int k = 0;
                long t3 = 0L;
                long t1 = 150L;// 控制多长时间获取一次数据
                
                final String BOUNDARY = "---------------------------7da2137580612"; //数据分隔线
                final String endline = "--" + BOUNDARY + "--\r\n";//数据结束标志
                int fileDataLength = 0;
                
                for(FormFile uploadFile : files){//得到文件类型数据的总长度
                    StringBuilder fileExplain = new StringBuilder();
                     fileExplain.append("--");
                     fileExplain.append(BOUNDARY);
                     fileExplain.append("\r\n");
                     fileExplain.append("Content-Disposition: form-data;name=\""+ uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilname() + "\"\r\n");
                     fileExplain.append("Content-Type: "+ uploadFile.getContentType()+"\r\n\r\n");
                     fileExplain.append("\r\n");
                     fileDataLength += fileExplain.length();
                    if(uploadFile.getInStream()!=null){
                        fileDataLength += uploadFile.getFile().length();
                     }else{
                         fileDataLength += uploadFile.getData().length;
                     }
                    DebugUtil.d("uploadFile Length:"+fileDataLength);
                }
                StringBuilder textEntity = new StringBuilder();
                for (Map.Entry<String, String> entry : params.entrySet()) {//构造文本类型参数的实体数据
                    textEntity.append("--");
                    textEntity.append(BOUNDARY);
                    textEntity.append("\r\n");
                    textEntity.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");
                    textEntity.append(entry.getValue());
                    textEntity.append("\r\n");
                }
                //计算传输给服务器的实体数据总长度
                int dataLength = textEntity.toString().getBytes().length + fileDataLength +  endline.getBytes().length;
                
                URL url = new URL(path);
                int port = url.getPort()==-1 ? 80 : url.getPort();
                Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);           
                OutputStream outStream = socket.getOutputStream();
                //下面完成HTTP请求头的发送
                String requestmethod = "POST "+ url.getPath()+" HTTP/1.1\r\n";
                outStream.write(requestmethod.getBytes());
                String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
                outStream.write(accept.getBytes());
                String language = "Accept-Language: zh-CN\r\n";
                outStream.write(language.getBytes());
                String contenttype = "Content-Type: multipart/form-data; boundary="+ BOUNDARY+ "\r\n";
                outStream.write(contenttype.getBytes());
                String contentlength = "Content-Length: "+ dataLength + "\r\n";
                outStream.write(contentlength.getBytes());
                String alive = "Connection: Keep-Alive\r\n";
                outStream.write(alive.getBytes());
                String host = "Host: "+ url.getHost() +":"+ port +"\r\n";
                outStream.write(host.getBytes());
                //写完HTTP请求头后根据HTTP协议再写一个回车换行
                outStream.write("\r\n".getBytes());
                //把所有文本类型的实体数据发送出来
                outStream.write(textEntity.toString().getBytes());           
                //把所有文件类型的实体数据发送出来
                for(FormFile uploadFile : files){
                    StringBuilder fileEntity = new StringBuilder();
                     fileEntity.append("--");
                     fileEntity.append(BOUNDARY);
                     fileEntity.append("\r\n");
                     fileEntity.append("Content-Disposition: form-data;name=\""+ uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilname() + "\"\r\n");
                     fileEntity.append("Content-Type: "+ uploadFile.getContentType()+"\r\n\r\n");
                     outStream.write(fileEntity.toString().getBytes());
                     if(uploadFile.getInStream()!=null){
                         while (true) {
                             if ((k != 0) || (getCancelled()) || (getCompleted())) {
                                DebugUtil.d("k:" + k + "      "
                                        + "getCancelled():" + getCancelled()
                                        + "      " + "getCompleted()"
                                        + getCompleted());
                                 outStream.close();
                                 break;
                             }
                             int len = 0;
                             len = uploadFile.getInStream().read(arrayOfByte, 0, 1024);
                             DebugUtil.d("len:"+len);
//                             outStream.write(arrayOfByte, 0, len);
                             outStream.write(arrayOfByte);
                             totalByte += len;
                             long t2 = SystemClock.uptimeMillis();
                             if ((len == 0) && (t3 > 0L) && (t2 < 200L + t3))
                                 continue;
                             if (t2 > t3 + t1) {
                                 paramTestParametersTransfer.setProgress(getProgress(totalByte));
                                 paramTestParametersTransfer.setBytes(totalByte);
                                 publishProgress(new Void[0]);
                                 t3 = t2;
                                 t1 = 30L;
                             }
                             if (len != -1)
                                 continue;
                             k = 1;
                         }
                         uploadFile.getInStream().close();
                     }else{
                         outStream.write(uploadFile.getData(), 0, uploadFile.getData().length);
                     }
                     outStream.write("\r\n".getBytes());
                }
                //下面发送数据结束标志，表示数据已经结束
                outStream.write(endline.getBytes());
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                if(reader.readLine().indexOf("200")==-1){//读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
                    DebugUtil.d("上传失败："+dataLength);
                }
                outStream.flush();
                outStream.close();
                reader.close();
                socket.close();
                DebugUtil.d("上传图片："+dataLength);
                
            } catch (IOException localIOException) {
                DebugUtil.e("DownloadTestTask", "Download test IO failed:" + localIOException);
                UploadTestTask.this.setError(SpeedTestError.TEST_RUN_IO);
            } catch (Exception localException) {
                DebugUtil.e("DownloadTestTask", "Download test failed" + localException);
                UploadTestTask.this.setError(SpeedTestError.TEST_RUN);
            }
        }
        
        @Override
        protected TestParameters doInBackground(URL[] paramArrayOfURL) {
            TestParametersTransfer localTestParametersTransfer = (TestParametersTransfer) getResult();
            try {
                UploadTestTask.this.setError(SpeedTestError.None);
                setStartTime(SystemClock.uptimeMillis());
                
                String requestUrl = "http://www.quickbird.com/";
                //请求普通信息
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "张三");
                params.put("pwd", "zhangsan");
                params.put("age", "21");
                params.put("fileName", file.getName());
                //上传文件
                FormFile formfile = new FormFile(file.getName(), file, "image", "application/octet-stream");
                
                processUpload(requestUrl, params, new FormFile[]{formfile},paramArrayOfURL[0], localTestParametersTransfer);
                if (UploadTestTask.this.getError() == SpeedTestError.None) {
                    localTestParametersTransfer.setSuccess(true);
                    UploadTestTask.this.success();
                    setCompleted(true);
                    return localTestParametersTransfer;
                }
            } catch (Exception localException) {
                while (true) {
                    DebugUtil.e("DownloadTestTask" + localException.getMessage());
                    UploadTestTask.this.setError(SpeedTestError.TEST_RUN);
                    localTestParametersTransfer.setSuccess(false);
                    UploadTestTask.this.failed(UploadTestTask.this.getError());
                    break;
                }
            }
            return localTestParametersTransfer;
        }
        
        protected float getProgress(int paramInt) {
            double d2 = paramInt / MAXSIZE;
            double d1 = (int) (SystemClock.uptimeMillis() - getStartTime()) / this.mTestLength;
            if (d1 >= 1.0D){
                setCompleted(true);
            }
            return (float) Math.max(d2, d1);
        }
    }
}
