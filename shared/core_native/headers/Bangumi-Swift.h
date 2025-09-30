
#include <Foundation/Foundation.h>

@class NSString;

@interface MySwiftClass : NSObject
- (NSString *_Nonnull)sayHelloWithName:(NSString *_Nonnull)name;

- (nonnull instancetype)init;
@end


@protocol Observer
@required
- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary <NSKeyValueChangeKey, id> *)change
                       context:(void *)context;
@end;