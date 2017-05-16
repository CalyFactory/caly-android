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
import io.caly.calyandroid.page.splash.SplashContract;
import io.caly.calyandroid.util.ApiClient;
import io.caly.calyandroid.util.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jspiner on 2017. 5. 16..
 */

public class AccountListFragment extends BaseFragment implements AccountListContract.View{

    @Bind(R.id.recycler_accountlist)
    RecyclerView recyclerList;

    @Bind(R.id.linear_loading_parent)
    LinearLayout linearLoading;

    AccountListAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;

    AccountListContract.Presenter presenter;

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


        presenter.loadAccountList();

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
        presenter.loadAccountList();
    }

    @Override
    public void setPresenter(AccountListContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setListData(ArrayList<AccountModel> accountList) {


        recyclerAdapter.setData(accountList);
        recyclerAdapter.notifyDataSetChanged();
    }
}
