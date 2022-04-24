package com.mericoztiryaki.yasin.common.tasklist;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mericoztiryaki.yasin.R;
import com.mericoztiryaki.yasin.common.taskdetail.TaskDetailActivity;
import com.mericoztiryaki.yasin.core.arch.BaseActivity;
import com.mericoztiryaki.yasin.core.task.AppTaskExecutor;
import com.mericoztiryaki.yasin.designer.taskform.TaskFormActivity;
import com.mericoztiryaki.yasin.login.LoginActivity;
import com.mericoztiryaki.yasin.model.LocalUser;
import com.mericoztiryaki.yasin.model.response.TaskStatus;
import com.mericoztiryaki.yasin.util.LoadingDialog;
import com.mericoztiryaki.yasin.util.persistance.LocalUserHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
        setContentView(R.layout.activity_common_task_list);

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
            if (!isCompletedSwitch.isChecked() && taskListAdapter.isThereExpressTask() && task.getIsExpress() == 0) {
                Toast.makeText(TaskListActivity.this, "Ekspres olan numuneleri bitirin.", Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.ARG_TASK_ID_EXTRA, task.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        recyclerView.setAdapter(taskListAdapter);

        loadingDialog = new LoadingDialog(this, getString(R.string.task_form_loading));

        presenter.fetchData(localUser.getId(), isTodaySwitch.isChecked(), isCompletedSwitch.isChecked());
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

            Intent intent = new Intent(TaskListActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == TaskDetailActivity.REQUEST_CODE) {
            Log.d("CommonTaskList", "Detail closed");
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

            Comparator<TaskStatus> comparator = (t1, t2) -> {
                if (t1.getIsExpress() == t2.getIsExpress()) {
                    return t1.getCreationDate().compareTo(t2.getCreationDate());
                }
                else if (t1.getIsExpress() == 1 && t2.getIsExpress() == 0) {
                    return -1;
                }
                else if (t1.getIsExpress() == 0 && t2.getIsExpress() == 1) {
                    return 1;
                }
                return 0;
            };

            Collections.sort(taskList, comparator);
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

            public ViewHolder(Context context, @NonNull View itemView) {
                super(itemView);
                this.context = context;
                imageView = itemView.findViewById(R.id.row_image);
                idTextView = itemView.findViewById(R.id.idTextView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
                quantityTextView = itemView.findViewById(R.id.quantityTextView);
                descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
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
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_task_list_row, parent, false);
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

        public boolean isThereExpressTask() {
            for(TaskStatus taskStatus: taskList) {
                if (taskStatus.getIsExpress() == 1) {
                    return true;
                }
            }
            return false;
        }

    }

    public interface OnTaskSelectListener {

        void onItemClick(TaskStatus task);

    }
}


