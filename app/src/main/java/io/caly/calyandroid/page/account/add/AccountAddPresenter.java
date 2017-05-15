package io.caly.calyandroid.page.account.add;

import io.caly.calyandroid.page.base.BasePresenter;

/**
 * Created by jspiner on 2017. 5. 15..
 */

public class AccountAddPresenter extends BasePresenter implements AccountAddContract.Presenter {

    public AccountAddFragment accountAddView;

    public AccountAddPresenter(AccountAddFragment accountAddView){
        this.accountAddView = accountAddView;

        accountAddView.setPresenter(this);
    }

}
