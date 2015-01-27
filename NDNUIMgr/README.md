# ndnmanager
Develop an Android GUI application to start, stop, and configure NFD.

# Requirement
Cross-compiled NFD, as produced by #1988, requires rooted device.
Poot-debug.apk is the miracle worker here. In order for it to work, it needs some libraries provided by Ministro.apk. 
If ministro is already installed, you can skip installing it again. If you don't have access to the googleplay app store,
there is an apk for ministro attached to this post.

Steps Download Poot-debug.apk and run adb install Poot-debug.apk after connecting the device via usb port.
Install ministro.apk from google app store.
