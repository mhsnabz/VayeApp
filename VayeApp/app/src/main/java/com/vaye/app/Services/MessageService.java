package com.vaye.app.Services;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.vaye.app.Controller.NotificationService.MajorPostNotification;
import com.vaye.app.Controller.NotificationService.MsgNotification;
import com.vaye.app.Controller.NotificationService.PushNotificationService;
import com.vaye.app.Controller.NotificationService.PushNotificationTarget;
import com.vaye.app.Controller.NotificationService.PushNotificationType;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.LoginRegister.MessageType;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.MessagesModel;
import com.vaye.app.Model.OtherUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.DOWNLOAD_SERVICE;

public class MessageService {
    private static final String TAG = "MessageService";
    private static final MessageService instance = new MessageService();
    public static MessageService shared() {
        return instance;
    }


    public void deleteBadge(CurrentUser currentUser , OtherUser otherUser){
        Log.d(TAG, "deleteBadge: start delete badge");

        Query deleteBadgeDb = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid()).collection("badgeCount").whereEqualTo("badge","badge");
        CollectionReference dbc = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list")
                .document(otherUser.getUid()).collection("badgeCount");
        deleteBadgeDb.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().getDocuments().isEmpty()){
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            Log.d(TAG, "onComplete: get badgeid" + item.getId());
                            dbc.document(item.getId()).delete();
                        }
                    }
                }
            }
        });

    }

 public void deleteRequestMessagesBadge(CurrentUser currentUser , OtherUser otherUser){
        Log.d(TAG, "deleteBadge: start delete badge");

        Query deleteBadgeDb = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-request")
                .document(otherUser.getUid()).collection("badgeCount").whereEqualTo("badge","badge");
        CollectionReference dbc = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-request")
                .document(otherUser.getUid()).collection("badgeCount");
        deleteBadgeDb.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (!task.getResult().getDocuments().isEmpty()){
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            Log.d(TAG, "onComplete: get badgeid" + item.getId());
                            dbc.document(item.getId()).delete();
                        }
                    }
                }
            }
        });
    }

    public void sendTextMsg(CurrentUser currentUser , OtherUser otherUser , String fileName,long time,GeoPoint geoPoint,int duration,Float width , Float heigth,String msg , String messageId , String type){
            if (currentUser.getFriendList().contains(otherUser.getUid())){
                DocumentReference dbSender = FirebaseFirestore.getInstance().collection("messages")
                        .document(currentUser.getUid())
                        .collection(otherUser.getUid())
                        .document(messageId);
                dbSender.set(getHasMap(msg,messageId,type,fileName,currentUser,width,heigth,duration,geoPoint,time)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                    .document(currentUser.getUid())
                                    .collection("msg-list")
                                    .document(otherUser.getUid());
                            db.set(dicSenderLastMessage(msg,otherUser,type),SetOptions.merge());
                        }
                    }
                });
                DocumentReference dbGetter = FirebaseFirestore.getInstance().collection("messages")
                        .document(otherUser.getUid())
                        .collection(currentUser.getUid())
                        .document(messageId);
                dbGetter.set(getHasMap(msg,messageId,type,fileName,currentUser,width,heigth,duration,geoPoint,time)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                    .document(otherUser.getUid())
                                    .collection("msg-list")
                                    .document(currentUser.getUid());
                            db.set(dicGetterLastMessage(msg,currentUser,type),SetOptions.merge());
                        }
                    }
                });
                getBadgeCount(currentUser,"msg-list",otherUser);
                setBadgeCount(currentUser,otherUser,"msg-list");


                    if (type.equals(MessageType.text)){
                        PushNotificationService.shared().sendPushNotification(currentUser,PushNotificationType.message,String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_msg,currentUser.getName(),msg, MsgNotification.descp.new_msg,currentUser.getUid());

                    }else if (type.equals(MessageType.photo)){
                        PushNotificationService.shared().sendPushNotification(currentUser,PushNotificationType.message,String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_image,currentUser.getName(),"Resim Gönderdi", MsgNotification.descp.new_image,currentUser.getUid());

                    }else if (type.equals(MessageType.audio)){
                        PushNotificationService.shared().sendPushNotification(currentUser,PushNotificationType.message,String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_record,currentUser.getName(),"Ses Kaydı Gönderdi", MsgNotification.descp.new_record,currentUser.getUid());

                    }else if (type.equals(MessageType.location)){
                        PushNotificationService.shared().sendPushNotification(currentUser,PushNotificationType.message,String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_location,currentUser.getName(),"Konum Gönderdi", MsgNotification.descp.new_location,currentUser.getUid());

                    }

            }else{
                DocumentReference dbSender = FirebaseFirestore.getInstance().collection("messages")
                        .document(currentUser.getUid())
                        .collection(otherUser.getUid())
                        .document(messageId);
                dbSender.set(getHasMap(msg,messageId,type,fileName,currentUser,width,heigth,duration,geoPoint,time),SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                    .document(currentUser.getUid())
                                    .collection("msg-list")
                                    .document(otherUser.getUid());
                            db.set(dicSenderLastMessage(msg,otherUser,type),SetOptions.merge());

                        }
                    }
                });
                DocumentReference dbGetter = FirebaseFirestore.getInstance().collection("messages")
                        .document(otherUser.getUid())
                        .collection(currentUser.getUid())
                        .document(messageId);
                dbGetter.set(getHasMap(msg,messageId,type,fileName,currentUser,width,heigth,duration,geoPoint,time),SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                    .document(otherUser.getUid())
                                    .collection("msg-request")
                                    .document(currentUser.getUid());
                            db.set(dicGetterLastMessage(msg,currentUser,type),SetOptions.merge());

                        }
                    }
                });
                getBadgeCount(currentUser,"msg-request",otherUser);
                setBadgeCount(currentUser,otherUser,"msg-request");



                        PushNotificationService.shared().sendPushNotification(currentUser,PushNotificationType.message,String.valueOf(Calendar.getInstance().getTimeInMillis()),otherUser.getUid(),otherUser, MsgNotification.type.new_rqst,currentUser.getName(),"Size Mesaj Göndermek İstiyor", MsgNotification.descp.new_rqst,currentUser.getUid());


            }
    }

    private HashMap<String , Object> getHasMap(String msg , String id , String  type ,String fileName, CurrentUser currentUser , Float width, Float heigth, int duration , GeoPoint geoPoint ,long time){
        HashMap<String , Object> map = new HashMap<>();
        map.put("content",msg);
        map.put("id",id);
        map.put("type",type);
        map.put("senderUid",currentUser.getUid());
        map.put("name",currentUser.getName());
        map.put("fileName",fileName);
        map.put("geoPoint",geoPoint);
        map.put("date", FieldValue.serverTimestamp());
        map.put("time",time);
        map.put("is_read",false);
        map.put("width",width);
        map.put("heigth",heigth);
        map.put("duration",duration);
        return map;

    }
    private HashMap<String , Object> dicGetterLastMessage(String lastMsg ,CurrentUser currentUser , String type ){
        HashMap<String , Object> map = new HashMap<>();
        map.put("lastMsg",lastMsg);
        map.put("time",FieldValue.serverTimestamp());
        map.put("thumbImage",currentUser.getThumb_image());
        map.put("username",currentUser.getUsername());
        map.put("uid",currentUser.getUid());
        map.put("name",currentUser.getName());
        map.put("type",type);
        return map;
    }

    private HashMap<String , Object> dicSenderLastMessage(String lastMsg ,OtherUser otherUser , String type ){
        HashMap<String , Object> map = new HashMap<>();
        map.put("lastMsg",lastMsg);
        map.put("time",FieldValue.serverTimestamp());
        map.put("thumbImage",otherUser.getThumb_image());
        map.put("username",otherUser.getUsername());
        map.put("uid",otherUser.getUid());
        map.put("name",otherUser.getName());
        map.put("type",type);
        return map;
    }

    private void getBadgeCount(CurrentUser currentUser , String target , OtherUser otherUser){

          Query db = FirebaseFirestore.getInstance().collection("user")
                  .document(otherUser.getUid())
                  .collection(target).document(currentUser.getUid())
                  .collection("badgeCount").whereEqualTo("badge","badge");
          db.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()){
                      if (task.getResult().getDocuments().size()>0){
                          DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                  .document(otherUser.getUid())
                                  .collection(target).document(currentUser.getUid());
                          Map<String , Object> map = new HashMap<>();
                          map.put("badgeCount",task.getResult().getDocuments().size());
                          db.set(map,SetOptions.merge());

                      }
                  }
              }
          });

    }

    private void setBadgeCount(CurrentUser currentUser , OtherUser otherUser , String  target){

            CollectionReference db = FirebaseFirestore.getInstance().collection("user")
                    .document(otherUser.getUid())
                    .collection(target).document(currentUser.getUid()) .collection("badgeCount");
            Map<String,Object> map = new HashMap<>();
            map.put("badge","badge");
            db.add(map);


    }


    public void downloadService(Context context, String fileUrl , String fileName){
        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(fileUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("My File");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        request.setDestinationUri(Uri.parse("file://" + "vayeApp"));

        downloadmanager.enqueue(request);
    }


    public void downloadAudio(Context context, String fileUrl , String fileName , TrueFalse<Boolean> callback){
        

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                File direct = new File(Environment.getExternalStorageDirectory()
                        + "vayeapp");
                if (!direct.exists()) {
                    direct.mkdirs();
                }
                File ExistingFile =  new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC) + "/vayeapp/" + fileName);
                if (!ExistingFile.exists()){
                    DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri downloadUri = Uri.parse(fileUrl);
                    DownloadManager.Request request = new DownloadManager.Request(
                            downloadUri);

                    request.setAllowedNetworkTypes(
                            DownloadManager.Request.NETWORK_WIFI
                                    | DownloadManager.Request.NETWORK_MOBILE)
                            .setAllowedOverRoaming(false)

                            .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC,"vayeapp/"+ fileName);


                    final long downloadId = mgr.enqueue(request);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            boolean downloading = true;
                            while (downloading) {
                                DownloadManager.Query q = new DownloadManager.Query();
                                q.setFilterById(downloadId);
                                Cursor cursor = mgr.query(q);
                                cursor.moveToFirst();
                                int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                    downloading = false;
                                }
                                if (!downloading){
                                    Log.d(TAG, "run: download complete");
                                    callback.callBack(true);
                                }else {
                                    callback.callBack(false);
                                }
                                cursor.close();
                            }

                        }
                    }).start();

                    mgr.enqueue(request);
                }



            }
        });

    }
    public  boolean isFilePresent(String fileName) {
        return getFilePath(fileName).isFile();
    }
    public  File getFilePath(String fileName){
        String extStorageDirectory = Environment.DIRECTORY_MUSIC.toString();
        File folder = new File(extStorageDirectory, "vayeapp");
        File filePath = new File(folder + "/" + fileName);
        return filePath;
    }
    public void down(Context context, String fileUrl , String fileName){
        Uri uri = Uri.parse(fileUrl);
        DownloadManager.Request r = new DownloadManager.Request(uri);

// This put the download in the same Download dir the browser uses
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

// When downloading music and videos they will be listed in the player
// (Seems to be available since Honeycomb only)
        r.allowScanningByMediaScanner();

// Notify user when download is completed
// (Seems to be available since Honeycomb only)
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

// Start download
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(r);
    }
    public void downloadAudioFile(String dwnload_file_path, String fileName,
                                  String pathToSave){
        Log.d(TAG, "downloadAudioFile: start to download "+ fileName);
        int downloadedSize = 0;
        int totalSize = 0;
        try{
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            // connect
            urlConnection.connect();

            File myDir;
            myDir = new File(pathToSave);
            myDir.mkdirs();

            String mFileName = fileName;
            File file = new File(myDir, mFileName);

            FileOutputStream fileOutput = new FileOutputStream(file);

            InputStream inputStream = urlConnection.getInputStream();


            totalSize = urlConnection.getContentLength();

            byte[] buffer = new byte[4096];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;

            }
            Log.d(TAG, "downloadAudioFile: download complete" );
            inputStream.close();
            fileOutput.close();


        }catch (final MalformedURLException e) {
            Log.d(TAG, "MalformedURLException: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (final IOException e) {
            Log.d(TAG, "IOException: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (final Exception e) {
            if (e!=null)
            Log.d(TAG, "Exception: " + e.getLocalizedMessage());
        }
    }


    public void  removeChat(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){

        DocumentReference reference = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-list").document(otherUser.getUid());
        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.callBack(true);
            }
        });
    }
    public void removeRequest(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){
        DocumentReference reference = FirebaseFirestore.getInstance().collection("user")
                .document(currentUser.getUid())
                .collection("msg-request").document(otherUser.getUid());
        reference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.callBack(true);
            }
        });
    }

    public void removeMessages(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback ){
        CollectionReference ref = FirebaseFirestore.getInstance().collection("messages")
                .document(currentUser.getUid())
                .collection(otherUser.getUid());
        ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().isEmpty()){
                        return;
                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            if (item.exists()){
                                ref.document(item.getId()).delete();
                            }
                        }
                        Log.d(TAG, "removeChat: " + "remove chat");
                        callback.callBack(true);
                    }
                }
            }
        });
    }

    public void checkChatIsExistOnOtherUser(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){
        CollectionReference reference = FirebaseFirestore.getInstance().collection("messages")
                .document(otherUser.getUid())
                .collection(currentUser.getUid());
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().isEmpty()){
                        callback.callBack(false);
                    }else{
                        callback.callBack(true);
                    }
                }
            }
        });
    }
    public void removeAllStorage(CurrentUser currentUser , OtherUser otherUser , TrueFalse<Boolean> callback){
        FirebaseStorage urlReference = FirebaseStorage.getInstance();
        Query reference = FirebaseFirestore.getInstance().collection("messages")
                .document(currentUser.getUid())
                .collection(otherUser.getUid());
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().getDocuments().isEmpty()){
                        callback.callBack(true);
                        return;
                    }else{
                        for (DocumentSnapshot item : task.getResult().getDocuments()){
                            if (!item.getString("type").equals("location")&&!item.getString("type").equals("text")&&item.getString("content") != null && !item.getString("content").isEmpty()){
                                urlReference.getReferenceFromUrl(item.getString("content")).delete();
                            }
                        }
                        Log.d(TAG, "removeChat: " + "remove storage");
                        removeMessages(currentUser, otherUser, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                callback.callBack(true);
                            }
                        });
                    }
                }
            }
        });

    }

    public  boolean isChatActive (Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d(TAG, "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        componentInfo.getPackageName();
        Log.d(TAG, "isChatActive: componentInfo.getPackageName(); " +  componentInfo.getPackageName());
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : l) {
            if (info.uid == context.getApplicationInfo().uid && info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Log.d(TAG, "isChatActive: " + info.getClass().getName().toString());
                Log.d(TAG, "isChatActive: " + info.processName);
                Log.d(TAG, "isChatActive: " + info);
                return true;
            }
        }
        return false;
    }
}
