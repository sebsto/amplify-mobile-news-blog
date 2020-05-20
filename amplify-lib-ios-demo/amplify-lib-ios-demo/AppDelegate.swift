//
//  AppDelegate.swift
//  amplify-lib-ios-demo
//
//  Created by Stormacq, Sebastien on 18/05/2020.
//  Copyright © 2020 Stormacq, Sebastien. All rights reserved.
//

import UIKit

import Amplify
import AmplifyPlugins

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        
        do {
            try Amplify.add(plugin: AWSAPIPlugin())
            try Amplify.add(plugin: AWSDataStorePlugin(modelRegistration: AmplifyModels()))
            try Amplify.add(plugin: AWSAuthPlugin())
            try Amplify.configure()
            print("Amplify initialized")
            
            _ = Amplify.Hub.listen(to: .auth) { (payload) in
                switch payload.eventName {
                case HubPayload.EventName.Auth.webUISignIn:
                    print("==HUB== User signed In, update UI")
                case HubPayload.EventName.Auth.signOut:
                    print("==HUB== User sign Out")
                case HubPayload.EventName.Auth.signedOut:
                    print("==HUB== User signed Out, update UI")
                case HubPayload.EventName.Auth.sessionExpired:
                    print("==HUB== Session expired, show sign in aui")
                case HubPayload.EventName.Auth.fetchUserAttributes:
                    print("==HUB== userAttributes are available, update UI")
                default:
                    print("==HUB== \(payload)")
                    break
                }
            }
            
        } catch {
            print("Failed to configure Amplify \(error)")
        }
        
        return true
    }
    
    // MARK: Amplify Auth
    
    func signIn() {
        print("SignIn")
        _ = Amplify.Auth.signInWithWebUI(presentationAnchor: UIApplication.shared.windows.first!) { (result) in
            switch(result) {
                case .success(let result):
                    print(result)
                    
                    // fetch user details
                    _ = Amplify.Auth.fetchUserAttributes() { (result) in
                        switch result {
                        case .success(let session):
                            print(session)
                        case .failure(let error):
                            print(error)
                        }
                    }
                
                case .failure(let error):
                    print("Can not signin \(error)")
            }
        }
    }
    
    func signOut() {
        print("SignOut")
        _ = Amplify.Auth.signOut() { (result) in
            print(result)
            switch(result) {
            case .success():
                print("Signout succeded")
            case .failure(let error):
                print("Signout failed with \(error)")
            }
        }

    }
    
    // MARK: Amplify DataStore

    func query() {
        Amplify.DataStore.query(Note.self) {
            switch $0 {
                case .success(let result):
                    // result will be of type [Note]
                    for note in result {
                        print("Notes: \(note)")
                    }
                case .failure(let error):
                    print("Error listing notes - \(error.localizedDescription)")
            }
        }
    }
    
    func create() {
        let date = Date()
        let df = DateFormatter()
        df.dateFormat = "HH:mm"
        let dateString = df.string(from: date)

        let note = Note(content: "--\(dateString)-- content")
        Amplify.DataStore.save(note) {
            switch $0 {
            case .success:
                print("Added note")
            case .failure(let error):
                print("Error adding note - \(error.localizedDescription)")
            }
        }
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }


}

