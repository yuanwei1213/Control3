package com.gengy.control.Control.MineModular;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.allen.library.SuperButton;
import com.gengy.control.Base.BaseActivity;
import com.gengy.control.Entity.ResourceEntity;
import com.gengy.control.ImageActivity;
import com.gengy.control.R;
import com.gengy.control.Untils.LoadFileUntils;
import com.gengy.control.Untils.ShareKey;
import com.gengy.control.Untils.SharedPreferencesHelper;
import com.gengy.control.Untils.ToastUtils;
import com.gengy.control.http.ApiService;
import com.github.library.BaseRecyclerAdapter;
import com.github.library.BaseViewHolder;
import com.github.library.listener.RequestLoadMoreListener;
import com.github.library.view.LoadType;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.bluemobi.dylan.http.Http;
import cn.bluemobi.dylan.http.HttpCallBack;
import cn.bluemobi.dylan.http.JsonParse;

public class CloudActivity extends BaseActivity {
    @BindView(R.id.recy_view)
    RecyclerView recyView;
    private RxPermissions rxPermissions;
    int page=1;
    List<ResourceEntity> mData=new ArrayList<>();
    private BaseRecyclerAdapter adapter;
    private String id;

    @Override
    public int intiLayout() {
        return R.layout.activity_cloud;
    }

    @Override
    public void initView() {
        action_title.setText("云端管理");

    }

    @Override
    public void initData() {
        rxPermissions = new RxPermissions(this);
        initSetAdapter(mData);


         id = SharedPreferencesHelper.getInstance(mActivity).getString(ShareKey.ID, "");
          initGetList();
           adapter.openLoadingMore(true);
          adapter.setOnLoadMoreListener(new RequestLoadMoreListener() {
              @Override
              public void onLoadMoreRequested() {
                  page++;
                  initGetList();
              }
          });



    }

    private void initGetList() {

             Http.with(mActivity).setObservable(Http.getApiService(ApiService.class).getResource(id,page)).setDataListener(new HttpCallBack() {
            @Override
            public void netOnSuccess(Map<String, Object> data) {
                List<ResourceEntity> list = JsonParse.getList(data, "data", ResourceEntity.class);
                if(list.size()<10){
                    //显示没有更多数据
                    adapter.notifyDataChangeAfterLoadMore(list,false);
                    adapter.addNoMoreView();
                    adapter.openLoadingMore(false);
                    return;
                }
                adapter.notifyDataChangeAfterLoadMore(list, true);
            }
        });

    }

    private void initSetAdapter(List<ResourceEntity> list) {

         adapter = new BaseRecyclerAdapter(mActivity, list,
                R.layout.item_cloud) {
            @Override
            protected void convert(BaseViewHolder helper, Object item) {
//                TextView tv_time
                ResourceEntity entity = (ResourceEntity) item;
                SuperButton btPlay = helper.getView(R.id.bt_play);
                SuperButton btLoad = helper.getView(R.id.bt_load);
                btPlay.setVisibility(View.GONE);
                String type = null;
                type = "其他";
                btPlay.setVisibility(View.VISIBLE);
                switch (entity.type) {
                    case 0:
                        type = entity.resource + "\n";
                        btPlay.setVisibility(View.GONE);
                        break;
                    case 1:
                        type = "拍照";
                        break;
                    case 2:
                        type = "录音";
                        break;
                    case 3:
                        type = "录屏";
                        break;
                    case 4:
                        type = "截屏";
                        break;
                }
//
                btPlay.setTag(helper.getAdapterPosition());
                helper.setText(R.id.tv_time, type + "      " + entity.created_at);
                btLoad.setTag(helper.getAdapterPosition());
//                String finalType = type;
                btLoad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int tag = (int) view.getTag();
                        ResourceEntity entity1 = list.get(tag);
                        if(entity1.type==0){
                            return;
                        }

                        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ).subscribe(granted -> {
                                    if(granted){
//                                        String url="http://www.jiayongshoujiguanli.com/uploads/3938/9db0Eglnnk.mp4";
//                                        LoadFileUntils.donwloadImg(CloudActivity.this, url,3);//iPath
                                        LoadFileUntils.donwloadImg(CloudActivity.this, entity1.resource,entity1.type);//iPath
                                    }else {
                                        ToastUtils.getInstanc().showToast("请打开权限进行下载");
                                    }

                        });


                    }
                });

                btPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int tag = (int) view.getTag();
                        ResourceEntity entity1 = list.get(tag);

                        switch (entity1.type) {
                            case 0:

                                break;
                            case 1:
                            case 4:
                                bundle.putString("imgurl", entity1.resource);
                                actionStart(ImageActivity.class, bundle);
//                                type="图片";
                                break;
                            case 2:
                            case 3:
//                                type="音频";
                                String url = entity1.resource;//示例，实际填你的网络视频链接
                                String extension = MimeTypeMap.getFileExtensionFromUrl(url);
                                String mimeType =
                                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                                Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
                                mediaIntent.setDataAndType(Uri.parse(url), mimeType);
                                startActivity(mediaIntent);

//                                type="视频";
                                break;
                        }

//                        Log.i(TAG , "onClick: "+tag);
//                        Intent intent = new Intent();
//
//                        intent.setAction("android.intent.action.VIEW");
//                        Uri content_url = Uri.parse(tag);
////                        Uri content_url = Uri.parse("file://"+tag);
//                        intent.setData(content_url);
//
//                        startActivity(intent);


                    }
                });


            }
        };
        recyView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyView.setAdapter(adapter);
    }


}
