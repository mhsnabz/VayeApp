package com.vaye.app.LoginRegister;

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
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vaye.app.Interfaces.DomainName;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.SchoolModel;
import com.vaye.app.R;
import com.vaye.app.Services.TeacherRegisterService;
import com.vaye.app.Util.Helper;

import java.util.ArrayList;


public class TeacherSignUp extends Fragment {
    String TAG = "TeacherSignUp";
    View rootView;
    Spinner spinner;
    MaterialEditText name , email , password;
    Button createAccount;
    String unvan;
    SchoolModel model;

    public TeacherSignUp(SchoolModel model) {
        this.model = model;
    }

    public TeacherSignUp() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_teacher_sign_up, container, false);

        configureUI();

        return rootView;
    }



    private void configureUI(){
        spinner = (Spinner)rootView.findViewById(R.id.spinner);
        name = (MaterialEditText)rootView.findViewById(R.id.name);
        email = (MaterialEditText)rootView.findViewById(R.id.email);
        password = (MaterialEditText)rootView.findViewById(R.id.password);
        createAccount = (Button)rootView.findViewById(R.id.signUp);
        ArrayAdapter<CharSequence> qual_adapter = ArrayAdapter.createFromResource(getContext(), R.array.unvan,R.layout.spnr_qualification);
        qual_adapter.setDropDownViewResource(R.layout.drpdn_qual);

        spinner.setAdapter(qual_adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (adapterView.getAdapter().getItem(i).equals("Ünvan Seçiniz")){
                    spinner.setBackgroundColor(Color.GRAY);
                    unvan = null;
                }else{
                    spinner.setBackgroundColor(Color.RED);
                    unvan = adapterView.getAdapter().getItem(i).toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCreateAccount();
            }
        });
    }


    private String getShort_unvan(String unvan){
        if (unvan == null){
            return  null;
        }else{
            if (unvan.equals("Asistan")){
                return "Ast.";
            }else if (unvan.equals("Araştırma Görevlisi")){
                return "Ars. Gör.";
            }
            else if (unvan.equals("Öğretim Görevlisi")){
                return "Öğr. Gör.";
            }
            else if (unvan.equals("Doktor")){
                return "Dr.";
            }
            else if (unvan.equals("Doçent Doktor")){
                return "Doç. Dr.";
            }
            else if (unvan.equals("Profesör Doktor")){
                return "Prof. Dr.";
            }
            else {
                return null;
            }
        }


    }

    private void setCreateAccount(){
        WaitDialog.show((AppCompatActivity) getContext(),"Lütfen Bekleyin");
        if (getShort_unvan(unvan) != null){

            String _name = name.getText().toString();
            String _email = email.getText().toString();
            String _password = password.getText().toString();
            if (_name.isEmpty()){
                WaitDialog.dismiss();
                name.setError("Lütfen Adınız ve Soyadınızı Giriniz");
                name.requestFocus();
                return;
            }
            if (_name.length() < 6){
                WaitDialog.dismiss();
                name.setError("Lütfen Adınız ve Soyadınızı Giriniz");
                name.requestFocus();
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

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(_email,_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        WaitDialog.dismiss();
                        WaitDialog.show((AppCompatActivity)getContext(),"Hesap Oluşturuluyor");
                        TeacherRegisterService.shared().setTaskTeacher(getContext(),_name, getShort_unvan(unvan), _email, task.getResult().getUser().getUid(), "teacher", model, new TrueFalse<Boolean>() {
                            @Override
                            public void callBack(Boolean _value) {
                                if (_value){
                                    TeacherRegisterService.shared().setPriority(getContext(),task.getResult().getUser().getUid(), "teacher", new TrueFalse<Boolean>() {
                                        @Override
                                        public void callBack(Boolean _value) {
                                            if (_value){
                                                TeacherRegisterService.shared().setStatus(getContext(),task.getResult().getUser().getUid(), new TrueFalse<Boolean>() {
                                                    @Override
                                                    public void callBack(Boolean _value) {
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
                                                                        CustomDialog.show((AppCompatActivity) getContext(), R.layout.auth_dialog, new CustomDialog.OnBindView() {
                                                                            @Override
                                                                            public void onBind(CustomDialog dialog, View v) {
                                                                                TextView headerTitle = (TextView)v.findViewById(R.id.headerTitle);
                                                                                TextView mainText = (TextView)v.findViewById(R.id.mainText);
                                                                                Button okey = (Button)v.findViewById(R.id.okey);

                                                                                headerTitle.setText("Sayın "+ getShort_unvan(unvan) + _name);


                                                                                SpannableStringBuilder builder = new SpannableStringBuilder();

                                                                                String text1 = "Üniversitenizin kurumsal web sitesinden ";
                                                                                SpannableString redSpannable= new SpannableString(text1);
                                                                                redSpannable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text1.length(), 0);
                                                                                builder.append(redSpannable);

                                                                                String textName = "'"+getShort_unvan(unvan) + _name +"'";
                                                                                SpannableString textNameSpannable= new SpannableString(textName);
                                                                                textNameSpannable.setSpan(new ForegroundColorSpan(Color.BLUE), 0, textName.length(), 0);
                                                                                builder.append(textNameSpannable);


                                                                                String text3 = " araştıracağız. Size ";
                                                                                SpannableString text3Spanable= new SpannableString(text3);
                                                                                text3Spanable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text3.length(), 0);
                                                                                builder.append(text3Spanable);

                                                                                String text4 =  "destek@vaye.app" ;
                                                                                SpannableString tex4Span= new SpannableString(text4);
                                                                                tex4Span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text4.length(), 0);
                                                                                builder.append(tex4Span);



                                                                                String text5 = "'den en geç 24 saat içinde onay mesajı göndereceğiz. ";
                                                                                SpannableString text5Spanable= new SpannableString(text5);
                                                                                text5Spanable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text5.length(), 0);
                                                                                builder.append(text5Spanable);


                                                                                String text6 = "Bize ";
                                                                                SpannableString text6Spanable= new SpannableString(text6);
                                                                                text6Spanable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text6.length(), 0);
                                                                                builder.append(text6Spanable);


                                                                                String text7 =  "destek@vaye.app" ;
                                                                                SpannableString tex7Span= new SpannableString(text7);
                                                                                tex7Span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text7.length(), 0);
                                                                                builder.append(tex7Span);


                                                                                String text8 = "'den ulaşabilirsiniz";
                                                                                SpannableString text8Spanable= new SpannableString(text8);
                                                                                text8Spanable.setSpan(new ForegroundColorSpan(Color.GRAY), 0, text8.length(), 0);
                                                                                builder.append(text8Spanable);


                                                                                mainText.setText(builder,TextView.BufferType.SPANNABLE);


                                                                                okey.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View view) {
                                                                                        dialog.doDismiss();
                                                                                        Intent i = new Intent(getContext(),LoginActivity.class);
                                                                                        getContext().startActivity(i);
                                                                                        ((AppCompatActivity) getContext()).finish();
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                });

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
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: " + e.getMessage());
                    Log.d(TAG, "onFailure: " + e.getLocalizedMessage());
                    Log.d(TAG, "onFailure: " + e.getCause());
                    Log.d(TAG, "onFailure: " + e.getStackTrace());
                    Log.d(TAG, "onFailure: " + e.toString());

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
            TipDialog.show((AppCompatActivity)getContext(),"Lütfen Ünvan Seçiniz", TipDialog.TYPE.ERROR);
            TipDialog.dismiss(750);
            spinner.performClick();
        }
    }

}