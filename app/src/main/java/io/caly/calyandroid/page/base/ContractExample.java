package io.caly.calyandroid.page.base;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

import io.caly.calyandroid.page.splash.SplashContract;

/**
 * Created by jspiner on 2017. 5. 15..
 */

public class ContractExample {

    public interface View extends BaseView<SplashContract.Presenter> {

    }

    public interface Presenter extends BasePresenterModel {

    }
}
