package com.amazonaws.newsblog.myapp

import java.util.Calendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.newsblog.myapp.ui.main.MainFragment
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthSession
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.Consumer
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.Priority
import com.amplifyframework.datastore.generated.model.Todo
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.hub.HubEvent
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin
import com.amplifyframework.predictions.models.LanguageType
import okhttp3.internal.notify
import kotlin.reflect.typeOf

// https://gist.github.com/TrekSoft/33190858041e6e0810d1324735bb0666

class MainActivity : AppCompatActivity() {

    val TAG = "Tutorial"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }

        // initialize Amplify
        // https://next-docs.amplify.aws/lib/getting-started/integrate/q/platform/android
        try {
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSPredictionsPlugin())
            Amplify.configure(applicationContext)

            Log.i(TAG, "Initialized Amplify")

            Amplify.Hub.subscribe(HubChannel.AUTH) {
                    hubEvent: HubEvent<*> -> Log.i("Tutorial", """HUB EVENT:${hubEvent.getName()}""")
            }

        } catch (e: AmplifyException) {

            Log.e(TAG, "Could not initialize Amplify", e)
        }
    }

    override fun onNewIntent(intent: Intent) {
        Log.i(TAG, "onNewIntent")
        super.onNewIntent(intent)
        if (intent.data != null && "myapp" == intent.data!!.scheme) {
            Log.i(TAG, "handleWebSigninResponse")
            Amplify.Auth.handleWebUISignInResponse(intent)
        }
    }

    fun signOut(view: View?) {
        Log.i("Tutorial", "Initiate Signout Sequence")

        Amplify.Auth.signOut(
            {  -> Log.i(TAG, "Signed out!") },
            { error -> Log.e(TAG, error.toString()) }
        )

    }

    fun signIn(view: View?) {
        Log.i(TAG, "Initiate Signin Sequence")

        Amplify.Auth.signInWithWebUI(
            this,
            { result: AuthSignInResult ->
                Log.i(TAG, result.toString())

                // fetch session details (incl token)
                Amplify.Auth.fetchAuthSession(
                    { result -> Log.i(TAG, result.toString()) },
                    { error -> Log.i(TAG, error.toString()) }
                )

                // get user details
                val user = Amplify.Auth.currentUser
                Log.i(TAG, user.toString() )

            },
            { error: AuthException -> Log.e(TAG, error.toString()) }
        )

    }

    fun createTodo(view: View?) {
        Log.i(TAG, "Creating Todo")
        val c = Calendar.getInstance()

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val item: Todo = Todo.builder()
            .name("--$hour:$minute-- Build Android application")
            .description("Build an Android Application using Amplify")
            .build()

        Amplify.DataStore.save(
            item,
            { success -> Log.i(TAG, "Saved item: " + success.item.name) },
            { error -> Log.e(TAG, "Could not save item to DataStore", error) }
        )
    }

    fun queryTodo(view: View?) {
        Log.i(TAG, "Querying Todos")
        Amplify.DataStore.query(
            Todo::class.java,
            { todos ->
                while (todos.hasNext()) {
                    val todo = todos.next()
                    val name = todo.name;
                    val priority: Priority? = todo.priority
                    val description: String? = todo.description

                    Log.i(TAG, "==== Todo ====")
                    Log.i(TAG, "Name: $name")

                    if (priority != null) {
                        Log.i(TAG, "Priority: $priority")
                    }

                    if (description != null) {
                        Log.i(TAG, "Description: $description")
                    }
                }
            },
            { failure -> Log.e(TAG, "Could not query DataStore", failure) }
        )
    }

    fun translate(view: View?) {
        Log.i(TAG, "Translating")

        val et : EditText = findViewById(R.id.toBeTranslated)
        val tv : TextView = findViewById(R.id.translated)

        Amplify.Predictions.translateText(
            et.text.toString(),
            LanguageType.ENGLISH,
            LanguageType.FRENCH,
            { success ->
                Log.i(TAG, success.translatedText)
                tv.setText(success.translatedText)
            },
            { failure -> Log.e(TAG,failure.localizedMessage) }
        )
    }
}

