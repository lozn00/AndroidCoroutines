import com.android.build.gradle.internal.scope.ProjectInfo.Companion.getBaseName
import com.android.build.gradle.internal.tasks.factory.dependsOn
import groovy.lang.Closure
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.utils.addToStdlib.cast

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0-alpha08" apply false
    id("com.android.library") version "8.1.0-alpha08" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}
open class CustomOutDIrTask : DefaultTask() {
    // 在这里添加你的属性和行为
    @Internal
    var flavor: String? = null

    @Internal
    var sourceDir: File? = null

    @Internal
    var destinationDir: File? = null

    @Internal
    var move: Boolean = false
    /* @InputFile
     lateinit var sourceFile: File

     @OutputFile
     lateinit var destinationFile: File




     @TaskAction
     fun copyFiles() {
         val source = java.io.FileInputStream(sourceFile)
         val destination = java.io.FileOutputStream(destinationFile)
         source.copyTo(destination)
         source.close()
         destination.close()
         println("copy ${sourceFile.absolutePath} to ${sourceFile.absolutePath}")
     }

 */

    @TaskAction
    fun execute() {

        if (sourceDir != null) {
            if (move) {
                print("准备移动 $sourceDir 到 $destinationDir 下")

            } else {
                print("准备复制 $sourceDir 到 $destinationDir 下")

            }
            if (!sourceDir!!.exists()) {
                println("忽略操作 ${sourceDir} 不存在")
            }
            val source = sourceDir!!
            val destination = destinationDir!!

            if (!source.isDirectory) {
                project.logger.error("Source directory does not exist. or is  not directory ${sourceDir}")
                println(" 文件不存在 ${source} ")
                return;
            }
            if (!destination.exists()) {
                destination.mkdirs()
                println(" ${destination} 不存在 进行创建")
            }
            println(" 需要复制文件总数 ${source.listFiles().size} ")


            source.listFiles().forEach { file ->
                val targetPath = File(destination, file.relativeTo(source).path)
                if (file.isFile) {
                    if (move) {
                        println("移动文件${file} 到 ${targetPath}")
                        file.renameTo(targetPath)
                    } else {

                        file.copyTo(targetPath, overwrite = true)
                        println("复制文件${file} 到 ${targetPath}")
                    }
                } else if (file.isDirectory) {
                    // 递归处理子目录
                    targetPath.mkdirs()
                    executeRecursive(file, targetPath)
                }
            }

        } else {
            println("custom dir task execute")
        }
    }

    private fun executeRecursive(srcDir: File, destDir: File) {
        srcDir.listFiles()?.forEach { file ->
            val targetPath = File(destDir, file.name)
            if (file.isFile) {
                file.copyTo(targetPath, overwrite = true)
            } else if (file.isDirectory) {
                targetPath.mkdirs()
                executeRecursive(file, targetPath)
            }
        }
    }

