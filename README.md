#背景

     公司使用2.4版本的mongodb，不带有文件压缩的功能，占用空间太大，已达到瓶颈
     所以为了将mongodb升级到4.0以上，才有了这个工具项目的开发
     
#开发思路

    1.查询数据库中的文件信息
    2.根据数据库的文件信息，从旧版mongodb下载文件到本地
    3.把下载下来的文件上传的新版本的mongodb上
    4.数据库创建一张表，保存错误信息（旧版mongodb文件不存在或者旧版mongodb的大小和数据库记录的大小不一致的情况）
    
#问题及解决方案

    问题：
        1.从开发思路的2，3来看，会有一个问题，那就是java的driver驱动包冲突
    解决方案：
        1.依赖旧版的驱动包，从旧版本下载文件，再用Runtime.exec()来执行命令行上传
        mongofiles --host 127.0.0.1:27017 -d filedb -l "\Temp\05b949d7c52b4f6e995a92d7a9cae756" put_id "xx.pdf" "d68e39d60b174abaa6be530d936211d6"
        
#插件依赖

    开发工具需要安装Lombok插件
    Eclipse: https://www.cnblogs.com/romulus/p/11109610.html
    IDEA: marketplace直接下载安装即可  
          
#Maven:

    先用maven工具执行 jfx:jar 打出jfx包
    
#Exe打包:

    控制台进入target文件夹，执行以下命令，用javafxpackager打出exe
    javafxpackager -deploy -native image -appclass com.mongodb.sync.MongoSyncApplication -srcdir ./ -outdir ./exe -outfile MongoSync -name MongoSync
    
    打包完成后，把readme.docx和REPO_FILE_SYNC_RESULT.sql两个文件复制放到target\exe\bundles\MongoSync\app目录下