#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.static_framework = true
  s.name             = 'flutter_umeng_analytics'
  s.version          = '0.0.1'
  s.summary          = 'Flutter plugin for umeng:analytics'
  s.description      = <<-DESC
Flutter plugin for umeng:analytics
                       DESC
  s.homepage         = 'http://example.com'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'UMengAnalytics-NO-IDFA'
  
  s.ios.deployment_target = '8.0'
end

