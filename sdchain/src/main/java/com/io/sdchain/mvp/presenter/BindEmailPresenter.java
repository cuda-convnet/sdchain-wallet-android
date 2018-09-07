package com.io.sdchain.mvp.presenter;

import android.content.Context;

import com.io.sdchain.R;
import com.io.sdchain.base.BasePresenter;
import com.io.sdchain.bean.CodeBean;
import com.io.sdchain.common.API;
import com.io.sdchain.common.Constants;
import com.io.sdchain.mvp.view.BindEmailView;
import com.io.sdchain.net.ResponseCallBack;
import com.io.sdchain.utils.SaveObjectTools;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiey on 2017/9/25.
 */

public final class BindEmailPresenter extends BasePresenter<BindEmailView> {

    public BindEmailPresenter(BindEmailView bindEmailView) {
        super(bindEmailView);
    }

    @Override
    public void closeDisposable() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private Disposable disposable;

    /**
     * start countdown
     */
    public void timeDown() {
        final long count = 60;
        Observable.interval(1, TimeUnit.SECONDS)
                .take(61)
                .map(aLong -> {
                    Logger.d(aLong);
                    return count - aLong;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long value) {
                        if (getView() != null) {
                            getView().updateTime(value);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (getView() != null) {
                            getView().updateTimeOver();
                        }
                    }
                });
    }

    /**
     * get email code
     */
    public void getCodeByEmail(String email, String mark, String validStr, Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("account", email);
        map.put("validStr", validStr);
        map.put("mark", mark);
        getData(API.EMAILCODE, map, true, new ResponseCallBack<CodeBean>(context) {
            @Override
            public void onSuccessResponse(CodeBean data, String msg) {
                if (getView() != null) {
                    getView().onCodeSuccess(data, msg);
                }
            }

            @Override
            public void onFailResponse(String msg) {
                if (getView() != null) {
                    getView().onCodeFailed(msg);
                }
            }
        }, null, context.getString(R.string.info72), false, context);
    }

    /**
     * bind email
     */
    public void bindEmail(String smsId, String userId, String code, String password, String email, String validStr, Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("smsId", smsId);
        map.put("validStr", validStr);
        map.put("userId", userId);
        map.put("code", code);
        map.put("password", password);
        map.put("email", email);
        postData(API.BINDEMAIL, map, true, new ResponseCallBack<Object>(context) {
            @Override
            public void onSuccessResponse(Object data, String msg) {
                SaveObjectTools tools = new SaveObjectTools(context);
                tools.saveData(null, Constants.CODEINFO);
                if (getView() != null) {
                    getView().onSuccess(data, msg);
                }
            }

            @Override
            public void onFailResponse(String msg) {
                if (getView() != null) {
                    getView().onFailed(msg);
                }
            }
        }, null, context.getString(R.string.info73), true, context);
    }
}
