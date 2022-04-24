package com.mericoztiryaki.yasin.common.taskdetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mericoztiryaki.yasin.R;
import com.mericoztiryaki.yasin.ROS;
import com.mericoztiryaki.yasin.RestApi.ApiResponse;
import com.mericoztiryaki.yasin.RestApi.ManagerAll;
import com.mericoztiryaki.yasin.UserType;
import com.mericoztiryaki.yasin.common.tasklist.TaskListActivity;
import com.mericoztiryaki.yasin.core.arch.BaseActivity;
import com.mericoztiryaki.yasin.core.task.AppTaskExecutor;
import com.mericoztiryaki.yasin.login.LoginActivity;
import com.mericoztiryaki.yasin.model.LocalUser;
import com.mericoztiryaki.yasin.model.response.TaskStatus;
import com.mericoztiryaki.yasin.util.ImageActivity;
import com.mericoztiryaki.yasin.util.LoadingDialog;
import com.mericoztiryaki.yasin.util.persistance.LocalUserHandler;
import com.mericoztiryaki.yasin.util.persistance.LocalWorkHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends BaseActivity<TaskDetailPresenter> implements TaskDetailView, View.OnClickListener {

    public static final int REQUEST_CODE = 2;
    public static final String ARG_TASK_ID_EXTRA = "task_id_extra";

    private LocalUser localUser;
    private TaskStatus task;

    private LoadingDialog loadingDialog;

    private ImageView imageView;
    private TextView modelTextView;
    private TextView materialTextView;
    private TextView quantityTextView;
    private TextView descriptionTextView;
    private TextView productTypeTextView;
    private Switch outsourcedSwitch;
    private Switch expressSwitch;

    private ProgressBar progressBar;
    private TextView remainingTextView;
    private Button progressButton;
    private Button btnPause;

    private boolean isValidToCount;

    private State state = State.IDLE;

    private int interval;
    private int remainingMinutes;

    private Handler timerHandler;

    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            remainingMinutes -= 1;

            if (remainingMinutes <= 0) {
                if (state == State.COUNTING) {
                    Log.d("TaskDetail", "Counter reached end. Showing extra time dialog");
                    showNormalTimeEndDialog();
                } else if (state == State.EXTRA) {
                    Log.d("TaskDetail", "Extra time counter reached end. Showing extra end dialog.");
                    showExtraTimeEndDialog();
                }
                return;
            }

            if (state == State.COUNTING) {
                remainingTextView.setText(remainingMinutes + "dk kaldı.");
            } else if (state == State.EXTRA) {
                remainingTextView.setText("Ekstra " + remainingMinutes + "dk kaldı.");
            }

            progressBar.setProgress(interval - remainingMinutes);

            timerHandler.postDelayed(this, 1000 * 60);

            Log.d("TaskDetail", "On tick... Remaining: " + remainingMinutes);
        }

    };

    boolean pause = false;
    int taskID = -1;
    Button btnSelectDate, btnSelectTime, btnUpdateTaskDate, btnDownloadFile;
    TextView txtDateYear, txtFileName;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    int hour, minute;
    String strDate = "";
    String strTime = "";
    Activity act;
    LinearLayout lytUpdateDeadLine,lytFile;

    @NonNull
    @Override
    protected TaskDetailPresenter createPresenter(@NonNull Context context) {
        return new TaskDetailPresenter(this, new AppTaskExecutor(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        act = TaskDetailActivity.this;

        int taskId = getIntent().getIntExtra(ARG_TASK_ID_EXTRA, -1);
        if (taskId == -1) {
            throw new RuntimeException("Task detail activity can not instantiated without task id");
        }
        this.taskID = taskId;
        getSupportActionBar().setTitle("Numune #" + taskId);

        localUser = new LocalUserHandler(this).getLocalUser();
        loadingDialog = new LoadingDialog(this, getString(R.string.task_detail_loading));

        imageView = findViewById(R.id.imageView);

        modelTextView = findViewById(R.id.modelTextView);
        materialTextView = findViewById(R.id.materialTextView);
        quantityTextView = findViewById(R.id.quantityTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        productTypeTextView = findViewById(R.id.productTypeTextView);
        outsourcedSwitch = findViewById(R.id.outsourceSwitch);
        expressSwitch = findViewById(R.id.expressSwitch);
        btnPause = findViewById(R.id.btnPause);
        lytFile = findViewById(R.id.lytFile);


        btnDownloadFile = findViewById(R.id.btnDownloadFile);
        txtFileName = findViewById(R.id.txtFileName);

        progressBar = findViewById(R.id.progressBar);
        remainingTextView = findViewById(R.id.remaininTextView);
        progressButton = findViewById(R.id.progressButton);

        btnSelectDate = findViewById(R.id.btnSelectTime);
        btnSelectTime = findViewById(R.id.btnSelectDate);
        txtDateYear = findViewById(R.id.txtDateYear);
        btnUpdateTaskDate = findViewById(R.id.btnUpdateTaskDate);
        lytUpdateDeadLine = findViewById(R.id.lytUpdateDeadLine);

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        btnDownloadFile.setOnClickListener(this);
        btnSelectDate.setOnClickListener(this);
        btnSelectTime.setOnClickListener(this);
        btnUpdateTaskDate.setOnClickListener(this);
        progressButton.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnPause.setVisibility(View.GONE);

        isValidToCount = false;

        presenter.fetchTask(taskId);

        timerHandler = new Handler();


    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.fetchInterval(taskID);
        Log.d("TaskDetail", "On resume method asks interval to backend");
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        Log.d("TaskDetail", "Counter stopped because activity paused.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            LocalUserHandler localUserHandler = new LocalUserHandler(this);
            localUserHandler.removeLocalUser();

            Intent intent = new Intent(TaskDetailActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (state != State.IDLE) {
            showInfoToast();
        } else {
            openListActivity();
        }
    }

    @Override
    public void showLoadingDialog() {
        loadingDialog.show();
    }

    @Override
    public void closeLoadingDialog() {
        loadingDialog.dismiss();
    }

    @Override
    public void onTaskFetched(TaskStatus task) {
        if (task != null) {
            this.task = task;
            if (localUser.getUserType() == UserType.URGE) {
                lytUpdateDeadLine.setVisibility(View.VISIBLE);
                if(task.getFilePath()!=null){
                    lytFile.setVisibility(View.VISIBLE);
                }else{
                    lytFile.setVisibility(View.GONE);
                }
            } else {
                lytUpdateDeadLine.setVisibility(View.GONE);

            }
            Glide.with(this)
                    .load(task.getImageUrl())
                    .into(imageView);
            imageView.setOnClickListener(v -> startActivity(ImageActivity.newInstance(TaskDetailActivity.this, task.getImageUrl())));

            modelTextView.setText(task.getModelName() + " (" + task.getModelRef() + ")");
            materialTextView.setText(task.getMaterialName() + " (" + task.getMaterialRef() + ")");
            quantityTextView.setText(String.valueOf(task.getQuantity()));
            descriptionTextView.setText(task.getDescription());
            productTypeTextView.setText(task.getProductTypeName());
            outsourcedSwitch.setChecked(task.getIsOutSourced() == 1);
            expressSwitch.setChecked(task.getIsExpress() == 1);

            if (task.getResponsibleUsername() == null || task.getResponsibleId() != localUser.getId()) {
                isValidToCount = false;
                progressButton.setText("Tamamlanmış");
                progressButton.setBackgroundColor(getResources().getColor(R.color.colorButtonGrey));
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.task_detail_error_title))
                    .setMessage(R.string.task_detail_error_msg)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
    }

    @Override
    public void onIntervalFetched(int interval) {
        Log.d("TaskDetail", "Interval fetched: " + interval);
        this.interval = interval;
        this.remainingMinutes = interval;
        progressBar.setMax(interval);
        isValidToCount = task != null && task.getIsCompleted() != 1;

        remainingTextView.setText(interval + "dk kaldı.");

        resumeCounterIfNeeded();
    }

    @Override
    public void onTaskEnded() {
        openListActivity();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnDownloadFile:
                ROS.log("File : " + task.getFilePath());
                ROS.openLink(act, task.getFilePath());
                break;
            case R.id.btnSelectTime:
                selectDate();
                break;

            case R.id.btnSelectDate:
                selectTime();
                break;
            case R.id.btnUpdateTaskDate:
                if (checkDateInputValue()) {
                    updateTaskDate();
                } else {
                    ROS.sweetWarning(act, "Tarih Ve Saati Seçiniz..");
                }
                break;
            default:
                if (!isValidToCount) {
                    return;
                }

                if (state == State.IDLE) {
                    state = State.COUNTING;

                    presenter.startTask(task.getId());

                    LocalWorkHandler localWorkHandler = new LocalWorkHandler(this);
                    localWorkHandler.saveLocalWork(localUser.getId(), task.getId(), interval, Calendar.getInstance().getTimeInMillis());

                    timerHandler.postDelayed(timerRunnable, 1000 * 60);

                    progressButton.setText("Bitir");
                    btnPause.setVisibility(View.VISIBLE);
                    btnPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pause = !pause;
                            if (pause) {
                                timerHandler.removeCallbacks(timerRunnable);
                                btnPause.setBackgroundColor(getResources().getColor(R.color.ef_grey));
                                btnPause.setText("Devam Et");
                            } else {
                                btnPause.setBackgroundColor(getResources().getColor(R.color.red));
                                btnPause.setText("Duraklat");
                                timerHandler.postDelayed(timerRunnable, 1000 * 60);
                            }

                        }
                    });
                    progressButton.setBackgroundColor(getResources().getColor(R.color.colorButtonRed));

                    Log.d("TaskDetail", "Counting started with button press");
                } else if (state == State.COUNTING || state == State.EXTRA) {
                    state = State.IDLE;

                    timerHandler.removeCallbacks(timerRunnable);

                    showFinishTaskDialog();

                    Log.d("TaskDetail", "Counting ended with button press");
                }
        }


    }

    private void selectDate() {

        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(act, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String tmpYear = "" + year;
                String tmpMonth = "" + month;
                String tmpDayOfMonth = "" + dayOfMonth;

                if (month < 10) {
                    tmpMonth = "0" + month;
                }

                if (dayOfMonth < 10) {
                    tmpDayOfMonth = "0" + dayOfMonth;
                }
                strDate = tmpYear + "-" + tmpMonth + "-" + tmpDayOfMonth;
                convertStringToDate(strDate, strTime);

            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void selectTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(act, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String tmpMinute = "" + minute;
                String tmpHour = "" + hour;
                if (minute < 10) {
                    tmpMinute = "0" + minute;
                }
                if (hourOfDay < 10) {
                    tmpHour = "0" + hourOfDay;
                }

                strTime = tmpHour + ":" + tmpMinute;
                convertStringToDate(strDate, strTime);

            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void resumeCounterIfNeeded() {
        LocalWorkHandler localWorkHandler = new LocalWorkHandler(this);
        if (localWorkHandler.isLocalWorkExists()) {
            Log.d("TaskDetail", "-------------------------------------------------");
            Log.d("TaskDetail", "Local work found in on resume, counter preparing.");
            showInfoToast();

            long startingTime = localWorkHandler.getLocalWork().getStartingTime();
            long now = Calendar.getInstance().getTimeInMillis();

            long diffInMinutes = ((now - startingTime) / (60 * 1000) % 60);
            remainingMinutes = (int) (interval - diffInMinutes);

            state = localWorkHandler.getLocalWork().hasExtraTime() ? State.EXTRA : State.COUNTING;
            Log.d("TaskDetail", "Timer restored for : " + state);

            timerHandler.postDelayed(timerRunnable, 0);

            progressButton.setText("Bitir");
            progressButton.setBackgroundColor(getResources().getColor(R.color.colorButtonRed));

            Log.d("TaskDetail", "Starting time: " + startingTime);
            Log.d("TaskDetail", "Now          : " + now);
            Log.d("TaskDetail", "Difference   : " + diffInMinutes + " minutes.");
            Log.d("TaskDetail", "-------------------------------------------------");
        }
    }

    private void showFinishTaskDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.task_detail_end_title))
                .setMessage(R.string.task_detail_end_msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        endTask();

                        Log.d("TaskDetail", "Task finished");
                    }
                })
                .show();
    }

    private void showExtraTimeEndDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.task_detail_end_title))
                .setMessage("Numune sayacı bitti. Ekstra istenen süre: " + interval + "dk. Kullanılan süre: " + (interval - remainingMinutes) + "dk.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        endTask();

                        Log.d("TaskDetail", "Extra time ended");
                    }
                })
                .show();
    }

    private void showNormalTimeEndDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.task_detail_extra_title);
        builder.setMessage(R.string.task_detail_extra_msg);
        builder.setCancelable(false);

        final MaterialEditText input = new MaterialEditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint(getString(R.string.task_detail_extra_time_hint));
        builder.setView(input);

        builder.setPositiveButton(R.string.task_detail_extra_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!input.getText().toString().equals("")) {
                    int extraTime = Integer.parseInt(input.getText().toString());

                    TaskDetailActivity.this.interval = extraTime;
                    TaskDetailActivity.this.remainingMinutes = extraTime;
                    TaskDetailActivity.this.progressBar.setMax(extraTime);
                    TaskDetailActivity.this.progressBar.setProgress(0);
                    TaskDetailActivity.this.remainingTextView.setText(extraTime + "dk kaldı.");


                    state = State.EXTRA;

                    LocalWorkHandler localWorkHandler = new LocalWorkHandler(TaskDetailActivity.this);
                    localWorkHandler.addExtraTime(extraTime, Calendar.getInstance().getTimeInMillis());

                    timerHandler.postDelayed(timerRunnable, 1000 * 60);

                    Log.d("TaskDetail", "Counting started with extra time.");

                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton(R.string.task_detail_extra_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                endTask();

                dialog.dismiss();

                Log.d("TaskDetail", "Normal counting ended and task finished.");
            }
        });

        builder.show();
    }

    private void endTask() {
        LocalWorkHandler localWorkHandler = new LocalWorkHandler(TaskDetailActivity.this);
        presenter.endTask(localUser.getId(), task.getId(), interval - remainingMinutes, localWorkHandler.getLocalWork());
        localWorkHandler.removeLocalWork();
    }

    private void showInfoToast() {
        Toast.makeText(this, "Devam eden işlem bulunuyor. Başka sayfaya geçmek için önce bu işlemi tamamlayın.", Toast.LENGTH_LONG).show();
    }

    private void openListActivity() {
        Intent intent = new Intent(TaskDetailActivity.this, TaskListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private enum State {
        COUNTING,
        IDLE,
        EXTRA
    }

    public void convertStringToDate(String date, String time) {
        String taskDate = date + " " + time + ":00";
        ROS.log("taskDate : " + taskDate);

        if (checkDateInputValue()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date parseDate = dateFormat.parse(taskDate);
                String formattedDate = dateFormat.format(parseDate);

                txtDateYear.setText(formattedDate);
                ROS.log("convertStringToDate : " + formattedDate);
            } catch (Exception ex) {
                ROS.loge("convertStringToDate Error : ", ex);
            }

        }

    }

    private void updateTaskDate() {
        ROS.showProgressBar(act, "Termin Tarihi Güncelleniyor..");


        String taskDate = strDate + " " + strTime + ":00";

        ROS.log("taskID : " + taskID + ", updateTaskDate : " + taskDate);
        retrofit2.Call<ResponseBody> call3 = ManagerAll.getInstance().updateTaskDate(taskID, taskDate);
        call3.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                // TODO: 23/06/2019 Cevap Başarılı mı kontrol et
                ROS.dismissProgressBar();

                String strjson = ROS.printResponse("Update Task Result : ", response);
                ApiResponse apiResponse = new Gson().fromJson(strjson, ApiResponse.class);
                if (!apiResponse.isError()) {
                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(act, SweetAlertDialog.SUCCESS_TYPE);

                    sweetAlertDialog
                            .setTitleText("Termin Tarihi Güncelleme")
                            .setContentText("Başarılı")
                            .setConfirmText("Tamam")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {

                                }
                            })
                            .show();
                } else {
                    ROS.sweetFailed(act, "İşlem Başarısız.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ROS.sweetFailed(act, "Başarısız");
                ROS.dismissProgressBar();

                ROS.loge("onFailure Hata : ", t);

            }
        });
    }

    public boolean checkDateInputValue() {
        return !strDate.equalsIgnoreCase("") && !strTime.equalsIgnoreCase("");
    }
}
