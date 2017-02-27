/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.djjie.mvpluglib.presenter;


import android.content.Context;

/**
 * View层有可能调用到的方法，都要在这里声明
 */
public interface MVPlugPresenter {
    void onRefresh(int tabId);//下拉刷新
    void startTask(int tabId);//首次载入
    void clearTask();
    Context getContext();
}
