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

package com.thirtydegreesray.openhub.mvp.presenter;

import com.thirtydegreesray.dataautoaccess.annotation.AutoAccess;
import com.thirtydegreesray.openhub.AppData;
import com.thirtydegreesray.openhub.dao.DaoSession;
import com.thirtydegreesray.openhub.http.core.HttpObserver;
import com.thirtydegreesray.openhub.http.core.HttpResponse;
import com.thirtydegreesray.openhub.mvp.contract.IProfileContract;
import com.thirtydegreesray.openhub.mvp.model.User;

import javax.inject.Inject;

import retrofit2.Response;
import rx.Observable;

/**
 * Created on 2017/7/18.
 *
 * @author ThirtyDegreesRay
 */

public class ProfilePresenter extends BasePresenter<IProfileContract.View>
        implements IProfileContract.Presenter{

    @AutoAccess String loginId;
    private User user;
    private boolean following = false;

    @Inject
    public ProfilePresenter(DaoSession daoSession) {
        super(daoSession);
    }

    @Override
    public void onViewInitialized() {
        super.onViewInitialized();
        getProfileInfo();
        checkFollowingStatus();
    }

    private void getProfileInfo(){
        mView.getProgressDialog(getLoadTip()).show();
        HttpObserver<User> httpObserver = new HttpObserver<User>() {
            @Override
            public void onError(Throwable error) {
                mView.showErrorToast(getErrorTip(error));
                mView.getProgressDialog(getLoadTip()).dismiss();
            }

            @Override
            public void onSuccess(HttpResponse<User> response) {
                user = response.body();
                mView.showProfileInfo(user);
                mView.getProgressDialog(getLoadTip()).dismiss();
            }
        };
        generalRxHttpExecute(new IObservableCreator<User>() {
            @Override
            public Observable<Response<User>> createObservable(boolean forceNetWork) {
                return getUserService().getUser(forceNetWork, loginId);
            }
        }, httpObserver, true);
    }

    public String getLoginId() {
        return loginId;
    }

    public User getUser() {
        return user;
    }

    public boolean isFollowing() {
        return following;
    }

    public boolean isUser(){
        return user != null && user.isUser();
    }

    public boolean isMe(){
        return user != null && user.getLogin().equals(AppData.INSTANCE.getLoggedUser().getLogin());
    }

    private void checkFollowingStatus(){
        checkStatus(
                getUserService().checkFollowing(loginId),
                new CheckStatusCallback() {
                    @Override
                    public void onChecked(boolean status) {
                        following = status;
                        mView.invalidateOptionsMenu();
                    }
                }
        );
    }

    @Override
    public void followUser(boolean follow) {
        following = follow;
        generalRxHttpExecute(follow ?
                getUserService().followUser(loginId) : getUserService().unfollowUser(loginId), null);
    }
}
