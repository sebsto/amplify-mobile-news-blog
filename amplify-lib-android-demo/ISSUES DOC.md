
# Android Getting Started Doc 

The below are reported in 

https://github.com/aws-amplify/docs/issues/1778
https://github.com/aws-amplify/docs/issues/1779
https://github.com/aws-amplify/amplify-android/issues/483


## Step 3 : Integrate in your App 

**URL** : https://next-docs.amplify.aws/lib/getting-started/integrate/q/platform/android#configure-amplify-and-datastore

Kotlin code snipet is invalid

```kotlin

import com.amplifyframework.AmplifyException
import com.amplifyframework.core.Amplify
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.datastore.AWSDataStorePlugin


        try {
            Amplify.addPlugin(AWSDataStorePlugin()) //new is not a kotlin keyword
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(applicationContext)

            Log.i("Tutorial", "Initialized Amplify")
        } catch (e: AmplifyException) {

            Log.e("Tutorial", "Could not initialize Amplify", e)
        }
```

# Android Getting Started Doc 

## Step 3 : Integrate in your App 

**URL** : https://next-docs.amplify.aws/lib/getting-started/integrate/q/platform/android#configure-amplify-and-datastore


Amplify can not be initialized before an `amplify push`
Running the app, after adding initialization code fails with 

```text
E/.newsblog.myap: Invalid ID 0x00000000.
E/Tutorial: Could not initialize Amplify
    AmplifyException {message=Failed to find amplifyconfiguration., cause=android.content.res.Resources$NotFoundException: Resource ID #0x0, recoverySuggestion=Please check that it has been created.}
        at com.amplifyframework.core.AmplifyConfiguration.fromConfigFile(AmplifyConfiguration.java:24)
        at com.amplifyframework.core.AmplifyConfiguration.fromConfigFile(AmplifyConfiguration.java:3)
        at com.amplifyframework.core.Amplify.configure(Amplify.java:1)
        at com.amazonaws.newsblog.myapp.MainActivity.onCreate(MainActivity.kt:29)
        at android.app.Activity.performCreate(Activity.java:7136)
        at android.app.Activity.performCreate(Activity.java:7127)
        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1271)
        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2893)
        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3048)
        at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:78)
        at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:108)
        at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:68)
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1808)
        at android.os.Handler.dispatchMessage(Handler.java:106)
        at android.os.Looper.loop(Looper.java:193)
        at android.app.ActivityThread.main(ActivityThread.java:6669)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
     Caused by: android.content.res.Resources$NotFoundException: Resource ID #0x0
        at android.content.res.ResourcesImpl.getValue(ResourcesImpl.java:216)
        at android.content.res.ResourcesImpl.openRawResource(ResourcesImpl.java:324)
        at android.content.res.Resources.openRawResource(Resources.java:1281)
        at android.content.res.Resources.openRawResource(Resources.java:1225)
        at com.amplifyframework.core.AmplifyConfiguration.fromConfigFile(AmplifyConfiguration.java:5)
        at com.amplifyframework.core.AmplifyConfiguration.fromConfigFile(AmplifyConfiguration.java:3) 
        at com.amplifyframework.core.Amplify.configure(Amplify.java:1) 
        at com.amazonaws.newsblog.myapp.MainActivity.onCreate(MainActivity.kt:29) 
        at android.app.Activity.performCreate(Activity.java:7136) 
        at android.app.Activity.performCreate(Activity.java:7127) 
        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1271) 
        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2893) 
        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:3048) 
        at android.app.servertransaction.LaunchActivityItem.execute(LaunchActivityItem.java:78) 
        at android.app.servertransaction.TransactionExecutor.executeCallbacks(TransactionExecutor.java:108) 
        at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:68) 
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1808) 
        at android.os.Handler.dispatchMessage(Handler.java:106) 
        at android.os.Looper.loop(Looper.java:193) 
        at android.app.ActivityThread.main(ActivityThread.java:6669) 
        at java.lang.reflect.Method.invoke(Native Method) 
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493) 
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858) 
```

At this stage, an `amplify status` fails too

```text
➜  amplify-lib-android-demo amplify status
| Category | Resource name     | Operation | Provider plugin   |
| -------- | ----------------- | --------- | ----------------- |
| Api      | amplifyDatasource | Create    | awscloudformation |

ENOENT: no such file or directory, open '/Users/stormacq/Documents/amazon/code/amplify/amplify-lib-android-demo/amplify/backend/amplify-meta.json'
Error: ENOENT: no such file or directory, open '/Users/stormacq/Documents/amazon/code/amplify/amplify-lib-android-demo/amplify/backend/amplify-meta.json'
    at Object.openSync (fs.js:461:3)
```

The `amplify/backend/amplify-meta.json` is created after a `modelPush` and Amplify Initialization works only after `amplifyPush`

```text
I/Tutorial: Initialized Amplify
I/amplify:aws-datastore: Creating table: PersistentModelVersion
I/amplify:aws-datastore: Creating table: Todo
I/amplify:aws-datastore: Creating table: PersistentRecord
I/amplify:aws-datastore: Creating table: LastSyncMetadata
I/amplify:aws-datastore: Creating table: ModelMetadata
I/amplify:aws-datastore: Creating index for table: PersistentRecord
```

After doing an amplify push, the examples work (init, save, query) 