package com.gengy.control.BeingControl.BeingControl;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gengy.control.Base.BaseActivity;
import com.gengy.control.Entity.PhoneEntity;
import com.gengy.control.Entity.PhoneNetWorkEntity;
import com.gengy.control.Entity.SelectEntity;
import com.gengy.control.R;
import com.gengy.control.Untils.DialogUntils;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.ToastUtils;
import com.gengy.control.http.ApiService;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;

public class WhiteListActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener
        , View.OnClickListener {
    @BindView(R.id.img_right)
    ImageButton imgRight;
    @BindView(R.id.rb1)
    RadioButton rb1;
    @BindView(R.id.rb2)
    RadioButton rb2;
    @BindView(R.id.rg)
    RadioGroup rg;

    @BindView(R.id.recy_view)
    RecyclerView recyView;
    private PopupWindow popupWindow;
    private int mAdressCode = 1;
    List<PhoneNetWorkEntity> mListData;
    private BaseRecyclerAdapter adapter;
    private Dialog mDialog;
    private EditText etName, etPhone;
    private int mContacts = 0;
    private String id;


    @Override
    public int intiLayout() {
        return R.layout.activity_white_list;
    }

    @Override
    public void initView() {
        action_title.setText("通话录音白名单");
        rg.setOnCheckedChangeListener(this);
        imgRight.setImageResource(R.mipmap.white_right);
        imgRight.setOnClickListener(this);
        imgRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopWindow();

            }
        });


    }

    private void initPopWindow() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentview = inflater.inflate(R.layout.popwindow_white_list, null);
        //自己的弹框布局
        popupWindow = DialogUntils.getInstance().createPopWindow(contentview);
        popupWindow.showAsDropDown(imgRight);
        contentview.findViewById(R.id.tv_adress).setOnClickListener(this);
        contentview.findViewById(R.id.tv_manual_add).setOnClickListener(this);
        contentview.findViewById(R.id.tv_delete).setOnClickListener(this);
        contentview.findViewById(R.id.tv_delete_all).setOnClickListener(this);
    }


    @Override
    public void initData() {
        id = SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID, "");
        initGetContacts(mContacts);
//        mListData= new PhoneUtil(this).getPhone();
//
//        adapter = new BaseRecyclerAdapter(mActivity, mListData,
//                R.layout.item_adress) {
//            @Override
//            protected void convert(BaseViewHolder helper, Object item) {
//                PhoneEntity entity = (PhoneEntity) item;
//                helper.setText(R.id.tv_con, entity.name + "\n" + "\n" + entity.telPhone);
//                ImageView imageView = helper.getView(R.id.img_select);
//                imageView.setImageResource(R.mipmap.adress_normal);
//                imageView.setTag(helper.getAdapterPosition());
//                if (entity.check) {
//                    imageView.setImageResource(R.mipmap.adress_select);
//                }
//
//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        int post= (int) view.getTag();
//                        PhoneEntity entity=mListData.get(post);
//                        entity.check=!entity.check;
//                        mListData.set(post,entity);
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//
//
//            }
//        };
//
//        recyView.setLayoutManager(new LinearLayoutManager(mActivity));
//        recyView.setAdapter(adapter);

    }

    private void initGetContacts(int mContacts) {

        if (TextUtils.isEmpty(id)) {
            return;
        }

        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).getContacts(id,
                mContacts + ""))
                .setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnSuccess(Map<String, Object> datas) {
                        mListData = JsonParse.getList(datas, "data", PhoneNetWorkEntity.class);
                        for (int i = 0; i < mListData.size(); i++) {
                            PhoneNetWorkEntity entity = mListData.get(i);
                            entity.check = false;
                            mListData.set(i, entity);

                        }
                        initSetAdapter();

                    }
                });
    }

    private void initSetAdapter() {
        adapter = new BaseRecyclerAdapter(mActivity, mListData,
                R.layout.item_adress) {
            @Override
            protected void convert(BaseViewHolder helper, Object item) {
                PhoneNetWorkEntity entity = (PhoneNetWorkEntity) item;
                helper.setText(R.id.tv_con, entity.name + "\n" + "\n" + entity.phone);
                ImageView imageView = helper.getView(R.id.img_select);
                imageView.setImageResource(R.mipmap.adress_normal);
                imageView.setTag(helper.getAdapterPosition());
                if (entity.check) {
                    imageView.setImageResource(R.mipmap.adress_select);
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int post = (int) view.getTag();
                        PhoneNetWorkEntity entity=mListData.get(post);
                        entity.check=!entity.check;
                        mListData.set(post,entity);
                        adapter.notifyDataSetChanged();
                    }
                });


            }
        };

        recyView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyView.setAdapter(adapter);

    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb1:
                mContacts = 0;
                initGetContacts(mContacts);
                break;
            case R.id.rb2:
                mContacts = 1;
                initGetContacts(mContacts);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mAdressCode) {
            initGetContacts(mContacts);


        }
    }

    @Override
    public void onClick(View view) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        }

        switch (view.getId()) {
            case R.id.tv_adress:
                bundle.putInt("type",mContacts);
                actionStartForResult(AdressBookActivity.class, bundle,mAdressCode);

                break;
            case R.id.tv_manual_add:
                mDialog = DialogUntils.getInstance().createCenterDialog(mActivity,
                        R.layout.dialog_add_white);
                mDialog.show();
                etName = mDialog.findViewById(R.id.et_name);
                etPhone = mDialog.findViewById(R.id.et_number);
                mDialog.findViewById(R.id.tv_cancel).setOnClickListener(this);
                mDialog.findViewById(R.id.tv_sure).setOnClickListener(this);


                break;
            case R.id.tv_delete:
                ArrayList<SelectEntity> selectData = new ArrayList<>();
                for (int i = 0; i < mListData.size(); i++) {
                    PhoneNetWorkEntity entity = mListData.get(i);
                    SelectEntity entity1=new SelectEntity();
                    if (entity.check) {
                        entity.check = false;
                        entity1.aid=entity.id+"";
                        selectData.add(entity1);
                    }

                }
                initSelectDelete(selectData);




                break;
            case R.id.tv_delete_all:
//                清空列表
                initDeleteAll();


                break;
            case R.id.tv_cancel:
                mDialog.dismiss();


                break;
            case R.id.tv_sure:
                mDialog.dismiss();
                String id = SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID,
                        "");
                if (TextUtils.isEmpty(id)) {
                    ToastUtils.getInstanc().showToast("请先登录");
                    return;
                }
                String name = etName.getText().toString();
                String phone = etPhone.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
                    ToastUtils.getInstanc().showToast("请填写完整信息");
                    return;
                }
                Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).addContacts(id, name, phone, mContacts + ""))
                        .setDataListener(new HttpCallBack() {
                            @Override
                            public void netOnSuccess(Map<String, Object> data) {
                                ToastUtils.getInstanc().showToast("添加成功");
                                initGetContacts(mContacts);

                            }
                        });


                break;


        }
    }

    private void initSelectDelete(ArrayList<SelectEntity> selectData) {
        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).deleteSelect(new Gson().toJson(selectData)))
                .setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnSuccess(Map<String, Object> data) {
                        initGetContacts(mContacts);
                    }
                });
    }

    private void initDeleteAll() {
        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).deletaAll(id,
                mContacts + ""))
                .setDataListener(new HttpCallBack() {
                    @Override
                    public void netOnSuccess(Map<String, Object> data) {
                        recyView.removeAllViews();
                        mListData .clear();
                        adapter.notifyDataSetChanged();

                    }
                });

    }


}
