// swiftlint:disable all
import Amplify
import Foundation

// Contains the set of classes that conforms to the `Model` protocol. 

final public class AmplifyModels: AmplifyModelRegistration {
  public let version: String = "a617c268ed151b6f82fb17a6b7fde2c7"
  
  public func registerModels(registry: ModelRegistry.Type) {
    ModelRegistry.register(modelType: Task.self)
    ModelRegistry.register(modelType: Note.self)
  }
}