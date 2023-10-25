package com.ozanyazici.javaworkmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Bundle;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Data data = new Data.Builder().putInt("intKey",1).build();

        Constraints constraints = new Constraints.Builder() //Sınırlamalar
                //.setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(false)
                .build();

        /*
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class)
                .setConstraints(constraints)
                .setInputData(data)
                //.setInitialDelay(5, TimeUnit.MINUTES) //uygulama başladıktan sonra ne kadar süre sonra çalışacak onu belirtiyoruz
                //.addTag("myTag") //birden fazla rquest olma durumlarında karışmamaları için tag ekliyorum
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);
         */

        WorkRequest workRequest = new PeriodicWorkRequest.Builder(RefreshDatabase.class,15,TimeUnit.MINUTES) //PeriodicWorkRequest en sık 15 dakikada bir çalışır.
                .setConstraints(constraints)
                .setInputData(data)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);


        //work ün durumu hakkında bilgi veren kod bloğu
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.getId()).observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                if(workInfo.getState() == WorkInfo.State.RUNNING){
                    System.out.println("running");
                } else if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                    System.out.println("succeded");
                } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                    System.out.println("failed");
                }
            }
        });

        //diyelimki yukarıdaki koddan work ün faillediğini gördüm burda da o workü veya hepsini iptal edebilirim.
        //WorkManager.getInstance(this).cancelAllWork();

        //Chaining  Chain yapmak için oneTimeRequest kullanmalıyız Periodic ile olmuyor.

        /*
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(RefreshDatabase.class)
                .setInputData(data)
                .setConstraints(constraints)
                .build();

        //bir defa olacak şekilde sırayla çalışacak işleri böyle yazabiliyorum.
        WorkManager.getInstance(this).beginWith(oneTimeWorkRequest)
                .then(oneTimeWorkRequest)
                .then(oneTimeWorkRequest)
                .enqueue();
         */


    }
}