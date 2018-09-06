/*
 * MIT License
 *
 * Copyright (c) 2017 Choko (choko@curioswitch.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.curioswitch.gradle.plugins.gcloud;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.curioswitch.gradle.plugins.curioserver.CurioServerPlugin;
import org.curioswitch.gradle.plugins.curioserver.DeploymentExtension;
import org.curioswitch.gradle.plugins.gcloud.tasks.DownloadHelmTask;
import org.curioswitch.gradle.plugins.gcloud.tasks.DownloadTerraformTask;
import org.curioswitch.gradle.plugins.gcloud.tasks.GcloudTask;
import org.curioswitch.gradle.plugins.gcloud.tasks.RequestNamespaceCertTask;
import org.curioswitch.gradle.plugins.gcloud.tasks.SetupTask;
import org.curioswitch.gradle.plugins.gcloud.util.PlatformHelper;
import org.curioswitch.gradle.plugins.helm.TillerExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Rule;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A plugin that adds tasks for automatically downloading the gcloud sdk and running commands using
 * it from gradle. Python 2 will have to be available for gcloud sdk commands to work. Eventually,
 * most commands should be migrated to using the gcloud Rest APIs to remove this dependency.
 */
public class GcloudPlugin implements Plugin<Project> {

  private static final Logger logger = LoggerFactory.getLogger(GcloudPlugin.class);

  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper(
              new YAMLFactory().enable(Feature.MINIMIZE_QUOTES).disable(Feature.SPLIT_LINES))
          .registerModule(new GuavaModule())
          .setSerializationInclusion(Include.NON_EMPTY);

  @Override
  public void apply(Project project) {
    project.getExtensions().create(ImmutableGcloudExtension.NAME, GcloudExtension.class, project);

    ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
    ext.set(GcloudTask.class.getSimpleName(), GcloudTask.class);
    ext.set(RequestNamespaceCertTask.class.getSimpleName(), RequestNamespaceCertTask.class);

    project
        .getTasks()
        .addRule(
            new Rule() {
              @Override
              public String getDescription() {
                return "Pattern: \"gcloud_<command>\": Executes a Gcloud command.";
              }

              @Override
              public void apply(String taskName) {
                if (taskName.startsWith("gcloud_")) {
                  GcloudTask task = project.getTasks().create(taskName, GcloudTask.class);
                  List<String> tokens = ImmutableList.copyOf(taskName.split("_"));
                  task.setArgs(tokens.subList(1, tokens.size()));
                }
              }
            });

    project
        .getTasks()
        .create(DownloadTerraformTask.NAME, DownloadTerraformTask.class, new PlatformHelper());

    TillerExtension.createAndAdd(project);
    project.getTasks().create(DownloadHelmTask.NAME, DownloadHelmTask.class, new PlatformHelper());

    var gcloudSetup = project.getTasks().register("gcloudSetup");
    var gcloudLoginToCluster =
        project.getTasks().register("gcloudLoginToCluster", GcloudTask.class);

    project.afterEvaluate(
        p -> {
          ImmutableGcloudExtension config =
              project.getExtensions().getByType(GcloudExtension.class);
          var downloadSdkTask =
              project
                  .getTasks()
                  .register(SetupTask.NAME, SetupTask.class, t -> t.setEnabled(config.download()));

          project.allprojects(
              proj ->
                  proj.getPlugins()
                      .withType(
                          CurioServerPlugin.class,
                          unused -> {
                            DeploymentExtension deployment =
                                proj.getExtensions().getByType(DeploymentExtension.class);
                            deployment.setImagePrefix(
                                config.containerRegistry() + "/" + config.clusterProject() + "/");
                          }));

          project.allprojects(
              proj ->
                  proj.getPlugins()
                      .withType(
                          CurioDatabasePlugin.class,
                          unused -> {
                            DatabaseExtension database =
                                proj.getExtensions().getByType(DatabaseExtension.class);
                            database.setDevDockerImagePrefix(
                                config.containerRegistry() + "/" + config.clusterProject() + "/");
                          }));

          gcloudLoginToCluster.configure(
              t ->
                  t.setArgs(
                      ImmutableList.of(
                          "container",
                          "clusters",
                          "get-credentials",
                          config.clusterName(),
                          System.getenv("CLOUDSDK_COMPUTE_ZONE") != null
                              ? "--zone=" + System.getenv("CLOUDSDK_COMPUTE_ZONE")
                              : "--region=" + config.clusterRegion())));

          var installComponents =
              project
                  .getTasks()
                  .register(
                      "gcloudInstallComponents",
                      GcloudTask.class,
                      t -> {
                        t.setArgs(
                            ImmutableList.of(
                                "components",
                                "install",
                                "app-engine-python",
                                "beta",
                                "kubectl",
                                "docker-credential-gcr"));
                        t.dependsOn(downloadSdkTask);
                      });
          gcloudSetup.configure(t -> t.dependsOn(downloadSdkTask, installComponents));
        });

    addGenerateCloudBuildTask(project);
  }

