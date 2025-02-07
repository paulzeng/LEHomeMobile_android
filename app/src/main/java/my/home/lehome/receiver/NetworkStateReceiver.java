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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import my.home.common.NetworkUtil;
import my.home.common.PrefUtil;
import my.home.lehome.helper.LocalMsgHelper;
import my.home.lehome.helper.PushSDKManager;
import my.home.lehome.mvp.presenters.MainActivityPresenter;

/**
 * Created by legendmohe on 15/3/8.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    public static final String TAG = "NetworkStateReceiver";
    public static final String VALUE_INTENT_STOP_LOCAL_SERVER = "my.home.lehome.receiver.NetworkStateReceiver:stop";
    public static final String VALUE_INTENT_START_LOCAL_SERVER = "my.home.lehome.receiver.NetworkStateReceiver:start";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            return;
        }
        if (intent.getExtras() == null) {
            return;
        }
        if (PrefUtil.getbooleanValue(context, MainActivityPresenter.APP_EXIT_KEY, false)) {
            Log.d(TAG, "app set exit. ignore network state change.");
            return;
        }
        final NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        Log.d(TAG, "NetworkInfo: " + info);
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                String ssid = NetworkUtil.getFormatSSID(context);
                String prefSSID = LocalMsgHelper.getLocalSSID(context);
                if (ssid.equals(prefSSID)) {
                    Log.d(TAG, "start " + "LocalMessageService. type: WIFI. reason: match SSID");
                    if (LocalMsgHelper.startLocalMsgService(context)) {
                        Intent startIntent = new Intent(VALUE_INTENT_START_LOCAL_SERVER);
                        context.sendBroadcast(startIntent);
                        if (PrefUtil.getbooleanValue(context, "pref_save_power_mode", true)) {
                            PushSDKManager.stopPushSDKService(context);
                        }
                    }
                } else {
                    Log.d(TAG, "stop " + "LocalMessageService. type: WIFI. reason: miss SSID");
                    PushSDKManager.startPushSDKService(context);
                    LocalMsgHelper.stopLocalMsgService(context);
                    Intent stopIntent = new Intent(VALUE_INTENT_STOP_LOCAL_SERVER);
                    context.sendBroadcast(stopIntent);
                }
            } else {
                Log.d(TAG, "stop " + "LocalMessageService. type: UNKNOWN. reason: NOT WIFI");
                PushSDKManager.startPushSDKService(context);
                LocalMsgHelper.stopLocalMsgService(context);
                Intent stopIntent = new Intent(VALUE_INTENT_STOP_LOCAL_SERVER);
                context.sendBroadcast(stopIntent);
            }
        }
    }
}
