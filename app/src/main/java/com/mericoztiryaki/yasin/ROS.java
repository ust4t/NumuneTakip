package com.mericoztiryaki.yasin;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mericoztiryaki.yasin.RestApi.ApiResponse;
import com.mericoztiryaki.yasin.RestApi.ManagerAll;
import com.mericoztiryaki.yasin.model.UserPojo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ROS extends Activity {
    public static String hash = "x3k";
    // TODO: 23/05/2019 Burayı yapabilirsen daha güvenli yap
    public static String API_KEY = "Rast?12?mobileapi";
    public static boolean isDebugMode = true;

    public static void log(String debugMesaj) {
        Log.d("x3k ", "" + debugMesaj);
    }

    public static SweetAlertDialog pDialog = null;
    public static JsonObject jsonUserInfo = new JsonObject();

    public static long daysBetween(Date startDate, Date endDate) {
        Calendar sDate = getDatePart(startDate);
        Calendar eDate = getDatePart(endDate);

        long daysBetween = 0;
        if (sDate.before(eDate)) {
            while (sDate.before(eDate)) {
                sDate.add(Calendar.DAY_OF_MONTH, 1);
                daysBetween++;
            }
        } else if (sDate.after(eDate)) {
            while (sDate.after(eDate)) {
                sDate.add(Calendar.DAY_OF_MONTH, -1);
                daysBetween--;
            }
        }


        return daysBetween;
    }

    public static void openGoogleMap(Activity act, String address) {
        //address = (address.trim()).replaceAll(" ", "+");
        // address = (address.trim()).replaceAll("/", "+");
        //address =address.replaceAll("[^a-zA-Z0-9]", "");

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        act.startActivity(mapIntent);

    }

    public static String printResponse(String title, Response<ResponseBody> response) {
        String strJson = "";
        ROS.log("-------------" + title + "" + "------------------------------------------------------------------");

        try {
            ROS.log("| login body: " + response.body());

            if (response.body() != null) {
                ROS.log("| login toString: " + response.body().toString());
            }

            ROS.log("| login message: " + response.message());
            ROS.log("| login errorBody : " + response.errorBody());
            ROS.log("| login code : " + response.code());
            ROS.log("| login raw : " + response.raw());

            strJson = response.body().string();
            ROS.log("| login string: " + strJson);
        } catch (Exception ex) {
            ROS.loge(title + " printResponse Error : ", ex);
        }


        ROS.log("--------#END_" + title + "" + "------------------------------------------------------------------" + " |");
        return strJson;
    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    public static long dateToLong(String date) {

        long milliseconds = 0;
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-M-dd");


            if (date.contains(":")) {
                f = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
            }
            ROS.log("Tarih : " + date);

            Date parseDate = null;
            try {
                parseDate = f.parse(date);
                milliseconds = parseDate.getTime();

            } catch (ParseException e) {
                ROS.loge("Time Parse Error : ", e);
                e.printStackTrace();
            }
            ROS.log("Tarih : " + milliseconds);
        } catch (Exception ex) {
            ROS.loge("dateToLong Error :  ", ex);
        }

        return milliseconds;
    }

    /*
        public static void setRecyclerAdapter(Activity act, RecyclerView recyclerView, TaskAdapter adapter) {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(act));

            recyclerView.setAdapter(adapter);
        }
    */
    public static void showProgressBar(Activity act, String message) {
        if (pDialog != null) {
            try {
                pDialog.dismiss();
            } catch (Exception ex) {
                ROS.loge("Progressbar dismiss hatası : ", ex);
            }
        }
        pDialog = new SweetAlertDialog(act, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(message);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    public static void dismissProgressBar() {
        if (pDialog != null) {
            try {
                pDialog.dismissWithAnimation();
            } catch (Exception ex) {
                ROS.loge("dismissProgressBar : ", ex);
            }
        }

    }

    public static void vibrate(Activity act, int milisaniye) {
        /*
        Vibrator v = (Vibrator) act.getSystemService(VIBRATOR_SERVICE);
        v.vibrate(milisaniye);
        */
    }

    public static String getTime() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c);
        ROS.log("Tarih : " + formattedDate);

        return formattedDate;
    }

    public static String getDate() {
        Date c = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd");
        String formattedDate = df.format(c);
        ROS.log("Tarih : " + formattedDate);

        return formattedDate;
    }


    public static void loge(Exception ex) {
        Log.e("x3k ", "Method : " + ex.getStackTrace()[0].getMethodName()
                + ",Line : " + ex.getStackTrace()[0].getLineNumber()
                + ex.getMessage());
        ex.printStackTrace();
        ROS.loge(Log.getStackTraceString(ex));
    }

    public static void loge(String debugMesaj, Exception ex) {
        Log.e("x3k ", debugMesaj + " .Method : " + ex.getStackTrace()[0].getMethodName()
                + ",Line : " + ex.getStackTrace()[0].getLineNumber()
                + ex.getMessage());
        ex.printStackTrace();
        ROS.loge(Log.getStackTraceString(ex));
    }

    public static void loge(String debugMesaj, Throwable ex) {
        Log.e("x3k ", debugMesaj + " .Method : " + ex.getStackTrace()[0].getMethodName()
                + ",Line : " + ex.getStackTrace()[0].getLineNumber()
                + ex.getMessage());
        Log.e("x3k", ex.toString());
        ex.printStackTrace();
        ROS.loge(Log.getStackTraceString(ex));
    }

    public static void loge(String debugMesaj) {
        Log.e("x3k ", debugMesaj);

    }

    public static boolean isEmpty(final CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static void toast(Activity act, String debugMesaj) {
        Toast.makeText(act, debugMesaj, Toast.LENGTH_LONG).show();
    }

    public static void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> cls, boolean isClearTop) {
        Intent intent = new Intent(context, cls);
        if (isClearTop) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static boolean isNetworkConnected(Activity act) {
        ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static void networkError(Activity act) {
        new SweetAlertDialog(act, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("İnternet Bağlantınızı Kontrol Edin.")
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        System.exit(0);
                    }
                })
                .show();
    }

    public static void exitApplication(final Activity act) {
        new SweetAlertDialog(act, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Çıkmak İstiyor Musunuz ?")
                .setContentText("")
                .setConfirmText("Evet")
                .setCancelText("Hayır")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        act.finish();
                        System.exit(0);

                    }
                })
                .show();
    }

    //public static UserPojo[] userPojos;

    public static void loadUsers(final Activity act, final Spinner spnDistrict) {

        final List<String> userList;
        userList = new ArrayList<>();


        userList.add("Modelist Seçiniz..");
        Call<ResponseBody> callUserList = ManagerAll.getInstance().getModelists();
        callUserList.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String jsonData = printResponse("callUserList : ", response);
                try {
                    ApiResponse apiResponse = new Gson().fromJson(jsonData, ApiResponse.class);

                    UserPojo[] modelists = apiResponse.getModelists();

                    for (int i = 0; i < modelists.length; i++) {
                        userList.add(modelists[i].getUsername());
                    }

                } catch (Exception ex) {
                    ROS.loge("LoadUsers Error : ", ex);
                } finally {
                    MySpinnerAdapter<String> dataAdapter = new MySpinnerAdapter<>(act, R.layout.custom_spinner, userList);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnDistrict.setAdapter(dataAdapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ROS.loge("onFailure Error : ", t);

            }
        });//#END_Call


    }

    public static void logout(final Activity act) {
        /*
        new SweetAlertDialog(act, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Güvenli Çıkış")
                .setContentText("Çıkmak İstediğinize Emin Misiniz?")
                .setConfirmText("Evet")
                .setCancelText("Hayır")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        EncryptedPreferences encryptedPreferences =
                                new EncryptedPreferences.Builder(act)
                                        .withEncryptionPassword(ROS.hash).build();
                        encryptedPreferences.edit().clear().apply();
                        act.finish();
                        System.exit(0);

                    }
                })
                .show();
        */
    }

    public static void sweetFailed(Activity act, String message) {
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(act, SweetAlertDialog.ERROR_TYPE);

        sweetAlertDialog
                .setTitleText("ERROR")
                .setContentText(message)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }


    public static void showNotification(Activity act, String message) {
        /*
        NotificationManager mNotificationManager =
                (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTIFICATION_CHANNEL_ID = "Teleofis";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Teleofis",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(act, NOTIFICATION_CHANNEL_ID);


        notificationBuilder
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(act, R.color.blue_btn_bg_color))
                .setContentTitle("Sayın  ")
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher);


        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(1, notificationBuilder.build());
        */
    }

    public static boolean isValidEmail(Activity act, CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            sweetWarning(act, "Email Adresinizi Giriniz.");
            return false;
        } else {
            boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
            if (!isValid) {
                sweetWarning(act, "Emailiniz,standart email formatlarıyla uyuşmuyor.");
                return false;
            } else {

                return true;
            }
        }
    }

    public static boolean isValidUserName(Activity act, String username) {
        if (username.length() < 3) {
            sweetWarning(act, "Kullanıcı Adınız En Az 3 Karakter Olmalı");
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidPass(Activity act, String pass, String checkPass) {
        if (!pass.equals(checkPass)) {
            sweetWarning(act, "Şifreler Uyuşmuyor.");
            return false;
        } else if (pass.length() < 6) {
            sweetWarning(act, "Şifreniz En Az 6 Karakter Olmalıdır.");
            return false;
        } else {
            return true;
        }
    }

    public static void sweetWarning(Activity act, String message) {
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(act, SweetAlertDialog.WARNING_TYPE);

        sweetAlertDialog
                .setTitleText("Uyarı")
                .setContentText(message)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }


    public static void sweetSuccess(Activity act, String message) {
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(act, SweetAlertDialog.SUCCESS_TYPE);

        sweetAlertDialog
                .setTitleText("Başarılı")
                .setContentText(message)
                .setConfirmText("OK")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }


    public static void openLink(Context context, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    private static class MySpinnerAdapter<S> extends ArrayAdapter<String> {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "font-Regular.otf");


        private MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTextColor(Color.WHITE);
            view.setTypeface(font);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }
    }
}
