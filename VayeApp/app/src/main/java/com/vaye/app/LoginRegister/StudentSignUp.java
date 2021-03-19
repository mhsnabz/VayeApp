package com.vaye.app.LoginRegister;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Interfaces.DomainName;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.R;
import com.vaye.app.Services.StudentSignUpService;
import com.vaye.app.Services.TeacherRegisterService;

import java.util.HashMap;


public class StudentSignUp extends Fragment {

    SchoolModel model;
    String TAG = "StudentSignUp";
    View rootView;
    MaterialEditText number , email , password;
    Button createAccount;
    public StudentSignUp(SchoolModel model) {
        this.model = model;
    }

    public StudentSignUp() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_student_sign_up, container, false);
        number = (MaterialEditText)rootView.findViewById(R.id.number);
        email = (MaterialEditText)rootView.findViewById(R.id.email);
        password = (MaterialEditText)rootView.findViewById(R.id.password);
        createAccount = (Button)rootView.findViewById(R.id.signUp);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCreateAccount();
            }
        });
        return rootView;
    }


    private void setCreateAccount() {
        WaitDialog.show((AppCompatActivity)getContext(),"Lütfen Bekleyin");
        String _number = number.getText().toString();
        String _email = email.getText().toString();
        String _password = password.getText().toString();
        if (_number.isEmpty()){
            WaitDialog.dismiss();
            number.setError("Lütfen Okul Numaranızı Giriniz");
            number.requestFocus();
            return;
        }
        if (_number.length() != 9){
            WaitDialog.dismiss();
            number.setError("Lütfen Geçerli Bir Numara Giriniz");
            number.requestFocus();
            return;
        }
        if (_email.isEmpty()){
            WaitDialog.dismiss();
            email.setError("Lütfen Geçerli Bir E-posta Giriniz");
            email.requestFocus();
            return;
        }
        if (!TeacherRegisterService.shared().getDoaminName(_email).equals(DomainName.iste)){
            WaitDialog.dismiss();
            email.setError(DomainName.iste + " Uzantılı E-postanızı Giriniz");
            email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(_email).matches()){
            WaitDialog.dismiss();
            email.setError("Lütfen Geçerli Bir E-posta Giriniz");
            email.requestFocus();
            return;
        }
        if (_password.isEmpty()){
            WaitDialog.dismiss();
            password.setError("Lütfen Bir Şifre Belirleyim");
            password.requestFocus();
            return;
        }
        if (_password.length()<6){
            WaitDialog.dismiss();
            password.setError("Lütfen Daha Güçlü Bir Şifre Belirleyin");
            password.requestFocus();
            return;
        }

        StudentSignUpService.shared().checkNumberIsExist(getContext(), model, _number, new TrueFalse<Boolean>() {
            @Override
            public void callBack(Boolean _value) {
                if (!_value){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(_email,_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                WaitDialog.dismiss();
                                WaitDialog.show((AppCompatActivity)getContext(),"Hesap Oluşturuluyor");
                                StudentSignUpService.shared().setTaskStudent(getContext(), _email, _email, _number, task.getResult().getUser().getUid(), "student", model, new TrueFalse<Boolean>() {
                                    @Override
                                    public void callBack(Boolean _value) {
                                        if (_value){
                                            StudentSignUpService.shared().setPriority(getContext(), task.getResult().getUser().getUid(), "student", new TrueFalse<Boolean>() {
                                                @Override
                                                public void callBack(Boolean _value) {
                                                    if (_value){
                                                        StudentSignUpService.shared().setStatus(getContext(), task.getResult().getUser().getUid(),false, new TrueFalse<Boolean>() {
                                                            @Override
                                                            public void callBack(Boolean _value) {
                                                                if (_value){
                                                                    WaitDialog.dismiss();
                                                                    FirebaseAuth.getInstance().signOut();
                                                                    CustomDialog.show((AppCompatActivity) getContext(), R.layout.auth_dialog, new CustomDialog.OnBindView() {
                                                                        @Override
                                                                        public void onBind(CustomDialog dialog, View v) {
                                                                            TextView headerTitle = (TextView)v.findViewById(R.id.headerTitle);
                                                                            TextView mainText = (TextView)v.findViewById(R.id.mainText);
                                                                            Button okey = (Button)v.findViewById(R.id.okey);

                                                                            headerTitle.setText("Kayıt Tamamlandı");
                                                                            String text = task.getResult().getUser().getEmail() + " ";
                                                                            String text2 = text + "adresine Bir Tane Doğrulama E-Postası Gönderdik. Lütfen E-Posta Adresinizi Doğrulayınız" ;

                                                                            Spannable spannable = new SpannableString(text2);

                                                                            spannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                                                            mainText.setText(spannable, TextView.BufferType.SPANNABLE);
                                                                            okey.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View view) {
                                                                                    dialog.doDismiss();

                                                                                }
                                                                            });
                                                                            dialog.setOnDismissListener(new OnDismissListener() {
                                                                                @Override
                                                                                public void onDismiss() {
                                                                                    Intent i = new Intent(getContext(),LoginActivity.class);
                                                                                    getContext().startActivity(i);
                                                                                    ((AppCompatActivity) getContext()).finish();
                                                                                }
                                                                            });

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

                            }else{
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity)getContext(),"HATA OLUŞTU\nTekrar Deneyin", TipDialog.TYPE.ERROR);
                                TipDialog.dismiss(750);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e.getLocalizedMessage().equals("The email address is already in use by another account.")){
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity)getContext(),"Bu E-posta Adresi Zaten Kayıtlı", TipDialog.TYPE.ERROR);

                                TipDialog.dismiss(1000);
                                email.setError("Bu E-posta Adresi Zaten Kayıtlı");
                                email.requestFocus();
                                return;
                            }else {
                                WaitDialog.dismiss();
                                TipDialog.show((AppCompatActivity)getContext(),"Lütfen İnternet Bağlantınızı Kontrol Ediniz", TipDialog.TYPE.ERROR);

                                TipDialog.dismiss(1000);

                                return;
                            }


                        }
                    });
                }else{
                    WaitDialog.dismiss();
                    number.setError("Bu Numaraya Kayıtlı Bir Kullanıcı Bulunuyor");
                    number.requestFocus();
                    return;
                }
            }
        });


    }


}