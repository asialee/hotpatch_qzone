package com.hotpatch.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class Register implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def android = project.extensions.findByType(AppExtension);
        project.logger.error("自定义插件成功");
        android.registerTransform(new PreDexTransform(project));
    }
}