package com.mericoztiryaki.yasin;


import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mericoztiryaki.yasin.RestApi.ApiResponse;
import com.mericoztiryaki.yasin.RestApi.ManagerAll;
import com.mericoztiryaki.yasin.model.Task;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PendingTaskAdapter extends RecyclerView.Adapter<PendingTaskAdapter.TaskHolder> {

    private Activity act;
    private List<Task> taskList;
    private RecyclerViewClickListener mListener;


    public PendingTaskAdapter(Activity act, List<Task> taskList, RecyclerViewClickListener listener) {
        this.act = act;
        this.taskList = taskList;
        this.mListener = listener;
    }


    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(act);
        View view = layoutInflater.inflate(R.layout.item_pending_task2, null);
        //   FontHelper.applyFont(act, view, "font-Regular.otf");
        return new TaskHolder(view, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull final TaskHolder holder, final int position) {
        final Task task = taskList.get(position);

        holder.txtProductType.setText("" + task.getProductType());
        holder.txtQuantity.setText("" + task.getQuantity() + " Adet");
        holder.txtTaskDate.setText("" + task.getCreationDate());
        holder.txtDescription.setText("" + task.getDescription());
        holder.txtModelName.setText("Model Adı : " + task.getModelName());
        holder.txtModelRefName.setText("Model Referansı : " + task.getMaterialRef());

        Glide.with(act)
                .load(task.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.imgProduct);

        holder.imgTaskResult.setOnClickListener(v -> {
            ImageView imageView = (ImageView) v;
            showInputMatchResult(imageView, position);
        });

    }

    public void showInputMatchResult(final ImageView imgStatus, final int position) {
        ROS.log("showInputMatchResult :  " + taskList.get(position).getId() +
                " : position" + position);

        final Dialog dialog = new Dialog(act);
        dialog.setContentView(R.layout.dialog_input_task_result);

        Button btnIptalEt = dialog.findViewById(R.id.btnIptal);
        Button btnOnayla = dialog.findViewById(R.id.btnOnayla);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnIptalOnay = dialog.findViewById(R.id.btnIptalOnay);
        TextInputEditText txtTaskNote = dialog.findViewById(R.id.txtTaskNote);

        LinearLayout lytMain = dialog.findViewById(R.id.lytMain);
        LinearLayout lytIptalOnay = dialog.findViewById(R.id.lytIptalOnay);

        lytIptalOnay.setVisibility(View.GONE);
        btnIptalOnay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strNote = txtTaskNote.getText().toString();
                if (!strNote.isEmpty()) {
                    ROS.showProgressBar(act, "Görev İptal Ediliyor..");
                    Call<ResponseBody> callCancelTask = ManagerAll.getInstance().setTaskResult(taskList.get(position).getId(), -1, txtTaskNote.getText().toString());
                    callCancelTask.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            ROS.dismissProgressBar();
                            try {
                                String strJson = ROS.printResponse("setTaskResult : ", response);
                                ApiResponse apiResponse = new Gson().fromJson(strJson, ApiResponse.class);

                                if (!apiResponse.isError()) {
                                    taskList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, taskList.size());
                                    ROS.toast(act, "İşlem Başarılı");

                                } else {
                                    ROS.sweetFailed(act, "İşlem Başarısız");

                                }
                            } catch (Exception e) {
                                ROS.sweetFailed(act, "Bir Sorun Oluştu.");
                                ROS.loge("Error setTaskResult : ", e);
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            ROS.dismissProgressBar();
                            ROS.sweetFailed(act, "Bir Sorun Oluştu.");
                            ROS.loge("Error setTaskResult : ", t);

                        }
                    });
                    dialog.dismiss();
                } else {
                    ROS.sweetWarning(act, "Lütfen İptal Nedenini Giriniz..");

                }

            }
        });
        btnIptalEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lytMain.setVisibility(View.GONE);
                lytIptalOnay.setVisibility(View.VISIBLE);

            }//#END_Onclick


        });

        btnOnayla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ROS.showProgressBar(act, "Görev Tamamlanıyor..");
                Call<ResponseBody> callCancelTask = ManagerAll.getInstance().setTaskResult(taskList.get(position).getId(), 1, txtTaskNote.getText().toString());
                callCancelTask.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ROS.dismissProgressBar();

                        try {
                            String strJson = ROS.printResponse("setTaskResult : ", response);
                            ApiResponse apiResponse = new Gson().fromJson(strJson, ApiResponse.class);

                            if (!apiResponse.isError()) {
                                taskList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, taskList.size());
                                ROS.toast(act, "İşlem Başarılı");

                            } else {
                                ROS.sweetFailed(act, "İşlem Başarısız");

                            }
                        } catch (Exception e) {
                            ROS.sweetFailed(act, "Bir Sorun Oluştu.");
                            ROS.loge("Error setTaskResult : ", e);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        ROS.dismissProgressBar();

                        ROS.sweetFailed(act, "Bir Sorun Oluştu.");
                        ROS.loge("Error setTaskResult : ", t);

                    }
                });
                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ROS.toast(act, "CANCEL");
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public interface RecyclerViewClickListener {

        void onClick(Object o, int position);
    }

    class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener mListener;
        private ImageView imgProduct;
        private ImageView imgTaskResult;

        private TextView txtProductType;
        private TextView txtQuantity;
        private TextView txtTaskDate;
        private TextView txtDescription;
        private TextView txtModelName;
        private TextView txtModelRefName;

        public TaskHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;

            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgTaskResult = itemView.findViewById(R.id.imgTaskResult);

            txtProductType = itemView.findViewById(R.id.txtProductType);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtTaskDate = itemView.findViewById(R.id.txtTaskDate);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtModelName = itemView.findViewById(R.id.txtModelName);
            txtModelRefName = itemView.findViewById(R.id.txtModelRefName);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListener.onClick(taskList.get(position), position);
        }


    }
}
