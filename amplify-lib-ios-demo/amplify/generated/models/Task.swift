// swiftlint:disable all
import Amplify
import Foundation

public struct Task: Model {
  public let id: String
  public var title: String
  public var description: String?
  public var status: String?
  
  public init(id: String = UUID().uuidString,
      title: String,
      description: String? = nil,
      status: String? = nil) {
      self.id = id
      self.title = title
      self.description = description
      self.status = status
  }
}