package io.caly.calyandroid.page.account.add;

import io.caly.calyandroid.page.base.BasePresenterModel;
import io.caly.calyandroid.page.base.BaseView;
import io.caly.calyandroid.page.splash.SplashContract;

/**
 * Created by jspiner on 2017. 5. 15..
 */

public class AccountAddContract {

    public interface View extends BaseView<AccountAddContract.Presenter> {
        void startGoogleAuthActivity();
        void changeProgressState(boolean isLoading);
    }

    public interface Presenter extends BasePresenterModel {
        void requestAddGoogle(String authCode);
        void requestAddCaldav(String loginPlatform, String userId, String userPw);
        void requestAddAccount(String loginPlatform, String userId, String userPw, String authCode);
    }
}
