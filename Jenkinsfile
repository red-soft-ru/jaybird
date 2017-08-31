@Library('jenkins_pipeline_utils') 
 
import ru.redsoft.jenkins.Pipeline; 
import ru.redsoft.jenkins.Git; 
import ru.redsoft.jenkins.ReleaseHub;  

String release_hub_project = 'jaybird2'
String maven_group = 'ru.red-soft.jdbc'
String rev
String version
String vcs_url
String archive_prefix
String version_tag = ''
String branch = env.BRANCH_NAME

try
{

node('master')
{
    stage('Prepare')
    {
        deleteDir()
        def wd = pwd()      

        checkout scm

        rev = Git.getGitRevision(wd)

        def matcher = (new File(wd + '/build/init.xml').text =~ /(?sm).*version\.major" value="(?<major>\d+)".*version\.minor" value="(?<minor>\d+).*version\.revision" value="(?<revision>\d+).*/)
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
        archive_prefix="jaybird-${version}"
    }
    
    stage ('Source dist')
    {
        sh 'rm -rf dist-src && mkdir dist-src'
        sh "git archive --prefix=${archive_prefix}/ -o dist-src/${archive_prefix}.tar.gz HEAD"
        sh "git archive --prefix=${archive_prefix}/ -o dist-src/${archive_prefix}.zip HEAD"
        stash includes: 'dist-src/**', name: 'src'
    }
}

def buildTasks = [:]
for (j in ['16', '17', '18'])
{
    def jdk = j
    buildTasks["build-${jdk}"] = { build(jdk, archive_prefix, version_tag) }
}
buildTasks.failFast = true
parallel buildTasks

test('18', archive_prefix, version)

node('master')
{
    stage ('Deploy')
    {
        deleteDir()
        def wd = pwd()
        
        unstash 'src'
        for (jdk in ['16', '17', '18'])
        {
            unstash "bin-${jdk}"
            unstash "esp-${jdk}"
            unstash "javadoc-${jdk}"
            unstash "sources-${jdk}"
            unstash "test-${jdk}"
        }

        sh "echo artifact jaybird-src ${version} > artifacts"
        sh "echo file dist-src/${archive_prefix}.tar.gz tar.gz src >> artifacts"
        sh "echo file dist-src/${archive_prefix}.zip zip src >> artifacts"
        sh "echo end >> artifacts"
        for (jdk in ['16', '17', '18'])
        {
            sh "echo artifact jaybird-jdk${jdk} ${version} >> artifacts"
            sh "echo file dist-${jdk}/bin/jaybird-jdk${jdk}-${version}.jar jar >> artifacts"
            sh "echo file dist-${jdk}/bin/jaybird-full-jdk${jdk}-${version}.jar jar full >> artifacts"
            sh "echo file dist-${jdk}/bin/jaybird-cryptoapi-jdk${jdk}-${version}.jar jar cryptoapi >> artifacts"
            sh "echo file dist-${jdk}/bin/jaybird-cryptoapi-security-jdk${jdk}-${version}.jar jar cryptoapi-security >> artifacts"
            sh "echo file dist-${jdk}/esp/jaybird-esp-jdk${jdk}-${version}.jar jar esp >> artifacts"
            sh "echo file dist-${jdk}/test/jaybird-test-jdk${jdk}-${version}.jar jar test >> artifacts"
            sh "echo file dist-${jdk}/sources/jaybird-jdk${jdk}-${version}-sources.jar jar sources >> artifacts"
            sh "echo file dist-${jdk}/javadoc/jaybird-jdk${jdk}-${version}-javadoc.jar jar javadoc >> artifacts"
            sh "echo end >> artifacts"
        }
        
        ReleaseHub.deployToReleaseHub(release_hub_project, version, env.BUILD_URL, rev, wd+'/artifacts', wd, maven_group, '', '', branch) 

        Pipeline.defaultSuccessActions(currentBuild)
    }
}

} // try
catch (any)
{
    utils.defaultFailureActions(any)
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
        stage('Build JDK' + jdk)
        {        
            deleteDir()
            unstash 'src'
            
            String java_home
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
            else
            {
                println("Unknown jdk version ${jdk}")
            }
            println("JDK${jdk}. JAVA_HOME=${java_home}")
        
            sh "tar xf dist-src/${archive_prefix}.tar.gz"
            withEnv(["JAVA_HOME=${java_home}", "archive_prefix=${archive_prefix}", "version_tag=${version_tag}", "jdk=${jdk}"]) {
                sh """#!/bin/bash
                    pushd ${archive_prefix}
                    ./build.sh -Dversion.tag.maven=.${version_tag} jars
                    popd
                    mkdir -p dist-${jdk}/{bin,esp,sources,javadoc,test}
                    mv ${archive_prefix}/output/lib/jaybird-*javadoc* dist-${jdk}/javadoc
                    mv ${archive_prefix}/output/lib/jaybird-*sources* dist-${jdk}/sources
                    mv ${archive_prefix}/output/lib/jaybird-*test* dist-${jdk}/test
                    mv ${archive_prefix}/output/lib/jaybird-*esp* dist-${jdk}/esp
                    mv ${archive_prefix}/output/lib/* dist-${jdk}/bin
                """
            }
            
            stash includes: "dist-${jdk}/bin/**", name: "bin-${jdk}"
            stash includes: "dist-${jdk}/esp/**", name: "esp-${jdk}"
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
        stage ('Test on JDK' + jdk)
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
            step([$class: "JUnitResultArchiver", testResults: "results/TEST-*.xml"])
        }   
    }
}
