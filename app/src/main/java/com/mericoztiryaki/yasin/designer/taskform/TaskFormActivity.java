package com.mericoztiryaki.yasin.designer.taskform;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.mericoztiryaki.yasin.R;
import com.mericoztiryaki.yasin.ROS;
import com.mericoztiryaki.yasin.RestApi.ApiResponse;
import com.mericoztiryaki.yasin.RestApi.ManagerAll;
import com.mericoztiryaki.yasin.core.arch.BaseActivity;
import com.mericoztiryaki.yasin.core.task.AppTaskExecutor;
import com.mericoztiryaki.yasin.model.LocalUser;
import com.mericoztiryaki.yasin.model.ProductType;
import com.mericoztiryaki.yasin.model.Task;
import com.mericoztiryaki.yasin.model.response.TaskResponse;
import com.mericoztiryaki.yasin.util.ImageActivity;
import com.mericoztiryaki.yasin.util.ImageFile;
import com.mericoztiryaki.yasin.util.persistance.LocalUserHandler;
import com.mericoztiryaki.yasin.util.NotNullValidator;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskFormActivity extends BaseActivity<TaskFormPresenter> implements TaskFormView, View.OnClickListener {

    public static final int REQUEST_CODE = 1;
    public static final String ARG_TASK_EXTRA = "task_extra";
    private static final int ACTIVITY_CHOOSE_FILE = 100;

    private TaskFormType type = TaskFormType.CREATE;
    private Task taskToUpdate;

    private LocalUser localUser;

    private List<MaterialEditText> editTexts;
    private MaterialSpinner productTypeSpinner;
    private ArrayAdapter<String> productTypeAdapter;
    private List<ProductType> productTypes;

    private Switch outsourceSwitch;
    private Switch expressSwitch;

    private ImageView imageView;
    private Button imageSelectionButton;
    private ImageFile selectedImage;

    private Button createButton;

    // private LoadingDialog loadingDialog;
    private Button btnFileUpload;
    Activity act;
    ArrayList<MediaFile> files = new ArrayList<>();
    Spinner spnModelist;

    @NonNull
    @Override
    protected TaskFormPresenter createPresenter(@NonNull Context context) {
        return new TaskFormPresenter(this, new AppTaskExecutor(this));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("x3k", "11");


        try {
            setContentView(R.layout.fragment_task_form);

            getSupportActionBar().setTitle(R.string.designer_task_form);
            localUser = new LocalUserHandler(this).getLocalUser();
            act = TaskFormActivity.this;

            initializeEditTexts();
            initializeProductSpinner();
            initializeImageSelectionViews();

            outsourceSwitch = findViewById(R.id.outsourceSwitch);
            expressSwitch = findViewById(R.id.expressSwitch);
            createButton = findViewById(R.id.createButton);
            btnFileUpload = findViewById(R.id.btnFileUpload);
            spnModelist = findViewById(R.id.spnModelist);

            btnFileUpload.setOnClickListener(this);
            createButton.setOnClickListener(this);

            //loadingDialog = new LoadingDialog(this, getString(R.string.task_form_loading));

            super.onCreate(savedInstanceState);

            ROS.loadUsers(act, spnModelist);

            Serializable taskToUpdate = getIntent().getSerializableExtra(ARG_TASK_EXTRA);
            Log.d("x3k", "33");

            if (taskToUpdate != null) {
                Log.d("x3k", "55");

                type = TaskFormType.UPDATE;
                this.taskToUpdate = (Task) taskToUpdate;
                displayPreUpdatedValues();
                Log.d("x3k", "444");

            }
            Log.d("x3k", "22");

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        } catch (Exception ex) {
            Log.d("x3k", ex.getMessage() + " : " + ex.getCause());
            Log.e("x3k", ex.toString() + " : ");
            ex.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFileUpload:
                onBrowse();
                break;

            default:
                if (validateForm()) {
                    ROS.log("----------------");
                    Log.d("x3k", "spinner Index : " + (productTypeSpinner.getSelectedItemPosition() - 1));

                    int typeIndex = (productTypeSpinner.getSelectedItemPosition() - 1);
                    if (typeIndex >= productTypes.size()) {
                        typeIndex = 0;
                    }
                    ROS.log("typeIndex : " + typeIndex + " , Size : " + productTypes.size());
                    ProductType productType = productTypes.get(typeIndex);

                    productTypes.get(productTypeSpinner.getSelectedItemPosition() - 1);
                    Task task = new Task(editTexts.get(0).getText().toString(),
                            editTexts.get(1).getText().toString(),
                            editTexts.get(2).getText().toString(),
                            editTexts.get(3).getText().toString(),
                            Integer.parseInt(editTexts.get(4).getText().toString()),
                            editTexts.get(5).getText().toString(),
                            productType,
                            outsourceSwitch.isChecked(),
                            expressSwitch.isChecked(),
                            localUser.getId());

                    if (type == TaskFormType.CREATE) {
                        presenter.createForm(task, selectedImage);
                        if (files.size() > 0) {
                            uploadFiles(act, files);
                        }
                    } else if (type == TaskFormType.UPDATE) {
                        presenter.updateForm(taskToUpdate.getId(), task, selectedImage);
                    }

                }//#END_if_validate
        }

    }

    void uploadFiles(Activity act, List<MediaFile> paths) {

        List<MultipartBody.Part> list = new ArrayList<>();

        for (MediaFile uri : paths) {
            ROS.log("uri : " + uri.toString() + " : " + uri.getPath());
            MultipartBody.Part imageRequest = prepareFilePart("file[]", uri);

            list.add(imageRequest);
        }


        Call<ResponseBody> callUploadImages = ManagerAll.getInstance().uploadImages(prepareJsonPart(), list);

        callUploadImages.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ROS.dismissProgressBar();
                try {
                    String jsonData = ROS.printResponse("uploadFiles task : ", response);
                    ApiResponse apiResponse = new Gson().fromJson(jsonData, ApiResponse.class);

                    try {
                        if (act != null) {
                            /*
                            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(act, SweetAlertDialog.SUCCESS_TYPE);
                            sweetAlertDialog
                                    .setTitleText("Başarılı")
                                    .setContentText("Görev Tamamlandı.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .show();*/
                            ROS.toast(act, "Dosya Yüklenmesi Başarılı");
                        }

                    } catch (Exception ex) {
                        ROS.loge("Sweet alert error ", ex);
                    }


                } catch (Exception ex) {
                    ROS.loge("callAddProduct Error : ", ex);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                ROS.dismissProgressBar();
                ROS.sweetFailed(act, "Görev Tamamlanamadı.");

                ROS.loge("main" + "on error is called and the error is  ----> ", throwable);
            }
        });


    }//#END_Upload_Images

    @NonNull
    private RequestBody prepareJsonPart() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("taskID", getTaskId());
        jsonObject.addProperty("task_uststatus", "48");

        ROS.log("Prepare Json : " + jsonObject.toString());
        RequestBody requestBody = null;


        requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        return requestBody;
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, MediaFile mediaFile) {
        File file = new File(mediaFile.getPath());
        //##Image_Compress

        //##END_Image_Compress

        RequestBody requestFile = null;
        ROS.log("prepareFilePart fileURI : " + mediaFile.getPath());

        requestFile = RequestBody.create(MediaType.parse(mediaFile.getMimeType()), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public void onBrowse() {
        Intent intent = new Intent(act, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowFiles(true)
                .setShowImages(false)
                .setShowVideos(false)
                .setMaxSelection(10)
                .build());
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("x3k", "aa" + resultCode + " : " + requestCode);
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image img = ImagePicker.getFirstImageOrNull(data);
            if (img != null) {
                try {
                    selectedImage = new ImageFile(this, img.getPath());
                    Glide.with(this)
                            .load(selectedImage.getImagePath())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imageView);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String path = "";
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);

            ROS.log("File :  " + files.size() + " : " + files.get(0).getPath() + " : " + files.get(0).getMimeType());
        }
        ROS.log("requestCode : " + resultCode + " : " + requestCode);

    }


    private void initializeEditTexts() {
        Log.d("x3k", "initializeEditTexts");

        editTexts = new ArrayList<>(6);
        editTexts.add((MaterialEditText) findViewById(R.id.modelRefEditText));
        editTexts.add((MaterialEditText) findViewById(R.id.modelNameEditText));
        editTexts.add((MaterialEditText) findViewById(R.id.materialRefEditText));
        editTexts.add((MaterialEditText) findViewById(R.id.materialNameEditText));
        editTexts.add((MaterialEditText) findViewById(R.id.quantityEditText));
        editTexts.add((MaterialEditText) findViewById(R.id.descriptionEditText));

        int count = 0;
        for (MaterialEditText editText : editTexts) {
            editText.addValidator(new NotNullValidator());
            editText.setText(String.valueOf(++count));
        }
        Log.d("x3k", "initializeEditTexts22");

    }

    private void initializeProductSpinner() {
        ROS.log("initializeProductSpinner1");
        productTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>());
        productTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productTypeSpinner = findViewById(R.id.productTypeSpinner);
        productTypeSpinner.setAdapter(productTypeAdapter);
        ROS.log("initializeProductSpinner2");

    }

    private void initializeImageSelectionViews() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.imageButton) {
                    ImagePicker.create(TaskFormActivity.this)
                            .returnMode(ReturnMode.ALL)
                            .toolbarImageTitle(getResources().getString(R.string.image_selection_title))
                            .includeVideo(false)
                            .single()
                            .showCamera(true)
                            .enableLog(true)
                            .start();
                } else if (view.getId() == R.id.row_image && selectedImage != null) {
                    startActivity(ImageActivity.newInstance(TaskFormActivity.this, selectedImage.getImagePath()));
                }
            }
        };

        imageView = findViewById(R.id.row_image);
        imageView.setOnClickListener(listener);
        imageSelectionButton = findViewById(R.id.imageButton);
        imageSelectionButton.setOnClickListener(listener);
    }

    private void displayPreUpdatedValues() {
        Log.d("x3k", "1");
        getSupportActionBar().setTitle("Numune Güncelle #" + taskToUpdate.getId());

        editTexts.get(0).setText(taskToUpdate.getModelRef());
        Log.d("x3k", "2");

        editTexts.get(1).setText(taskToUpdate.getModelName());
        editTexts.get(2).setText(taskToUpdate.getMaterialRef());
        editTexts.get(3).setText(taskToUpdate.getMaterialName());
        editTexts.get(4).setText(String.valueOf(taskToUpdate.getQuantity()));
        editTexts.get(5).setText(taskToUpdate.getDescription());

        outsourceSwitch.setChecked(taskToUpdate.getIsOutSourced() == 1);
        expressSwitch.setChecked(taskToUpdate.getIsOutSourced() == 1);

        Glide.with(this)
                .load(taskToUpdate.getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);

        createButton.setText(R.string.task_update);
    }


    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }


    private boolean validateForm() {
        Log.d("x3k", "validateForm");

        boolean isValidToCreate = true;
        for (MaterialEditText editText : editTexts) {
            isValidToCreate &= editText.validate();
        }

        if (productTypeSpinner.getSelectedItemPosition() == 0) {
            productTypeSpinner.setEnableErrorLabel(true);
            productTypeSpinner.setError("Bu alan doldurulmak zorundadır.");
            isValidToCreate = false;
        }
        Log.d("x3k", "validateForm : " + isValidToCreate);

        return isValidToCreate;
    }

    @Override
    public void showLoadingDialog() {
        Log.d("x3k", "showLoadingDialog1");
        //loadingDialog.show();
        ROS.showProgressBar(act, "Yükleniyor..");
    }

    @Override
    public void closeLoadingDialog() {
        ROS.dismissProgressBar();
        //loadingDialog.dismiss();
    }

    @Override
    public void setProductList(List<ProductType> productList) {
        Log.d("x3k", "setProductList");

        List<String> names = new ArrayList<>(productList.size());
        Log.d("x3k", "productListsize : " + productList.size());

        for (ProductType type : productList) {
            names.add(type.getName());
        }

        productTypeAdapter.addAll(names);
        productTypes = productList;
        ROS.log("productTypes.size() : " + productTypes.size());
        ROS.log("productTypeSpinner.getCount() : " + productTypeSpinner.getAdapter().getCount());
        productTypeSpinner.setSelection(0);
        Log.d("x3k", "setProductList11");
        Log.d("x3k", type.toString() + " : " + TaskFormType.UPDATE.toString());

        if (type == TaskFormType.UPDATE) {
            Log.d("x3k", "setProductLis22");

            int typeIndex = 0;
            for (int i = 0; i < productTypes.size(); i++) {
                Log.d("x3k", "4");

                if (productTypes.get(i).getCode().equals(taskToUpdate.getProductType())) {
                    typeIndex = i;
                    break;
                }
            }
            Log.d("x3k", "productTypeSpinner : " + typeIndex);

            productTypeSpinner.setSelection(typeIndex + 1);
            Log.d("x3k", "productTypeSpinner22 : " + typeIndex);

        }//#END  if (type == TaskFormType.
    }

    @Override
    public void creationSuccessful(TaskResponse result) {
        Log.d("x3k", "creationSuccessful");

        String title = getString(R.string.task_succ_title);
        String message = type == TaskFormType.CREATE ? String.format(getString(R.string.task_succ_message), result.getTaskId(), result.getResponsibleUsername()) : getString(R.string.task_succ_update_msg);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        // TODO: 15/07/2019 Burasi Yorum satiri yapildi 
                        finish();
                    }
                })
                .show();
        Log.d("x3k", "creationSuccessful22");

    }

    @Override
    public void creationFailed() {
        Log.d("x3k", "creationFailed");

        String title = getString(R.string.task_fail_title);
        String message = getString(type == TaskFormType.CREATE ? R.string.task_fail_message : R.string.task_fail_update_msg);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, null)
                .show();
        Log.d("x3k", "creationFailed11");

    }

    enum TaskFormType {
        CREATE, UPDATE;
    }
}
