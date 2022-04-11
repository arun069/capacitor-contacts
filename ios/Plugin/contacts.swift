import Foundation

@objc public class contacts: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
