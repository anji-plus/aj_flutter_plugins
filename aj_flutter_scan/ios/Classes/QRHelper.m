//
//  QRHelper.m
//  aj_flutter_scan
//
//  Created by kean_qi on 2019/7/17.
//

#import "QRHelper.h"
#import <AVFoundation/AVFoundation.h>
// 类方法中不能用成员属性,所以只能定义全局变量
static NSMutableDictionary *_sounds;

@implementation QRHelper
// 懒加载字典
+ (void)initialize
{
    _sounds = [NSMutableDictionary dictionary];
}

+ (void)playAudioWithSoundName:(NSString *)soundName
{
    if (_sounds[soundName] == nil) { // 先通过字典取,没有的话创建
        SystemSoundID soundID = 0;
        
        NSURL *url = [[NSBundle mainBundle] URLForResource:soundName withExtension:nil];
        
        AudioServicesCreateSystemSoundID((__bridge CFURLRef _Nonnull)(url), &soundID);
        
        // 存入集合
        _sounds[soundName] = @(soundID);
    }
    
    AudioServicesPlaySystemSound([_sounds[soundName] unsignedIntValue]);
}
@end
