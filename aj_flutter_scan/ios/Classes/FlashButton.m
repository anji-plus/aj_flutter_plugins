//
//  FlashButton.m
//  aj_flutter_scan
//
//  Created by kean_qi on 2019/7/18.
//

#import "FlashButton.h"

@interface FlashButton()
@property(nonatomic, retain) UIView *line;
@property(nonatomic,strong)   UIColor     *drawColor;

@end
@implementation FlashButton


- (void)setNeedsDrawColor:(UIColor *)drawColor {
    self.drawColor = drawColor;
    [self setNeedsDisplay];
}

- (void)drawRect:(CGRect)rect{
    
    CGFloat _h = self.frame.size.height * 0.8;
    
    UIBezierPath *path = [UIBezierPath bezierPath];
    [path moveToPoint:CGPointMake(self.frame.size.width * 0.25, _h * 0.1)];
    [path addLineToPoint:CGPointMake(self.frame.size.width * 0.75, _h * 0.1)];
    
    [path moveToPoint:CGPointMake(self.frame.size.width * 0.25, _h * 0.1)];
    [path addLineToPoint:CGPointMake(self.frame.size.width * 0.25, _h * 0.4)];
    
    [path moveToPoint:CGPointMake(self.frame.size.width * 0.25, _h * 0.2)];
    [path addLineToPoint:CGPointMake(self.frame.size.width * 0.75, _h * 0.2)];
    
    
    [path moveToPoint:CGPointMake(self.frame.size.width * 0.75, _h * 0.1)];
    [path addLineToPoint:CGPointMake(self.frame.size.width * 0.75, _h * 0.4)];
    
    [path moveToPoint:CGPointMake(self.frame.size.width * 0.25, _h * 0.4)];
    CGPoint controlPoint1 = CGPointMake(self.frame.size.width * 0.41, _h * 0.5);
    CGPoint controlPoint2 = CGPointMake(self.frame.size.width * 0.25, _h * 0.5);
    [path addQuadCurveToPoint:controlPoint1 controlPoint:controlPoint2];
    
    [path moveToPoint:CGPointMake(self.frame.size.width * 0.75, _h * 0.4)];
    CGPoint controlPoint3 = CGPointMake(self.frame.size.width * 0.59, _h * 0.5);
    CGPoint controlPoint4 = CGPointMake(self.frame.size.width * 0.75, _h * 0.5);
    [path addQuadCurveToPoint:controlPoint3 controlPoint:controlPoint4];
    
    [path moveToPoint:CGPointMake(self.frame.size.width * 0.37, _h * 0.8)];
    [path addLineToPoint:CGPointMake(self.frame.size.width * 0.62, _h * 0.8)];
    
//    [path moveToPoint:CGPointMake(self.frame.size.width * 0.65, _h * 0.25)];
//    [path addLineToPoint:CGPointMake(self.frame.size.width * 0.35, _h * 0.5)];
//    [path addLineToPoint:CGPointMake(self.frame.size.width * 0.65, _h * 0.75)];
    path.lineWidth = 1;
    [self.drawColor setStroke];
    [path stroke];
    
    UIBezierPath *path1 = [UIBezierPath bezierPathWithRect:CGRectMake(self.frame.size.width * 0.37, _h * 0.5, self.frame.size.width * 0.25, _h * 0.4)];
    path1.lineWidth = 1;
    [self.drawColor setStroke];
    [path1 stroke];
}




@end
