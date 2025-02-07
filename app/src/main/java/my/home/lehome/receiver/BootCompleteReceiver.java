/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package my.home.lehome.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import my.home.common.PrefUtil;
import my.home.lehome.helper.PushSDKManager;
import my.home.lehome.mvp.presenters.MainActivityPresenter;

/**
 * Created by legendmohe on 15/4/1.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private final static String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PrefUtil.getbooleanValue(context, MainActivityPresenter.APP_EXIT_KEY, false)) {
            Log.d(TAG, "app set exit. ignore boot complete state change.");
            return;
        }
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(TAG, "start PushSDK.");
            PushSDKManager.startPushSDKService(context);
        }
    }
}
