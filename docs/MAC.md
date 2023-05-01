# MAC 常用命令
## 获取权限
`chmod u+rwx /Users/nemo/.bashrc`


## 打开文件
`open ~/.bashrc`


## 创建 .bashrc
```
// 查看当前已安装的jdk版本
/usr/libexec/java_home -V

// 创建.bashrc
cd ~/
touch .bashrc
nano .bashrc
// 添加ANDROID和JAVA环境变量
export ANDROID_HOME=/Users/nemo/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.6.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
// Ctrl+X，按Y保存后，控制台输入下面内容使其生效
source ~/.bashrc
// 验证是否正确
echo $JAVA_HOME
echo $ANDROID_HOME
java -version
// 寻找某个jdk版本安装路径，比如这里是17
/usr/libexec/java_home -v 17

我们添加了一个名为 ANDROID_HOME 的环境变量，并将其值设置为 Android SDK 的安装目录。
我们还将 $PATH 变量扩展了 $ANDROID_HOME/platform-tools 和 $ANDROID_HOME/tools 目录，
以使 Android 工具可用于终端中的所有位置。
保存并退出文件编辑器。如果您使用的是 nano 编辑器，则可以按下 Ctrl + X，然后按下 Y 确认保存。
在终端中使用以下命令来使 .bashrc 文件中的更改生效：source ~/.bashrc
```

## 