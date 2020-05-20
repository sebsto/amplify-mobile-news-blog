// swiftlint:disable all
import Amplify
import Foundation

public struct Note: Model {
  public let id: String
  public var content: String
  
  public init(id: String = UUID().uuidString,
      content: String) {
      self.id = id
      self.content = content
  }
}