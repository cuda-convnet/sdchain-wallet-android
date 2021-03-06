package com.io.sdchain.mvp.presenter;

import android.content.Context;

import com.io.sdchain.R;
import com.io.sdchain.base.BasePresenter;
import com.io.sdchain.bean.CodeBean;
import com.io.sdchain.common.API;
import com.io.sdchain.common.Constants;
import com.io.sdchain.mvp.view.RegisterPhoneView;
import com.io.sdchain.net.ResponseCallBack;
import com.io.sdchain.utils.SaveObjectTools;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiey on 2017/9/21.
 */

public final class RegisterPhonePresenter extends BasePresenter<RegisterPhoneView> {

    public RegisterPhonePresenter(RegisterPhoneView registerPhoneView) {
        super(registerPhoneView);
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
                .map(aLong -> count - aLong)
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
     * get code
     */
    public void getCodeByPhone(String phone, String mark, String randomPic, String validStr, Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("account", phone);
        map.put("validStr", validStr);
        map.put("mark", mark);
        map.put("randomPic", randomPic);
        getDataWithUUID(API.PHONECODE, map, true, new ResponseCallBack<CodeBean>(context) {
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
        }, null, "", false, context);
    }

    /**
     * verify code
     */
    public void validatePhone(CodeBean codeBean, String validStr, Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", codeBean.getSmsId());
        map.put("account", codeBean.getUserName());
        map.put("validStr", validStr);
        map.put("code", codeBean.getSmsCode());
        getData(API.REGISTERBYPHONE, map, true, new ResponseCallBack<CodeBean>(context) {
            @Override
            public void onSuccessResponse(CodeBean data, String msg) {
                SaveObjectTools tools = new SaveObjectTools(context);
                tools.saveData(data, Constants.CODEINFO);
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
        }, null, "", true, context);
    }

    /**
     * get image code
     *
     * @param isShow
     * @param context
     */
    public void getImageCode(boolean isShow, Context context) {
        HashMap<String, String> map = new HashMap<String, String>();
        getDataWithUUID(API.IMAGECODE, map, true, new ResponseCallBack<String>(context) {
            @Override
            public void onSuccessResponse(String data, String msg) {
                if (getView() != null) {
                    getView().onGetCodeSuccess(data, msg);
                }
            }

            @Override
            public void onFailResponse(String msg) {
                if (getView() != null) {
                    getView().onGetCodeFailed(msg);
                }
            }
        }, null, context.getString(R.string.info95), isShow, context);
    }

}
