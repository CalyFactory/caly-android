package io.caly.calyandroid.page.account.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.R;
import io.caly.calyandroid.adapter.AccountListAdapter;
import io.caly.calyandroid.exception.HttpResponseParsingException;
import io.caly.calyandroid.exception.UnExpectedHttpStatusException;
import io.caly.calyandroid.model.LoginPlatform;
import io.caly.calyandroid.model.dataModel.AccountModel;
import io.caly.calyandroid.model.event.AccountListLoadingEvent;
import io.caly.calyandroid.model.event.AccountListRefreshEvent;
import io.caly.calyandroid.model.orm.TokenRecord;
import io.caly.calyandroid.model.response.AccountResponse;
import io.caly.calyandroid.page.account.add.AccountAddFragment;
import io.caly.calyandroid.page.base.BaseFragment;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jspiner on 2017. 5. 16..
 */

public class AccountListFragment extends BaseFragment {

    @Bind(R.id.recycler_accountlist)
    RecyclerView recyclerList;

    @Bind(R.id.linear_loading_parent)
    LinearLayout linearLoading;

    AccountListAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;


    public static AccountListFragment getInstance(){
        return new AccountListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accountlist, container, false);

        ButterKnife.bind(this, view);

        init();
        return view;
    }

    void init() {

        //set recyclerview
        recyclerList.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerList.setLayoutManager(layoutManager);

        ArrayList<AccountModel> accountModels = new ArrayList<>();
        recyclerAdapter = new AccountListAdapter(getActivity(), accountModels);
        recyclerList.setAdapter(recyclerAdapter);


        loadAccountList();

    }

    void loadAccountList(){
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

                        recyclerAdapter.setData(accountList);
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    default:
                        Crashlytics.logException(new UnExpectedHttpStatusException(call, response));
                        Logger.e(TAG,"status code : " + response.code());
                        showToast(
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

                Toast.makeText(
                        getBaseContext(),
                        getString(R.string.toast_msg_network_error),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Subscribe
    public void onAccountListLoadingCallback(AccountListLoadingEvent event){
        if(event.enable){
            linearLoading.setVisibility(View.VISIBLE);
        }
        else{
            linearLoading.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onAccountLIstRefreshEventCallback(AccountListRefreshEvent event){
        Logger.i(TAG, "onAccountLIstRefreshEventCallback");
        loadAccountList();
    }
}
