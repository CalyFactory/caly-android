package io.caly.calyandroid.page.account.list;

import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.util.ArrayList;

import io.caly.calyandroid.R;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.LoginPlatform;
import io.caly.calyandroid.model.dataModel.AccountModel;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.AccountResponse;
import io.caly.calyandroid.page.base.BasePresenter;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jspiner on 2017. 5. 16..
 */

public class AccountListPresenter extends BasePresenter implements AccountListContract.Presenter {

    AccountListFragment accountListView;

    public AccountListPresenter(AccountListFragment accountListView){
        this.accountListView = accountListView;

        accountListView.setPresenter(this);
    }

    @Override
    public void loadAccountList(){
        Logger.i(TAG, "loadAccountList");
        ApiClient.getService().accountList(
                TokenRecord.getTokenRecord().getApiKey()
        ).enqueue(new Callback<AccountResponse>() {
            @Override
            public void onResponse(Call<AccountResponse> call, Response<AccountResponse> response) {
                Logger.d(TAG,"onResponse code : " + response.code());

                switch (response.code()){
                    case 200:
                        AccountResponse body = response.body();

                        ArrayList<AccountModel> googleAccountList = new ArrayList<AccountModel>();
                        ArrayList<AccountModel> naverAccountList = new ArrayList<AccountModel>();
                        ArrayList<AccountModel> appleAccountList = new ArrayList<AccountModel>();

                        for(AccountModel accountModel : body.payload.data){
                            switch (LoginPlatform.getInstance(accountModel.loginPlatform)){
                                case CALDAV_NAVER:
                                    naverAccountList.add(accountModel);
                                    break;
                                case CALDAV_ICAL:
                                    appleAccountList.add(accountModel);
                                    break;
                                case GOOGLE:
                                    googleAccountList.add(accountModel);
                                    break;
                            }
                        }

                        ArrayList<AccountModel> accountList = new ArrayList<AccountModel>();

                        if(googleAccountList.size()!=0){
                            accountList.add(new AccountModel("Google Calendar 계정"));
                            accountList.addAll(googleAccountList);
                        }
                        if(naverAccountList.size()!=0){
                            accountList.add(new AccountModel("Naver Calendar 계정"));
                            accountList.addAll(naverAccountList);
                        }
                        if(appleAccountList.size()!=0){
                            accountList.add(new AccountModel("Apple Calendar 계정"));
                            accountList.addAll(appleAccountList);
                        }

                        accountListView.setListData(accountList);
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        Logger.e(TAG,"status code : " + response.code());
                        accountListView.showToast(
                                (R.string.toast_msg_server_internal_error),
                                Toast.LENGTH_LONG
                        );
                        break;
                }
            }

            @Override
            public void onFailure(Call<AccountResponse> call, Throwable t) {

                Logger.e(TAG,"onfail : " + t.getMessage());
                Logger.e(TAG, "fail " + t.getClass().getName());


                if(t instanceof MalformedJsonException || t instanceof JsonSyntaxException){
                    Crashlytics.logException(new HttpResponseParsingException(call, t));
                }

                accountListView.showToast(
                        R.string.toast_msg_network_error,
                        Toast.LENGTH_LONG
                );
            }
        });
    }
}
