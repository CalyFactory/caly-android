package io.caly.calyandroid.page.account.add;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;

import io.caly.calyandroid.CalyApplication;
import io.caly.calyandroid.R;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.LoginPlatform;
import io.caly.calyandroid.model.event.EventListRefreshEvent;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.BasicResponse;
import io.caly.calyandroid.page.base.BasePresenter;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.BusProvider;
import io.caly.calyandroid.util.Logger;
import io.caly.calyandroid.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jspiner on 2017. 5. 15..
 */

public class AccountAddPresenter extends BasePresenter implements AccountAddContract.Presenter {

    public AccountAddFragment accountAddView;

    public AccountAddPresenter(AccountAddFragment accountAddView){
        this.accountAddView = accountAddView;

        accountAddView.setPresenter(this);
    }


    @Override
    public void requestAddCaldav(String loginPlatform, String userId, String userPw){
        requestAddAccount(loginPlatform, userId, userPw, "null");
    }

    @Override
    public void requestAddGoogle(String authCode){
        requestAddAccount(LoginPlatform.GOOGLE.value, "null", "null", authCode);
    }

    @Override
    public void requestAddAccount(String loginPlatform, String userId, String userPw, String authCode){
        Logger.i(TAG, "requestAddAccount");
        accountAddView.changeProgressState(true);
        ApiClient.getService().addAccount(
                TokenRecord.getTokenRecord().getApiKey(),
                loginPlatform,
                userId,
                userPw,
                authCode
        ).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

                accountAddView.changeProgressState(false);
                BasicResponse body = response.body();
                switch (response.code()){
                    case 200:
                        BusProvider.getInstance().post(new EventListRefreshEvent());

                        accountAddView.showToast(
                                (R.string.toast_msg_add_account_success),
                                Toast.LENGTH_LONG
                        );
                        accountAddView.finishActivity();
                        break;
                    case 201: //TODO : 에러라면 code가 300~500번대어야 말이 될듯?
                        accountAddView.showToast(
                                (R.string.toast_msg_add_account_error),
                                Toast.LENGTH_LONG
                        );
                        break;
                    case 401:
                        accountAddView.showToast(
                                (R.string.toast_msg_login_fail),
                                Toast.LENGTH_LONG
                        );
                        break;
                    case 403:
                        accountAddView.showToast(
                                (R.string.toast_msg_add_account_duplicate),
                                Toast.LENGTH_LONG
                        );
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        Logger.e(TAG,"status code : " + response.code());
                        accountAddView.showToast(
                                (R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        );
                        break;
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());

                accountAddView.changeProgressState(false);
                accountAddView.showToast(
                        (R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                );
            }
        });
    }


}
