// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package screen.utils;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class DetectMediaProjectionPromptService extends AccessibilityService {
    public final LogUtils log = new LogUtils(
            "GlobalActionBarService", "a bug has occurred, this should not happen"
    );
    private boolean serviceConnected = false;

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        int r = super.onStartCommand(intent, flags, startId);
        Bundle extras = intent.getExtras();
        if(extras.containsKey("data")) {
            int data = extras.getInt("data");
            if (data == 1) {
                log.log("recieved data 1");
                if (serviceConnected) {
                    // com.android.systemui.media.MediaProjectionPermissionActivity
                    long itterations = 0;
                    while(true) {
                        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
                        String packageName = rootInActiveWindow.getPackageName().toString();
                        if (packageName.equals("com.android.systemui")) {
                            log.log(
                                    "we have identified the system dialog in " + itterations +
                                    " itterations, attempt to verify"
                            );
                            int childCount = rootInActiveWindow.getChildCount();
                            log.log("found " + childCount + " children");
                            if (childCount != 0) {
                                int childIndex = 0;
                                boolean childFound = false;
                                AccessibilityNodeInfo child = null;
                                for (int i = 0; i < childCount; i++) {
                                    child = rootInActiveWindow.getChild(i);
                                    if (child != null) {
                                        CharSequence text = child.getText();
                                        if (text.toString().equals("Start now")) {
                                            childFound = true;
                                            childIndex = i;
                                            break;
                                        }
                                    }
                                }
                                if (childFound) {
                                    log.log("found Start Now button at child " + childIndex);
                                    if (child.isClickable()) {
                                        log.log("child is clickable");
                                        log.log("clicking...");
                                        child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        log.log("clicked child");
                                    }
                                }
                            }
                            break;
                        } else {
                            log.log("we have not identified the system dialog");
                        }
                        itterations++;
                    }
                } else {
                    log.errorNoStackTrace("WARNING: service is not connected");
                }
            }
        }
        return r;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        serviceConnected = true;
        log.log("service connected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // mandatory
    }

    @Override
    public void onInterrupt() {
        // mandatory
    }
}
