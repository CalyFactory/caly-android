package io.caly.calyandroid.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.Activity.WebViewActivity;
import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.Model.DataModel.RecoModel;
import io.caly.calyandroid.Model.Event.RecoMoreClickEvent;
import io.caly.calyandroid.Model.ORM.TokenRecord;
import io.caly.calyandroid.R;
import io.caly.calyandroid.Util.BusProvider;
import io.caly.calyandroid.Util.StringFormmater;
import io.caly.calyandroid.Util.Util;

/**
 * Created by jspiner on 2017. 2. 27..
 */

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.ViewHolder>  {

    //로그에 쓰일 tag
    private static final String TAG = CalyApplication.class.getSimpleName() + "/" + RecommendListAdapter.class.getSimpleName();

    private ArrayList<RecoModel> dataList;

    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View view;

        @Bind(R.id.tv_reco_title)
        TextView tvRecoTitle;

        @Bind(R.id.imv_reco_food)
        ImageView imvFood;

//        @Bind(R.id.imv_reco_map)
//        ImageView imvMap;

        @Bind(R.id.tv_reco_map)
        TextView tvMap;

        @Bind(R.id.tv_reco_distance)
        TextView tvRecoDistance;

        @Bind(R.id.tv_reco_hashtag)
        TextView tvRecoHashtag;

        @Bind(R.id.imv_reco_share)
        ImageView imvShare;

        /*
        @Bind(R.id.imv_reco_more)
        ImageView imvRecoMore;
*/
        Context context;

        public ViewHolder(Context context, View view){
            super(view);

            this.view = view;
            this.context = context;
            ButterKnife.bind(this, view);

        }
    }

    public RecommendListAdapter(Context context, ArrayList<RecoModel> dataList){
        this.dataList = dataList;
        this.context = context;
    }


    public void addItem(int position, RecoModel data){
        dataList.add(position, data);
        notifyItemInserted(position);
    }

    public void addItems(List<RecoModel> data){
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recolist_row, parent, false);

        ViewHolder holder = new ViewHolder(parent.getContext(), view);
        /*
        Picasso.with(parent.getContext())
                .load(R.drawable.sample_food_1)
                .into(holder.imvFood);*/
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final RecoModel recoModel = dataList.get(position);

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

                RecoModel recoModel = dataList.get(position);

//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(recoModel.deepUrl));
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", recoModel.deepUrl);
                intent.putExtra("recoHashKey", recoModel.recoHashKey);
                intent.putExtra("eventHashKey", recoModel.eventHashKey);
                context.startActivity(intent);

                Tracker t = ((CalyApplication)((Activity)context).getApplication()).getDefaultTracker();
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

        /*
        holder.imvRecoMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProvider.getInstance().post(new RecoMoreClickEvent(recoModel));

            }
        });
*/

        holder.tvMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RecoModel recoModel = dataList.get(position);
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(recoModel.mapUrl));
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", recoModel.mapUrl);
                context.startActivity(intent);

                ((Activity)context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);


                Tracker t = ((CalyApplication)((Activity)context).getApplication()).getDefaultTracker();
                t.setScreenName(this.getClass().getName());
                t.send(
                        new HitBuilders.EventBuilder()
                                .setCategory(context.getString(R.string.ga_action_button_click))
                                .setAction("onMapClick")
                                .set("1","1")
                                .build()
                );
            }
        });

        holder.imvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] snsList = {
                        "com.kakao.talk", //kakaotalk
                };
                boolean sended = false;
                for(String snsPackage : snsList){
                    if(Util.isPackageInstalled(snsPackage)){

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,"[캘리] 여기어때요? \n" + dataList.get(position).deepUrl);
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

    public RecoModel getItem(int position){
        return dataList.get(position);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
