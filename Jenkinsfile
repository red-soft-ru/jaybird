import java.text.SimpleDateFormat 

String release_hub_project = 'jaybird3'
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
    stage 'Prepare'
    def wd = pwd()

    utils = fileLoader.fromGit('utils', 'http://git.red-soft.biz/utils/jenkins_pipeline_utils.git', 'master', null, '')

    checkout scm

    rev = utils.getGitRevision(wd)

    def matcher = (new File(wd + '/build.properties').text =~ /(?sm).*version\.major=(?<major>\d+).*version\.minor=(?<minor>\d+).*version\.revision=(?<revision>\d+).*/)
    if (!matcher.matches())
    {
        throw new Exception("Unable obtain version")
    }
    version_major = matcher.group('major')
    version_minor = matcher.group('minor')
    version_revision = matcher.group('revision')
    version = version_major + '.' + version_minor + '.' + version_revision
    version_tag = utils.getBuildNo(release_hub_project, version)
    version += "." + version_tag
    matcher = null    
    
    vcs_url = "http://git.red-soft.biz/red-database/jaybird/commit/" + rev
    
    stage 'Source dist'

    archive_prefix="jaybird-${version}"
    sh 'rm -rf dist-src && mkdir dist-src'
    sh "git archive --prefix=${archive_prefix}/ -o dist-src/${archive_prefix}.tar.gz HEAD"
    sh "git archive --prefix=${archive_prefix}/ -o dist-src/${archive_prefix}.zip HEAD"
    stash includes: 'dist-src/**', name: 'src'
}

for (j in ['17', '18'])
{
    jdk = j
    build(jdk, archive_prefix, version_tag)
}

node('master')
{
    stage 'Deploy'
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

    sh "echo artifact jaybird-src ${version} > artifacts"
    sh "echo file dist-src/${archive_prefix}.tar.gz tar.gz src >> artifacts"
    sh "echo file dist-src/${archive_prefix}.zip zip src >> artifacts"
    sh "echo end >> artifacts"
    for (jdk in ['17', '18'])
    {
        sh "echo artifact jaybird-jdk${jdk} ${version} >> artifacts"
        sh "echo file dist-${jdk}/bin/jaybird-${version}.jar jar >> artifacts"
        sh "echo file dist-${jdk}/bin/jaybird-full-${version}.jar jar full >> artifacts"
        sh "echo file dist-${jdk}/test/jaybird-test-${version}.jar jar test >> artifacts"
        sh "echo file dist-${jdk}/sources/jaybird-${version}-sources.jar jar sources >> artifacts"
        sh "echo file dist-${jdk}/javadoc/jaybird-${version}-javadoc.jar jar javadoc >> artifacts"
        sh "echo end >> artifacts"
    }
    
    utils.deployAndRegister(release_hub_project, version, wd+'/artifacts', env.BUILD_URL, vcs_url, maven_group, wd, '', '', branch)

    utils.defaultSuccessActions()
}

} // try
catch (any)
{
    utils.defaultFailureActions(any)
}
finally
{
    mail(to: utils.defaultEmailAddresses(),
         subject: utils.defaultEmailSubject(version, rev),
         body: utils.defaultEmailBody(vcs_url, release_hub_project, version));
}

def build(String jdk, archive_prefix, version_tag)
{
    node('jdk' + jdk + '&&builder&&linux')
    {
        stage 'Build JDK' + jdk
        
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
        
        if (version_tag)
        	version_tag = "-Dversion.tag=.${version_tag}"

        sh "tar xf dist-src/${archive_prefix}.tar.gz"
        withEnv(["JAVA_HOME=${java_home}", "archive_prefix=${archive_prefix}", "version_tag=${version_tag}", "jdk=${jdk}"]) {
            sh """#!/bin/bash
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