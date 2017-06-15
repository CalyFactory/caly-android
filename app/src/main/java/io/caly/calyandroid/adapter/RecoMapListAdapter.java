package io.caly.calyandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.activity.WebViewActivity;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.LogType;
import io.caly.calyandroid.model.dataModel.RecoModel;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.StringFormmater;
import io.caly.calyandroid.util.Util;
import io.caly.calyandroid.util.tracker.AnalysisTracker;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.sql.Types.NULL;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 4. 25
 */

public class RecoMapListAdapter extends PagerAdapter{

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + RecoMapListAdapter.class.getSimpleName();

    LayoutInflater inflater;

    Context context;
    public ArrayList<RecoModel> recoList;

    public class ViewHolder{

        View view;

        @Bind(R.id.tv_reco_title)
        TextView tvRecoTitle;

        @Bind(R.id.imv_reco_food)
        ImageView imvFood;

        @Bind(R.id.tv_reco_distance)
        TextView tvRecoDistance;

        @Bind(R.id.tv_reco_hashtag)
        TextView tvRecoHashtag;

        @Bind(R.id.imv_reco_share)
        ImageView imvShare;

        @Bind(R.id.tv_reco_price)
        TextView tvRecoPrice;

        @Bind(R.id.tv_reco_insta)
        TextView tvInsta;


        public ViewHolder(View view){
            ButterKnife.bind(this, view);

            this.view = view;
        }

    }

    public RecoMapListAdapter(Context context, LayoutInflater inflater){
        this.inflater = inflater;
        this.context = context;
        this.recoList = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;

        view = inflater.inflate(R.layout.item_reco_mapitem, null);

        bindView(new ViewHolder(view), position);
        container.addView(view);
        return view;
    }

    void bindView(ViewHolder holder, final int position){

        RecoModel recoModel = recoList.get(position);
        holder.tvRecoTitle.setText(recoModel.title);
        holder.tvRecoDistance.setText(recoModel.distance);
        holder.tvRecoHashtag.setText(StringFormmater.hashTagFormat(recoModel.tagNames));
        holder.tvInsta.setText(recoModel.sourceUserId);
        holder.tvRecoPrice.setText(
                StringFormmater.priceFormat(
                        recoModel.price
                )
        );
        Picasso.with(context)
                .load(context.getString(R.string.app_server) + "img/" + recoModel.imgUrl)
                .error(R.drawable.img_not_found)
                .placeholder(R.drawable.img_not_found)
                .into(holder.imvFood);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RecoModel recoModel = recoList.get(position);

                requestSetRecoLog (
                        TokenRecord.getTokenRecord().getApiKey(),
                        recoModel.eventHashKey,
                        LogType.CATEGORY_RECO_MAP_CELL.value,
                        LogType.LABEL_RECO_MAP_DEEPLINK.value,
                        LogType.ACTION_CLICK.value,
                        NULL,
                        recoModel.recoHashKey
                );



//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(recoModel.deepUrl));
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", recoModel.deepUrl);
                intent.putExtra("recoHashKey", recoModel.recoHashKey);
                intent.putExtra("eventHashKey", recoModel.eventHashKey);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);

                Tracker t = ((CalyApplication) ((Activity) context).getApplication()).getDefaultTracker();
                t.setScreenName(this.getClass().getName());
                t.send(
                        new HitBuilders.EventBuilder()
                                .setCategory(context.getString(R.string.ga_action_reco_view))
                                .setAction("recoItemMapClick")
                                .set("&userId", TokenRecord.getTokenRecord().getUserId())
                                .set("&loginPlatform", TokenRecord.getTokenRecord().getLoginPlatform())
                                .set("&eventHashKey", recoModel.eventHashKey)
                                .set("&recoHashKey", recoModel.recoHashKey)
                                .build()
                );
            }
        });


        holder.imvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RecoModel recoModel = recoList.get(position);
                requestSetRecoLog (
                        TokenRecord.getTokenRecord().getApiKey(),
                        recoModel.eventHashKey,
                        LogType.CATEGORY_RECO_MAP_CELL.value,
                        LogType.LABEL_RECO_MAP_SHARE_KAKAO_INCELL.value,
                        LogType.ACTION_CLICK.value,
                        NULL,
                        recoModel.recoHashKey
                );

                String[] snsList = {
                        "com.kakao.talk", //kakaotalk
                };
                boolean sended = false;
                for(String snsPackage : snsList){
                    if(Util.isPackageInstalled(snsPackage)){

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,"[캘리] 여기어때요? \n" + recoList.get(position).deepUrl);
                        intent.setPackage("com.kakao.talk");

                        context.startActivity(intent);
                        sended = true;
                    }
                }
                if(!sended){
                    Toast.makeText(context, "공유 할 수 있는 SNS가 설치 되어있지 않습니다.",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void addItem(RecoModel object){
        this.recoList.add(object);
        notifyDataSetChanged();
    }

    public void addItems(List<RecoModel> objects){
        this.recoList.addAll(objects);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return recoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View)object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    void requestSetRecoLog (String apikey, String eventHashkey, int category, int label, int action, long residenseTime, String recoHashkey){
        ApiClient.getService().setRecoLog(
                AnalysisTracker.getAppSession().getSessionKey().toString(),
                apikey,
                eventHashkey,
                category,
                label,
                action,
                residenseTime,
                recoHashkey
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());
                Logger.d(TAG, "param" + Util.requestBodyToString(call.request().body()));

                switch (response.code()){
                    case 200:
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        break;
                }

            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }

                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

            }
        });
    }
}
