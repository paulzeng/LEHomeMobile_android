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

package my.home.lehome.application;

import android.app.Application;
import android.util.Log;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import my.home.common.ComUtil;
import my.home.common.PrefUtil;
import my.home.lehome.helper.PushSDKManager;

public class LEHomeApplication extends Application {
    private final static String TAG = "LEHomeApplication";

    @Override
    public void onCreate() {
        super.onCreate();
//        XGPushConfig.enableDebug(getApplicationContext(), false);
        PrefUtil.setBooleanValue(getApplicationContext(), "PushSDKManager.stopping", false);
        PrefUtil.setBooleanValue(getApplicationContext(), "PushSDKManager.starting", false);
        if (ComUtil.isMainProcess(getApplicationContext())
                && !PrefUtil.getbooleanValue(getApplicationContext(), "pref_save_power_mode", true)) {
            Log.d(TAG, "start application process: " + ComUtil.getProcessName(getApplicationContext()));
            PushSDKManager.startPushSDKService(getApplicationContext(), true);
        }

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .memoryCache(new LruMemoryCache(10 * 1024 * 1024))
                .diskCacheSize(10 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
    }

}
