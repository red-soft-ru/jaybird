@Library('jenkins_pipeline_utils') 
 
import ru.redsoft.jenkins.Pipeline; 
import ru.redsoft.jenkins.Git; 
import ru.redsoft.jenkins.ReleaseHub;  

String release_hub_project = 'jaybird'
String maven_group = 'ru.red-soft.jdbc'
String rev
String version
String vcs_url
String archive_prefix
String version_tag = ''
String branch = env.BRANCH_NAME

properties([
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '90', numToKeepStr: ''))
])

try
{

node('master')
{
    def wd = pwd()
    stage('Prepare')
    {
        deleteDir()
        checkout scm

        rev = Git.getGitRevision(wd)

        def matcher = (new File(wd + '/build.properties').text =~ /(?sm).*version\.major=(?<major>\d+).*version\.minor=(?<minor>\d+).*version\.revision=(?<revision>\d+).*/)
        if (!matcher.matches())
        {
            throw new Exception("Unable obtain version")
        }
        version_major = matcher.group('major')
        version_minor = matcher.group('minor')
        version_revision = matcher.group('revision')
        version = version_major + '.' + version_minor + '.' + version_revision
        version_tag = ReleaseHub.getBuildNo(release_hub_project, version)
        version += "." + version_tag
        matcher = null    
        
        vcs_url = "http://git.red-soft.biz/red-database/jaybird/commit/" + rev
    }
    
    stage('Source dist')
    {
        archive_prefix="jaybird-${version}"
        sh 'rm -rf dist-src && mkdir dist-src'
        sh "git archive --prefix=${archive_prefix}/ -o dist-src/${archive_prefix}.tar.gz HEAD"
        sh "git archive --prefix=${archive_prefix}/ -o dist-src/${archive_prefix}.zip HEAD"
        stash includes: 'dist-src/**', name: 'src'
    }
}

def buildTasks = [:]
buildTasks['build-jdk17'] = { build('17', archive_prefix, version_tag) }
buildTasks['build-jdk18'] = { build('18', archive_prefix, version_tag) }
parallel buildTasks

test('18', archive_prefix, version)

node('master')
{
    stage('Deploy')
    {
        deleteDir()
        def wd = pwd()
        
        unstash 'src'
        for (jdk in ['17', '18'])
        {
            unstash "bin-${jdk}"
            unstash "javadoc-${jdk}"
            unstash "sources-${jdk}"
            unstash "test-${jdk}"
        }
        unstash "results-jdk18"

        withEnv(["archive_prefix=${archive_prefix}", "version=${version}"]) {
            sh """#!/bin/bash
                set -e
                tar xf dist-src/${archive_prefix}.tar.gz
                m4 -DVERSION=$version ${archive_prefix}/ci/artifacts.m4 > artifacts
            """
        }

        ReleaseHub.deployToReleaseHub(release_hub_project, version, env.BUILD_URL, rev, wd+'/artifacts', wd, maven_group, wd + '/results', '', branch)

        Pipeline.defaultSuccessActions(currentBuild)
    }
}

} // try
catch (any)
{
    Pipeline.defaultFailureActions(currentBuild, any)
}
finally
{
     mail(to: Pipeline.defaultEmailAddresses(), 
         subject: Pipeline.defaultEmailSubject(currentBuild, version, rev), 
         body: Pipeline.defaultEmailBody(currentBuild, vcs_url, release_hub_project, version));
}

def build(String jdk, archive_prefix, version_tag)
{
    node('jdk' + jdk + '&&builder&&linux')
    {
        stage('Build on JDK' + jdk)
        {
            deleteDir()
            unstash 'src'
            
            if (jdk == '17')
            {
                java_home = env.JAVA_HOME_1_7
            }
            else if (jdk == '18')
            {
                java_home = env.JAVA_HOME_1_8
            }
            else
            {
                throw new Exception("Unsupported JDK version " + jdk)
            }

            if (version_tag)
            	version_tag = "-Dversion.tag=.${version_tag}"

            sh "tar xf dist-src/${archive_prefix}.tar.gz"
            withEnv(["JAVA_HOME=${java_home}", "archive_prefix=${archive_prefix}", "version_tag=${version_tag}", "jdk=${jdk}"]) {
                sh """#!/bin/bash
                    set -e
                    pushd ${archive_prefix}
                    ./build.sh ${version_tag} jars
                    popd
                    mkdir -p dist-${jdk}/{bin,sources,javadoc,test}
                    mv ${archive_prefix}/output/lib/jaybird-*javadoc* dist-${jdk}/javadoc
                    mv ${archive_prefix}/output/lib/jaybird-*sources* dist-${jdk}/sources
                    mv ${archive_prefix}/output/lib/jaybird-*test* dist-${jdk}/test
                    mv ${archive_prefix}/output/lib/* dist-${jdk}/bin
                """
            }
            
            stash includes: "dist-${jdk}/bin/**", name: "bin-${jdk}"
            stash includes: "dist-${jdk}/javadoc/**", name: "javadoc-${jdk}"
            stash includes: "dist-${jdk}/sources/**", name: "sources-${jdk}"
            stash includes: "dist-${jdk}/test/**", name: "test-${jdk}"
        }
    }    
}

def test(jdk, archive_prefix, version)
{
    node('jdk' + jdk + '&&tester&&linux')
    {
        stage('Test on JDK' + jdk)
        {
            deleteDir()
            def wd = pwd()
            unstash "bin-${jdk}"
            unstash "test-${jdk}"
            unstash 'src'
            
            if (jdk == '16')
            {
                java_home = env.JAVA_HOME_1_6
            }
            else if (jdk == '17')
            {
                java_home = env.JAVA_HOME_1_7
            }
            else if (jdk == '18')
            {
                java_home = env.JAVA_HOME_1_8
            }
            
            sh "tar xf dist-src/${archive_prefix}.tar.gz"

            withEnv(["JAVA_HOME=${java_home}", "archive_prefix=${archive_prefix}", "JDK_VERSION=${jdk}", "BINDIR=${wd}/dist-${jdk}", "SRCDIR=${wd}/${archive_prefix}", "JAYBIRD_VERSION=${version}", "WORKSPACE=${wd}"]) {
                sh """#!/bin/bash
                    cd ${archive_prefix}/ci
                    ./test.sh
                """
            }
            stash includes: "results/jdk${jdk}/TEST-*.xml", name: "results-jdk${jdk}"
        }   
    }
}
