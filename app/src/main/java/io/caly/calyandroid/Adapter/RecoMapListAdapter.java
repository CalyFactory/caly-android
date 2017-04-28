package io.caly.calyandroid.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.WebViewActivity;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.DataModel.RecoModel;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.StringFormmater;

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

        Picasso.with(context)
                .load(context.getString(R.string.app_server) + "img/" + recoModel.imgUrl)
                .error(R.drawable.img_not_found)
                .placeholder(R.drawable.img_not_found)
                .into(holder.imvFood);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RecoModel recoModel = recoList.get(position);

//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(recoModel.deepUrl));
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", recoModel.deepUrl);
                intent.putExtra("recoHashKey", recoModel.recoHashKey);
                intent.putExtra("eventHashKey", recoModel.eventHashKey);
                context.startActivity(intent);

                Tracker t = ((CalyApplication) ((Activity) context).getApplication()).getDefaultTracker();
                t.setScreenName(this.getClass().getName());
                t.send(
                        new HitBuilders.SocialBuilder()
                                .setNetwork(context.getString(R.string.app_name))
                                .setAction(context.getString(R.string.ga_action_reco_view))
                                .set("&userId", TokenRecord.getTokenRecord().getUserId())
                                .set("&loginPlatform", TokenRecord.getTokenRecord().getLoginPlatform())
                                .set("&recoHashKey", recoModel.recoHashKey)
                                .setTarget(recoModel.recoHashKey)
                                .build()
                );
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
}
