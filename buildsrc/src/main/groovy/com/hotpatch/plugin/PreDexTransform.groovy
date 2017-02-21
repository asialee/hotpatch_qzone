package com.hotpatch.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

public class PreDexTransform extends Transform {

    Project project;

    public PreDexTransform(Project project1) {
        this.project = project1;

        def libPath = project.project(":hack").buildDir.absolutePath.concat("/intermediates/classes/debug")
        println libPath
        Inject.appendClassPath(libPath)
        Inject.appendClassPath("/Users/liyazhou/Library/Android/sdk/platforms/android-24/android.jar")
    }
    @Override
    String getName() {
        return "preDex"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {

        // 遍历transfrom的inputs
        // inputs有两种类型，一种是目录，一种是jar，需要分别遍历。
        inputs.each {TransformInput input ->
            input.directoryInputs.each {DirectoryInput directoryInput->

                //TODO 注入代码
                Inject.injectDir(directoryInput.file.absolutePath)

                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each {JarInput jarInput->


                //TODO 注入代码
                String jarPath = jarInput.file.absolutePath;
                String projectName = project.rootProject.name;
                if(jarPath.endsWith("classes.jar")
                        && jarPath.contains("exploded-aar/"+projectName)
                        // hotpatch module是用来加载dex，无需注入代码
                        && !jarPath.contains("exploded-aar/"+projectName+"/hotpatch")) {
                    Inject.injectJar(jarPath)
                }

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if(jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0,jarName.length()-4)
                }
                def dest = outputProvider.getContentLocation(jarName+md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }
}