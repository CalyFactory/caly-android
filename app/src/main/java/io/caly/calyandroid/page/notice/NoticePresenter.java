package io.caly.calyandroid.page.notice;

import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import io.caly.calyandroid.R;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.dataModel.NoticeModel;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.NoticeResponse;
import io.caly.calyandroid.page.base.BasePresenter;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class NoticePresenter extends BasePresenter implements NoticeContract.Presenter {

    NoticeFragment noticeView;

    public NoticePresenter(NoticeFragment noticeView){
        this.noticeView = noticeView;

        noticeView.setPresenter(this);
    }

    @Override
    public void loadNotice(){

        noticeView.changeProgressState(true);

        ApiClient.getService().notices(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<NoticeResponse>() {
            @Override
            public void onResponse(Call<NoticeResponse> call, Response<NoticeResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());
                noticeView.changeProgressState(false);
                NoticeResponse body = response.body();
                switch (response.code()){
                    case 200:
                        for(NoticeModel noticeModel : body.payload.data){
                            noticeView.addListItem(noticeModel);
                        }
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        Logger.e(TAG,"status code : " + response.code());
                        noticeView.showToast(
                                (R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        );
                        break;
                }
            }

            @Override
            public void onFailure(Call<NoticeResponse> call, Throwable t) {
                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                noticeView.changeProgressState(false);
                noticeView.showToast(
                        (R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                );
            }
        });
    }
}
