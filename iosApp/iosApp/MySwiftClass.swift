import Foundation

@objc(MySwiftClass)
@objcMembers
public class MySwiftClass: NSObject {
    public func sayHello(name: String) -> String {
        return "Hello from Swift, \(name)!"
    }
}
