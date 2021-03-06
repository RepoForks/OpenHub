/*
 *    Copyright 2017 ThirtyDegreesRay
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.thirtydegreesray.openhub.ui.fragment.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.thirtydegreesray.dataautoaccess.DataAutoAccess;
import com.thirtydegreesray.openhub.AppApplication;
import com.thirtydegreesray.openhub.inject.component.AppComponent;
import com.thirtydegreesray.openhub.mvp.contract.IBaseContract;
import com.thirtydegreesray.openhub.util.Logger;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;

/**
 * Created on 2017/7/18.
 *
 * @author ThirtyDegreesRay
 */

public abstract class BaseFragment<P extends IBaseContract.Presenter>
        extends Fragment implements IBaseContract.View{

    private final String TAG = BaseFragment.class.getSimpleName();

    @Inject
    protected P mPresenter;
    private ProgressDialog mProgressDialog;
    Unbinder unbinder;

    public BaseFragment() {

    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void setupFragmentComponent(AppComponent appComponent);

    protected abstract void initFragment(Bundle savedInstanceState);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Logger.d(TAG, getClass().getSimpleName() + " onCreateView");
        View fragmentView = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, fragmentView);
        initFragment(savedInstanceState);
        mPresenter.onViewInitialized();
        return fragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFragmentComponent(getAppComponent());
        mPresenter.attachView(this);
        DataAutoAccess.getData(this, savedInstanceState);
        mPresenter.onRestoreInstanceState(getArguments());
        mPresenter.onRestoreInstanceState(savedInstanceState);
        Logger.d(TAG, getClass().getSimpleName() + " onCreate");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d(TAG, getClass().getSimpleName() + " onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.d(TAG, getClass().getSimpleName() + " onActivityCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Logger.d(TAG, getClass().getSimpleName() + " onViewStateRestored");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        DataAutoAccess.saveData(this, outState);
        mPresenter.onSaveInstanceState(outState);
        Logger.d(TAG, getClass().getSimpleName() + " onSaveInstanceState");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d(TAG, getClass().getSimpleName() + " onResume");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mPresenter.detachView();
        Logger.d(TAG, getClass().getSimpleName() + " onDestroyView");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.d(TAG, getClass().getSimpleName() + " onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d(TAG, getClass().getSimpleName() + " onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.d(TAG, getClass().getSimpleName() + " onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d(TAG, getClass().getSimpleName() + " onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, getClass().getSimpleName() + " onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.d(TAG, getClass().getSimpleName() + " onDetach");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.with(this).onLowMemory();
    }

    protected AppApplication getAppApplication() {
        return AppApplication.get();
    }

    protected AppComponent getAppComponent(){
        return getAppApplication().getAppComponent();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showProgressDialog(String content) {
        getProgressDialog(content);
        mProgressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }else{
            throw new NullPointerException("dismissProgressDialog: can't dismiss a null dialog, must show dialog first!");
        }
    }

    @Override
    public ProgressDialog getProgressDialog(String content){
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.setMessage(content);
        return mProgressDialog;
    }

    @Override
    public void showToast(String message){
        Toasty.normal(getActivity(), message).show();
    }

    @Override
    public void showInfoToast(String message) {
        Toasty.info(getActivity(), message).show();
    }

    @Override
    public void showSuccessToast(String message) {
        Toasty.success(getActivity(), message).show();
    }

    @Override
    public void showErrorToast(String message) {
        Toasty.error(getActivity(), message).show();
    }

    @Override
    public void showWarningToast(String message) {
        Toasty.warning(getActivity(), message).show();
    }

    @Override
    public void showTipDialog(String content) {
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage(content)
                .setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void showConfirmDialog(String msn, String title, String confirmText
            , DialogInterface.OnClickListener confirmListener) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(msn)
                .setCancelable(true)
                .setPositiveButton(confirmText, confirmListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

}
