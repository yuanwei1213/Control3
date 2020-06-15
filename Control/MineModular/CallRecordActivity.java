package com.gengy.control.Control.MineModular;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;
import retrofit2.http.HTTP;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.gengy.control.Base.BaseActivity;
import com.gengy.control.Entity.SoundRecordEntity;
import com.gengy.control.R;
import com.gengy.control.Untils.LoadFileUntils;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.ToastUtils;
import com.gengy.control.http.ApiService;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallRecordActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private List<SoundRecordEntity> mData;
    private RxPermissions rxPermissions;

    @Override
    public int intiLayout() {
        return R.layout.activity_call_record;
    }

    @Override
    public void initView() {
        action_title.setText("通话记录");
         recyclerView=findViewById(R.id.recy_view);

    }

    @Override
    public void initData() {
        rxPermissions = new RxPermissions(this);
        String id= SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID,"");
        Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).getSoundRecord(id)).setDataListener(new HttpCallBack() {
            @Override
            public void netOnSuccess(Map<String, Object> data) {
                mData= JsonParse.getList(data,"data",SoundRecordEntity.class);

                initAdapter();

            }
        });

    }

    private void initAdapter() {


        BaseRecyclerAdapter adapter=new BaseRecyclerAdapter(mActivity,mData,R.layout.item_call) {
            @Override
            protected void convert(BaseViewHolder helper, Object item) {
                SoundRecordEntity entity= (SoundRecordEntity) item;
//                tv_time
                helper.setText(R.id.tv_time,entity.created_at);
                  helper.setText(R.id.tv_name,entity.hint);
                TextView tvPlay=helper.getView(R.id.tv_play);
                tvPlay.setTag(entity.record);

                TextView tvLoad=helper.getView(R.id.tv_load);
                tvLoad.setTag(entity.record);
                tvLoad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initLoadUrl((String) view.getTag());
                    }
                });


                tvPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initPlay((String) view.getTag());
                    }
                });


            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setAdapter(adapter);

    }

    private void initLoadUrl(String url) {
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe(granted -> {
            if(granted){
//                                        String url="http://www.jiayongshoujiguanli.com/uploads/3938/9db0Eglnnk.mp4";
//                                        LoadFileUntils.donwloadImg(CloudActivity.this, url,3);//iPath
                LoadFileUntils.donwloadImg(mActivity, url,2);//iPath
            }else {
                ToastUtils.getInstanc().showToast("请打开权限进行下载");
            }

        });


    }

    private void initPlay(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType =
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
        mediaIntent.setDataAndType(Uri.parse(url), mimeType);
        startActivity(mediaIntent);
    }
}
