//
//  BackButton.m
//  aj_flutter_scan
//
//  Created by kean_qi on 2019/7/18.
//

#import "BackButton.h"

@implementation BackButton



- (void)drawRect:(CGRect)rect{
    UIBezierPath *path = [UIBezierPath bezierPath];
    [path moveToPoint:CGPointMake(self.frame.size.width * 0.65, self.frame.size.height * 0.25)];
    [path addLineToPoint:CGPointMake(self.frame.size.width * 0.35, self.frame.size.height * 0.5)];
    [path addLineToPoint:CGPointMake(self.frame.size.width * 0.65, self.frame.size.height * 0.75)];
    path.lineWidth = 2;
    [[UIColor whiteColor] setStroke];
    [path stroke];
}


@end
