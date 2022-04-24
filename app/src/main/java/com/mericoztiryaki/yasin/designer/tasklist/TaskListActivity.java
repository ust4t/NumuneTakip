package com.mericoztiryaki.yasin.designer.tasklist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mericoztiryaki.yasin.R;
import com.mericoztiryaki.yasin.actConfirmTask;
import com.mericoztiryaki.yasin.core.arch.BaseActivity;
import com.mericoztiryaki.yasin.core.task.AppTaskExecutor;
import com.mericoztiryaki.yasin.designer.taskform.TaskFormActivity;
import com.mericoztiryaki.yasin.login.LoginActivity;
import com.mericoztiryaki.yasin.model.LocalUser;
import com.mericoztiryaki.yasin.model.response.TaskStatus;
import com.mericoztiryaki.yasin.util.LoadingDialog;
import com.mericoztiryaki.yasin.util.persistance.LocalUserHandler;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends BaseActivity<TaskListPresenter> implements TaskListView {

    private LocalUser localUser;

    private LoadingDialog loadingDialog;
    private Switch isTodaySwitch;
    private Switch isCompletedSwitch;

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;
    private TaskListAdapter taskListAdapter;

    @NonNull
    @Override
    protected TaskListPresenter createPresenter(@NonNull Context context) {
        return new TaskListPresenter(this, new AppTaskExecutor(this));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_designer_task_list);

        localUser = new LocalUserHandler(this).getLocalUser();

        getSupportActionBar().setTitle("#" + localUser.getUsername());

        CompoundButton.OnCheckedChangeListener listener =
                (compoundButton, b) -> presenter.fetchData(localUser.getId(), isTodaySwitch.isChecked(), isCompletedSwitch.isChecked());

        isTodaySwitch = findViewById(R.id.isTodaySwitch);
        isTodaySwitch.setOnCheckedChangeListener(listener);

        isCompletedSwitch = findViewById(R.id.isCompletedSwitch);
        isCompletedSwitch.setOnCheckedChangeListener(listener);

        swipeContainer = findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(() -> {
            presenter.fetchData(localUser.getId(), isTodaySwitch.isChecked(), isCompletedSwitch.isChecked());
            swipeContainer.setRefreshing(false);
        });

        recyclerView = findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskListAdapter = new TaskListAdapter(this);
        taskListAdapter.setTaskSelectListener(task -> {
            Intent intent = new Intent(TaskListActivity.this, TaskFormActivity.class);
            intent.putExtra(TaskFormActivity.ARG_TASK_EXTRA, task);
            startActivityForResult(intent, TaskFormActivity.REQUEST_CODE);
        });
        recyclerView.setAdapter(taskListAdapter);

        loadingDialog = new LoadingDialog(this, getString(R.string.task_form_loading));

        presenter.fetchData(localUser.getId(), isTodaySwitch.isChecked(), isCompletedSwitch.isChecked());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.designer_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            LocalUserHandler localUserHandler = new LocalUserHandler(this);
            localUserHandler.removeLocalUser();

            Intent intent = new Intent(TaskListActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.action_new_task) {
            try {
                Log.d("x3k", "Girdi");

                Intent intent = new Intent(TaskListActivity.this, TaskFormActivity.class);
                startActivityForResult(intent, TaskFormActivity.REQUEST_CODE);
            } catch (Exception ex) {
                Log.d("x3k", ex.getMessage() + " : " + ex.getCause());
                Log.e("x3k", ex.toString() + " : ");
                ex.printStackTrace();
            }
        } else if (item.getItemId() == R.id.action_pending_task) {

            Intent intent = new Intent(TaskListActivity.this, actConfirmTask.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == TaskFormActivity.REQUEST_CODE) {
            presenter.fetchData(localUser.getId(), isTodaySwitch.isChecked(), isCompletedSwitch.isChecked());
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
    public void dataFetched(List<TaskStatus> taskList) {
        taskListAdapter.setTaskList(taskList);
        taskListAdapter.notifyDataSetChanged();
    }

    private class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

        private Context context;
        private OnTaskSelectListener taskSelectListener;

        private List<TaskStatus> taskList;

        public TaskListAdapter(Context context) {
            this.context = context;
            this.taskList = new ArrayList<>();
        }

        public void setTaskList(List<TaskStatus> taskList) {
            this.taskList = taskList;
        }

        public void setTaskSelectListener(OnTaskSelectListener taskSelectListener) {
            this.taskSelectListener = taskSelectListener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private Context context;

            private ImageView imageView;
            private TextView idTextView;
            private TextView dateTextView;
            private TextView quantityTextView;
            private TextView descriptionTextView;
            private TextView statusTextView;
            private TextView isStartedTextView;

            public ViewHolder(Context context, @NonNull View itemView) {
                super(itemView);
                this.context = context;
                imageView = itemView.findViewById(R.id.row_image);
                idTextView = itemView.findViewById(R.id.idTextView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                quantityTextView = itemView.findViewById(R.id.quantityTextView);
                descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
                statusTextView = itemView.findViewById(R.id.statusTextView);
                isStartedTextView = itemView.findViewById(R.id.startingStatusTextView);
            }

            public void bindTask(TaskStatus task) {
                Glide.with(context)
                        .load(task.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);

                idTextView.setText("#" + task.getId() + " - " + task.getProductTypeName());
                dateTextView.setText(task.getFormattedCreationTime("dd-MM-yyyy"));
                quantityTextView.setText("Adet: " + task.getQuantity());
                descriptionTextView.setText(task.getDescription());
                itemView.setBackgroundColor(getResources().getColor(task.getIsExpress() == 1 ? R.color.colorExpressBg : android.R.color.white));

                String durum = "";
                switch (task.getIsCompleted()) {
                    case 0:
                        durum = "Beklemede";

                        switch (task.getIsStarted()) {
                            case 1:
                                durum = "İşlemde";
                        }

                        break;
                    case 1:
                        durum = "Tamamlandı";
                        break;
                    case 2:
                        durum = "Onay Bekliyor..";
                        break;
                    case -1:
                        durum = "İptal Edildi";
                        break;
                    default:
                }
                isStartedTextView.setText(durum);
                if (task.getIsCompleted() == 0) {
                    statusTextView.setText("Sorumlu: " + task.getResponsibleUsername() + " (" + task.getDepartment() + ")");
                } else {
                    statusTextView.setText("");
                }
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.designer_task_list_row, parent, false);
            ViewHolder viewHolder = new ViewHolder(context, view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindTask(taskList.get(position));
            holder.itemView.setOnClickListener(view -> {
                if (taskSelectListener != null) {
                    taskSelectListener.onItemClick(taskList.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

    }

    public interface OnTaskSelectListener {

        void onItemClick(TaskStatus task);

    }
}


