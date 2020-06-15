package com.gengy.control.BeingControl.BeingControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.gengy.control.Base.BaseActivity;
import com.gengy.control.Entity.PhoneEntity;
import com.gengy.control.R;
import com.gengy.control.Untils.PhoneUtil;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.http.ApiService;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AdressBookActivity extends BaseActivity {
    @BindView(R.id.recy_adress)
    RecyclerView recyAdress;
    private List<PhoneEntity> phoneData;
    private BaseRecyclerAdapter adapter;


    @Override
    public int intiLayout() {
        return R.layout.activity_adress_book;
    }

    @Override
    public void initView() {
        action_title.setText("通讯录");
        img_right.setImageDrawable(getResources().getDrawable(R.mipmap.adress_right));
        img_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type=extras.getInt("type",0);
                String id= SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID,"");


                ArrayList<PhoneEntity> resultData = new ArrayList<>();
                for (int i = 0; i < phoneData.size(); i++) {
                    PhoneEntity entity = phoneData.get(i);
                    if (entity.check) {
                        entity.check = false;
                        entity.type=type;
                        entity.id=id;
                        resultData.add(entity);
                    }
                }

                RequestBody cids =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), new Gson().toJson(resultData));
                Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).addContacts(cids))
                        .setDataListener(new HttpCallBack() {
                            @Override
                            public void netOnSuccess(Map<String, Object> data) {

                            }
                        });






//                Intent intent = new Intent();
//                intent.putExtra("data", resultData);
//                setResult(Activity.RESULT_OK, intent);

//                Bundle bundle = new Bundle();
//                bundle.putSerializable("data",  resultData);
//                intent.putExtras(bundle);
//                setResult(Activity.RESULT_OK,intent);
//                finish();


            }
        });
    }

    @Override
    public void initData() {
        PhoneUtil phoneUtil = new PhoneUtil(this);
        phoneData = phoneUtil.getPhone();
        Log.i(TAG, "onClick: "+phoneData.toString());




        adapter = new BaseRecyclerAdapter(mActivity, phoneData,
                R.layout.item_adress) {
            @Override
            protected void convert(BaseViewHolder helper, Object item) {
                PhoneEntity entity = (PhoneEntity) item;
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

                        PhoneEntity entity = phoneData.get(post);
                        entity.check = !entity.check;
                        phoneData.set(post, entity);
                        adapter.notifyDataSetChanged();
                    }
                });


            }
        };

        recyAdress.setLayoutManager(new LinearLayoutManager(mActivity));
        recyAdress.setAdapter(adapter);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