    override fun doLast(action: Closure<*>): Task {
        return super.doLast(action)
    }
}
subprojects {
    afterEvaluate {
        System.err.println("library-need-encrypt-str" + project.name);
        plugins.withId("com.android.application") {
            System.err.println(
                "app字符串加密???" + project.name + "," + extensions.findByType(
                    com.android.build.gradle.AppExtension::class
                )
            );
            extensions.findByType(com.android.build.gradle.AppExtension::class)?.run {


// 在构建脚本中注册并配置自定义任务

                applicationVariants.all(Action {
                    val activeFlavor = this.name.capitalized()//包含release
                    val flavorName = this.flavorName
                    val customTaskInstance =

                        tasks.register<CustomOutDIrTask>("BuildMy${activeFlavor}Apk") {
                            group = "LOZN${project.name}"
                            this.outputs.upToDateWhen { false }
                            description = "打包APK 渠道 ${flavorName} " + applicationVariants
                            this.outputs.upToDateWhen { false } // 强制copyApkTask每次都运行
                            flavor = activeFlavor
                            move = true
                            sourceDir = File(
                                "${project.projectDir}/build/outputs/apk/${flavorName}/${buildType.name}"
                            )
                            /*
                                                        sourceDir = File(
                                                            "${project.projectDir}/${flavorName}/${buildType.name}"
                                                        )
                            */
                            destinationDir =
                                File(
                                    "${project.projectDir.parentFile.absolutePath}",
                                    "apks/${buildType.name}"
                                )
                        }

               // customTaskInstance.dependsOn("create${activeFlavor}ApkListingFileRedirect")

                 customTaskInstance.dependsOn("assemble${this.name.capitalized()}")
                  /*  val assembleApkTask = "assemble${this.name.capitalized()}"
                    // 检查或创建对应的任务（如果它确实存在）
                    val task = project.tasks.findByName("assemble${this.name.capitalized()}")
                    if (task != null) {
                        val asssembleTaskObj = project.tasks.findByName(assembleApkTask)
                        asssembleTaskObj!!.mustRunAfter(customTaskInstance)
                    }
                    */


                    if (!this.buildType.isDebuggable) {
//                            val myBuildTime = SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
                        val outputFileName =
                            "${flavorName}-scmes-${this.versionName}_${this.versionCode}.apk"
//                                "${productFlavors.first().name}channel_sotrun-${this.versionName}_${this.versionCode}-${myBuildTime}.apk"
                        outputs.all {
                            val output =
                                this as? com.android.build.gradle.internal.api.BaseVariantOutputImpl
                            output?.outputFileName = outputFileName
                        }
                    }
                    // 当前激活的 flavor 名称
                    //dependsOn("assemble${variant.name}")
                    /*     var floavorName = this.flavorName
                         val myBuildTime =
                             java.text.SimpleDateFormat("yyyyMMddHHmm")
                                 .format(System.currentTimeMillis())
                         if (this.buildType.name == "release") {

                             *//*   this.packageApplicationProvider.configure {
                               this.outputDirectory.set(rootProject.file("apks/${floavorName}"))
                           }*//*

                        val apkListingFileRedirectTaskName =
                            "create${this.name.capitalized()}ApkListingFileRedirect"
                        val assembleApkTask = "assemble${this.name.capitalized()}"
                        // 检查或创建对应的任务（如果它确实存在）
                        val task = project.tasks.findByName(apkListingFileRedirectTaskName)
                        val asssembleTaskObj = project.tasks.findByName(assembleApkTask)
                        customTaskInstance.dependsOn(assembleApkTask)
                        if (task != null && asssembleTaskObj != null) {
                            task.mustRunAfter(asssembleTaskObj)
                            //task!!.dependsOn(mycopyTask)
                            // 任务存在，则可以添加依赖或进行其他操作
                            // task.doSomething() 或者 variant.variantData.taskContainer.add(task)
                            // 注意：在Kotlin DSL中，直接将任务添加到构建变体的依赖关系可能需要更具体的API调用
                            task.outputs.upToDateWhen { false } // 强制此任务每次构建都运行
                            asssembleTaskObj.outputs.upToDateWhen { false } // 强制此任务每次构建都运行
                            task.doFirst() {
                                println("      dofisrst执行了 --------------${apkListingFileRedirectTaskName}")
                                // 修改生成 apk 的位置
                                val outputDirectory = File(rootProject.projectDir, "apk/")

                                var apkListingFileRedirectTask1: com.android.build.gradle.internal.tasks.ListingFileRedirectTask =
                                    task.cast<com.android.build.gradle.internal.tasks.ListingFileRedirectTask>()
                                val listingFile =
                                    apkListingFileRedirectTask1.listingFile.asFile.get()
                                println(apkListingFileRedirectTask1.listingFile)
                                // apk 原始输出位置

                                println("------------ 开始拷贝 ${listingFile} 到 ${rootProject.projectDir} 下 ----------")

                                // 将打包生成的 apk 复制到 原有的构建目录
                                copy {
                                    from(outputDirectory)
                                    into(rootProject.projectDir)
                                }
                            }
                        } else {
                            println("任务 '$apkListingFileRedirectTaskName'  $assembleApkTask 在当前构建变体下未找到.")
                        }
                    }*/
                })

            }


        }
    }
}
