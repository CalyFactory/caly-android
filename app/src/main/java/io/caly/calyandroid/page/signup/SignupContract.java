package io.caly.calyandroid.page.signup;

import android.os.Bundle;

import io.caly.calyandroid.model.Gender;
import io.caly.calyandroid.page.base.BasePresenterModel;
import io.caly.calyandroid.page.base.BaseView;

/**
 * Copyright 2017 JSpiner. All rights reserved.
 *
 * @author jspiner (jspiner@naver.com)
 * @project CalyAndroid
 * @since 17. 5. 16
 */

public class SignupContract {

    public interface View extends BaseView<Presenter> {
        void changeProgressState(boolean isLoading);
        void setPolicyCheckState(boolean isChecked);
        void startEventListActivity();
        void updateButton();
        Bundle getIntentBundle();
    }

    public interface Presenter extends BasePresenterModel {
        void requestSignup();
        boolean checkEnable();
        INPUT_STATE getInputState();
        void setGender(Gender gender);
        void onGenderCheckChanged();
        void onPolicyCheckChanged();
        void onBirthTextChanged();
        void onSignupClick();
        void onEdtAreaClick();
    }
}