  private static void addGenerateCloudBuildTask(Project rootProject) {
    rootProject
        .getTasks()
        .register(
            "gcloudGenerateCloudBuild",
            task ->
                task.doLast(
                    t -> {
                      ImmutableGcloudExtension config =
                          rootProject.getExtensions().getByType(GcloudExtension.class);

                      File existingCloudbuildFile = rootProject.file("cloudbuild.yaml");
                      final CloudBuild existingCloudBuild;
                      try {
                        existingCloudBuild =
                            !existingCloudbuildFile.exists()
                                ? null
                                : OBJECT_MAPPER.readValue(existingCloudbuildFile, CloudBuild.class);
                      } catch (IOException e) {
                        throw new UncheckedIOException(
                            "Could not parse existing cloudbuild file.", e);
                      }

                      String deepenGitRepoId = "curio-generated-deepen-git-repo";
                      String fetchUncompressedCacheId =
                          "curio-generated-fetch-uncompressed-build-cache";
                      String fetchCompressedCacheId =
                          "curio-generated-fetch-compressed-build-cache";

                      String buildAllImageId = "curio-generated-build-all";

                      var fetchUncompressedCacheStep =
                          ImmutableCloudBuildStep.builder()
                              .id(fetchUncompressedCacheId)
                              .addWaitFor("-")
                              .name("gcr.io/cloud-builders/gsutil")
                              .entrypoint("bash")
                              .addArgs(
                                  "-c",
                                  "gsutil cp gs://"
                                      + config.buildCacheStorageBucket()
                                      + "/cloudbuild-cache-uncompressed.tar .gradle/cloudbuild-cache-uncompressed.tar || echo Could not fetch uncompressed build cache...")
                              .build();
                      var fetchCompressedCacheStep =
                          ImmutableCloudBuildStep.builder()
                              .id(fetchCompressedCacheId)
                              .addWaitFor("-")
                              .name("gcr.io/cloud-builders/gsutil")
                              .entrypoint("bash")
                              .addArgs(
                                  "-c",
                                  "gsutil cp gs://"
                                      + config.buildCacheStorageBucket()
                                      + "/cloudbuild-cache-compressed.tar.gz .gradle/cloudbuild-cache-compressed.tar.gz || echo Could not fetch compressed build cache...")
                              .build();

                      List<CloudBuildStep> steps = new ArrayList<>();
                      steps.add(
                          ImmutableCloudBuildStep.builder()
                              .id(deepenGitRepoId)
                              .addWaitFor("-")
                              .name("gcr.io/cloud-builders/git")
                              .args(ImmutableList.of("fetch", "origin", "master", "--depth=10"))
                              .build());
                      steps.add(fetchUncompressedCacheStep);
                      steps.add(fetchCompressedCacheStep);
                      steps.add(
                          ImmutableCloudBuildStep.builder()
                              .id(buildAllImageId)
                              .addWaitFor(
                                  deepenGitRepoId, fetchUncompressedCacheId, fetchCompressedCacheId)
                              .name("openjdk:10-jdk-slim")
                              .entrypoint("bash")
                              .addArgs(
                                  "-c",
                                  "(test -e .gradle/cloudbuild-cache-uncompressed.tar && tar -xpPf .gradle/cloudbuild-cache-uncompressed.tar && tar -xpPf .gradle/cloudbuild-cache-compressed.tar.gz || echo No build cache yet.) && ./gradlew continuousBuild --stacktrace --no-daemon -Pcuriostack.revisionId=$REVISION_ID && tar -cpPf .gradle/cloudbuild-cache-uncompressed.tar /root/.gradle/wrapper /root/.gradle/caches /root/.gradle/curiostack && tar -cpPzf .gradle/cloudbuild-cache-compressed.tar.gz /usr/local/share/.cache")
                              .env(
                                  ImmutableList.of(
                                      "CI=true",
                                      "CI_MASTER=true",
                                      "CLOUDSDK_COMPUTE_ZONE=" + config.clusterRegion()))
                              .build());
                      steps.add(
                          ImmutableCloudBuildStep.builder()
                              .id("curio-generated-push-uncompressed-build-cache")
                              .addWaitFor(buildAllImageId)
                              .name("gcr.io/cloud-builders/gsutil")
                              .addArgs(
                                  "-o",
                                  "GSUtil:parallel_composite_upload_threshold=150M",
                                  "cp",
                                  ".gradle/cloudbuild-cache-uncompressed.tar",
                                  "gs://"
                                      + config.buildCacheStorageBucket()
                                      + "/cloudbuild-cache-uncompressed.tar")
                              .build());
                      steps.add(
                          ImmutableCloudBuildStep.builder()
                              .id("curio-generated-push-compressed-build-cache")
                              .addWaitFor(buildAllImageId)
                              .name("gcr.io/cloud-builders/gsutil")
                              .addArgs(
                                  "-o",
                                  "GSUtil:parallel_composite_upload_threshold=150M",
                                  "cp",
                                  ".gradle/cloudbuild-cache-compressed.tar.gz",
                                  "gs://"
                                      + config.buildCacheStorageBucket()
                                      + "/cloudbuild-cache-compressed.tar.gz")
                              .build());

                      ImmutableCloudBuild.Builder cloudBuildConfig =
                          ImmutableCloudBuild.builder().addAllSteps(steps);

                      if (existingCloudBuild != null) {
                        CloudBuild existingWithoutGenerated =
                            ImmutableCloudBuild.builder()
                                .from(existingCloudBuild)
                                .steps(
                                    existingCloudBuild
                                            .steps()
                                            .stream()
                                            .filter(
                                                step -> !step.id().startsWith("curio-generated-"))
                                        ::iterator)
                                .images(existingCloudBuild.images())
                                .build();
                        cloudBuildConfig.from(existingWithoutGenerated);
                      }

                      try {
                        OBJECT_MAPPER.writeValue(
                            rootProject.file("cloudbuild.yaml"), cloudBuildConfig.build());
                      } catch (IOException e) {
                        throw new UncheckedIOException(e);
                      }

                      CloudBuild releaseCloudBuild =
                          ImmutableCloudBuild.builder()
                              .addSteps(
                                  fetchUncompressedCacheStep,
                                  fetchCompressedCacheStep,
                                  ImmutableCloudBuildStep.builder()
                                      .id("curio-generated-build-releases")
                                      .addWaitFor(fetchUncompressedCacheId, fetchCompressedCacheId)
                                      .name("openjdk:10-jdk-slim")
                                      .entrypoint("bash")
                                      .addArgs(
                                          "-c",
                                          "(test -e .gradle/cloudbuild-cache-uncompressed.tar && tar -xpPf .gradle/cloudbuild-cache-uncompressed.tar && tar -xpPf .gradle/cloudbuild-cache-compressed.tar.gz || echo No build cache yet.) && ./gradlew releaseBuild --stacktrace --no-daemon")
                                      .env(
                                          ImmutableList.of(
                                              "CI=true",
                                              "TAG_NAME=$TAG_NAME",
                                              "BRANCH_NAME=$BRANCH_NAME"))
                                      .build())
                              .build();
                      try {
                        OBJECT_MAPPER.writeValue(
                            rootProject.file("cloudbuild-release.yaml"), releaseCloudBuild);
                      } catch (IOException e) {
                        throw new UncheckedIOException(e);
                      }
                    }));
  }

  @Immutable
  @Style(
      visibility = ImplementationVisibility.PACKAGE,
      builderVisibility = BuilderVisibility.PACKAGE,
      defaultAsDefault = true)
  @JsonDeserialize(as = ImmutableCloudBuildStep.class)
  @JsonSerialize(as = ImmutableCloudBuildStep.class)
  interface CloudBuildStep {

    String id();

    default List<String> waitFor() {
      return ImmutableList.of();
    }

    String name();

    @Nullable
    default String entrypoint() {
      return null;
    }

    List<String> args();

    default List<String> env() {
      return ImmutableList.of("CI=true");
    };
  }

  @Immutable
  @Style(
      visibility = ImplementationVisibility.PACKAGE,
      builderVisibility = BuilderVisibility.PACKAGE,
      defaultAsDefault = true)
  @JsonDeserialize(as = ImmutableCloudBuild.class)
  @JsonSerialize(as = ImmutableCloudBuild.class)
  interface CloudBuild {
    List<CloudBuildStep> steps();

    List<String> images();

    @Nullable
    default String timeout() {
      return null;
    }

    Map<String, String> options();
  }
}
