package com.vaye.app.LoginRegister.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.vaye.app.Controller.HomeController.HomeActivity;
import com.vaye.app.Interfaces.CurrentUserService;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.BolumModel;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.TaskUser;
import com.vaye.app.R;
import com.vaye.app.Services.StudentSignUpService;
import com.vaye.app.Services.TeacherRegisterService;
import com.vaye.app.Services.UserService;
import com.vaye.app.SplashScreen.SplashScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BolumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<BolumModel> list;
    Context context;
    TaskUser taskUser;
    String fakulte;
    public void setList(ArrayList<BolumModel> list) {
        this.list = list;
    }

    public BolumAdapter(String fakulte, ArrayList<BolumModel> list, Context context, TaskUser taskUser) {
        this.list = list;
        this.context = context;
        this.taskUser = taskUser;
        this.fakulte = fakulte;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fakulte_bolum_item, parent, false);
        return new FakulteAdapterVievHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FakulteAdapterVievHolder fakulteAdapterVievHolder = (FakulteAdapterVievHolder)holder;
        BolumModel item = list.get(position);
        fakulteAdapterVievHolder.setName(item.getValue());
        fakulteAdapterVievHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskUser.getPriority().equals("student")){
                    CustomDialog.show((AppCompatActivity) context, R.layout.auth_dialog, new CustomDialog.OnBindView() {
                        @Override
                        public void onBind(CustomDialog dialog, View v) {
                            TextView header = (TextView)v.findViewById(R.id.headerTitle);
                            TextView mainText =(TextView)v.findViewById(R.id.mainText);
                            Button done = (Button)v.findViewById(R.id.okey);
                            header.setText("Kaydı Tamamla");
                            mainText.setText("Onaylarsanız Girdiniz Bilgileri Değiştiremezsiniz\n Kaydı Tamamla");
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.doDismiss();
                                }
                            });
                        }
                    }).setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WaitDialog.show((AppCompatActivity)context,"Kayıt Tamamlanıyor");
                            StudentSignUpService.shared().completeSignUp(context, taskUser,"student", item.getValue(), item.getKey(), fakulte, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        UserService.shared().getCurrentUser(taskUser.getUid(), new CurrentUserService() {
                                            @Override
                                            public void onCallback(CurrentUser user) {
                                                Intent i = new Intent(context , SplashScreen.class);
                                                context.startActivity(i);
                                                ((AppCompatActivity) context).finish();
                                                WaitDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }else if (taskUser.getPriority().equals("teacher")){
                    CustomDialog.show((AppCompatActivity) context, R.layout.auth_dialog, new CustomDialog.OnBindView() {
                        @Override
                        public void onBind(CustomDialog dialog, View v) {
                            TextView header = (TextView)v.findViewById(R.id.headerTitle);
                            TextView mainText =(TextView)v.findViewById(R.id.mainText);
                            Button done = (Button)v.findViewById(R.id.okey);
                            header.setText("Kaydı Tamamla");
                            mainText.setText("Onaylarsanız Girdiniz Bilgileri Değiştiremezsiniz\n Kaydı Tamamla");
                            done.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.doDismiss();
                                }
                            });
                        }
                    }).setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WaitDialog.show((AppCompatActivity)context,"Kayıt Tamamlanıyor");
                            StudentSignUpService.shared().completeSignUp(context, taskUser,"student", item.getValue(), item.getKey(), fakulte, new TrueFalse<Boolean>() {
                                @Override
                                public void callBack(Boolean _value) {
                                    if (_value){
                                        WaitDialog.show((AppCompatActivity)context,"Lütfen Bekleyin...");
                                        TeacherRegisterService.shared().completeSignUp(taskUser, item.getValue(), fakulte,item.getKey(), new TrueFalse<Boolean>() {
                                            @Override
                                            public void callBack(Boolean _value) {
                                                if (_value){
                                                    DocumentReference ref = FirebaseFirestore.getInstance().collection("task-teacher")
                                                            .document(taskUser.getUid());
                                                    ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                String tokenID = FirebaseInstanceId.getInstance().getToken();
                                                                final Map<String,Object> map=new HashMap<>();
                                                                map.put("tokenID",tokenID);
                                                                DocumentReference db = FirebaseFirestore.getInstance().collection("user")
                                                                        .document(taskUser.getUid());
                                                                db.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            Intent i = new Intent(context,SplashScreen.class);
                                                                            context.startActivity(i);
                                                                            ((AppCompatActivity) context).finish();
                                                                            WaitDialog.dismiss();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
