package io.caly.calyandroid.page.account.add;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import io.caly.calyandroid.util.Logger;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.caly.calyandroid.page.base.BaseAppCompatActivity;
import io.caly.calyandroid.R;
import io.caly.calyandroid.util.Util;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 2. 22
 */

public class AccountAddActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;

    private AccountAddPresenter presenter;
    private AccountAddFragment accountAddView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountadd);

        init();
    }

    void init(){
        ButterKnife.bind(this);

        initToolbar();

        accountAddView = AccountAddFragment.getInstance();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frame_accountadd_container, accountAddView);
        transaction.commit();

        presenter = new AccountAddPresenter(
                accountAddView
        );


    }

    void initToolbar(){
        //set toolbar
        tvToolbarTitle.setText("계정 추가");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Util.RC_INTENT_GOOGLE_SIGNIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            Logger.d(TAG, "handleSignInResult:" + result.isSuccess());
            Logger.d(TAG, "handleSignInResult:" + result.getStatus().getStatus());


            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                Logger.d(TAG, acct.getDisplayName());
                Logger.i(TAG, "id token : " + acct.getIdToken());
                Logger.i(TAG, "serverauthcode : " + acct.getServerAuthCode());
                Logger.i(TAG, "id : " + acct.getId());
                Logger.d(TAG, "email : " + acct.getEmail());

                presenter.requestAddGoogle(acct.getServerAuthCode());

            } else {
                switch (result.getStatus().getStatusCode()){

                    case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                        /*
                        Toast.makeText(
                                getBaseContext(),
                                getString(R.string.toast_msg_login_canceled),
                                Toast.LENGTH_LONG
                        ).show();*/
                        break;
                    case GoogleSignInStatusCodes.SIGN_IN_FAILED:
                        showToast(
                                (R.string.toast_msg_login_fail),
                                Toast.LENGTH_LONG
                        );
                        break;
                    default:
                        showToast(
                                getString(R.string.toast_msg_unknown_error),
                                Toast.LENGTH_LONG
                        );
                        break;
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
