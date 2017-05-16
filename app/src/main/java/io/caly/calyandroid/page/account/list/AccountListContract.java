package io.caly.calyandroid.page.account.list;

import java.util.ArrayList;

import io.caly.calyandroid.model.dataModel.AccountModel;
import io.caly.calyandroid.page.base.BasePresenterModel;
import io.caly.calyandroid.page.base.BaseView;
import io.caly.calyandroid.page.splash.SplashContract;

/**
 * Created by jspiner on 2017. 5. 16..
 */

public class AccountListContract {

    public interface View extends BaseView<AccountListContract.Presenter> {
        void setListData(ArrayList<AccountModel> accountList);
    }

    public interface Presenter extends BasePresenterModel {
        void loadAccountList();
    }
}
