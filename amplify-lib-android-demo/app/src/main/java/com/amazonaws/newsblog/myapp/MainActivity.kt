package com.amazonaws.newsblog.myapp

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
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.Note
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.hub.HubEvent
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin
import com.amplifyframework.predictions.models.LanguageType
import java.util.*


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

            // listen to auth event
            Amplify.Hub.subscribe(HubChannel.AUTH) {
                    hubEvent: HubEvent<*> -> Log.i("Tutorial", """HUB EVENT:${hubEvent.getName()}""")
            }

            // listen to data store subscriptions
            // test subscriptions using the AppSync Console
            // Query Notes
            /**
            query GetNotes {
            listNotes {
                items {
                    id
                    content
                    _version
                    _deleted
                }
            }}
             */

            // Mutate a note to trigger a subscription
            /**
            mutation CreateNote {
            createNote(
                input: {
                    content: "Added through AppSync Console"
                }
            ) {
                id,
                content,
                _deleted
                _version,
                _lastChangedAt,
                }
            }
             */

            // Delete a note
            /**
            mutation DeleteNote {
                deleteNote(input: {
                    id: "38D81154-7EF5-49EF-BD54-5476B8264DA9"
                    _version: 2
                }
                ){
                    id
                    content
                    _deleted
                    _version
                    _lastChangedAt
                }
            }
             */

            Amplify.DataStore.observe(
                Note::class.java,
                { cancelable -> Log.i( TAG, "Observation began.")  },
                { postChanged  ->
                    val note: Note = postChanged.item()
                    Log.i(TAG, "== SUBSCRIPTION == Note: $note")
                },
                { failure -> Log.e(TAG, "Observation failed.", failure) },
                { ->  Log.i(TAG, "Observation complete.") }
            )

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

    fun createNote(view: View?) {
        Log.i(TAG, "Creating Note")
        val c = Calendar.getInstance()

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val item: Note = Note.builder()
            .content("--$hour:$minute-- Build Android application")
            .build()

        Amplify.DataStore.save(
            item,
            { saved -> Log.i(TAG, "Saved item: " + saved) },
            { failure -> Log.e(TAG, "Could not save item to DataStore", failure) }
        )
    }

    fun queryNotes(view: View?) {
        Log.i(TAG, "Querying Notes")
        Amplify.DataStore.query(
            Note::class.java,
            { allNotes ->
                while (allNotes.hasNext()) {
                    val note    = allNotes.next()
                    val content = note.content;

                    Log.i(TAG, "==== Note ==== Content: $content")

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
            { result ->
                Log.i(TAG, result.translatedText)
                tv.setText(result.translatedText)
            },
            { failure -> Log.e(TAG,failure.localizedMessage) }
        )
    }
}

