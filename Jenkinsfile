import java.text.SimpleDateFormat 

String release_hub_project = 'jaybird2'
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

    //checkout scm
    git url: 'http://git.red-soft.biz/red-database/jaybird.git', branch: 'branch_2_2'

    rev = utils.getGitRevision(wd)
    
    def sout = new StringBuilder()
    def serr = new StringBuilder()
    def proc = 'git show-ref --tags -d'.execute(null, new File(wd))
    proc.waitForProcessOutput(sout, serr)
    
    if (proc.exitValue() != 0)
    {
        println(serr)
        throw new Exception("Unable obtain tags")
    }        
    proc = null
        
    def matcher = (sout =~ /(?sm).*${rev} refs\/tags\/v(?<version>\d+\.\d+(\.\d+)?).*/)
    if (matcher.matches())
    {
        version = matcher.group('version')
    }
    else
    {
        matcher = (new File(wd + '/build/init.xml').text =~ /(?sm).*version\.major" value="(?<major>\d+)".*version\.minor" value="(?<minor>\d+).*version\.revision" value="(?<revision>\d+).*/)
        if (!matcher.matches())
        {
            throw new Exception("Unable obtain version")
        }
        version_major = matcher.group('major')
        version_minor = matcher.group('minor')
        version_revision = matcher.group('revision')
        version = version_major + '.' + version_minor + '.' + version_revision
        version_tag = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        version += "-" + version_tag
    }
    matcher = null    
    
    vcs_url = "http://git.red-soft.biz/red-database/jaybird/commit/" + rev
    
    stage 'Source dist'

    archive_prefix="jaybird-${version}"
    sh 'rm -rf dist-src && mkdir dist-src'
    sh "git archive --prefix=${archive_prefix}/ -o dist-src/${archive_prefix}.tar.gz HEAD"
    sh "git archive --prefix=${archive_prefix}/ -o dist-src/${archive_prefix}.zip HEAD"
    stash includes: 'dist-src/**', name: 'src'
}

for (j in ['16', '17', '18'])
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
    for (jdk in ['16', '17', '18'])
    {
        unstash "bin-${jdk}"
        unstash "esp-${jdk}"
        unstash "javadoc-${jdk}"
        unstash "sources-${jdk}"
        unstash "test-${jdk}"
    }

    sh 'find'

    sh "echo artifact jaybird ${version} > artifacts"
    sh "echo file dist-src/${archive_prefix}.tar.gz tar.gz src >> artifacts"
    sh "echo file dist-src/${archive_prefix}.zip zip src >> artifacts"
    for (jdk in ['16', '17', '18'])
    {
        sh "echo file dist-${jdk}/bin/jaybird-jdk${jdk}-${version}.jar jar jdk${jdk} >> artifacts"
        sh "echo file dist-${jdk}/bin/jaybird-full-jdk${jdk}-${version}.jar jar full-jdk${jdk} >> artifacts"
        sh "echo file dist-${jdk}/esp/jaybird-esp-jdk${jdk}-${version}.jar jar esp-jdk${jdk} >> artifacts"
        sh "echo file dist-${jdk}/test/jaybird-test-jdk${jdk}-${version}.jar jar test-jdk${jdk} >> artifacts"
        sh "echo file dist-${jdk}/sources/jaybird-jdk${jdk}-${version}-sources.jar jar sources-jdk${jdk} >> artifacts"
        sh "echo file dist-${jdk}/javadoc/jaybird-jdk${jdk}-${version}-javadoc.jar jar javadoc-jdk${jdk} >> artifacts"
    }
    sh "echo end >> artifacts"
    
    utils.deployAndRegister(release_hub_project, version, wd+'/artifacts', env.BUILD_URL, vcs_url, 'jaybird', wd, '', '', branch)

    utils.defaultSuccessActions()
}

} // try
catch (any)
{
    utils.defaultFailureActions(any)
}
finally
{
    def body = utils.defaultEmailBody()
    def subject = "Job '${env.JOB_NAME}' (${version}~${rev}) - ${currentBuild.result}"

    body += "\n\nVCS: ${vcs_url}"
    if (currentBuild.result == 'SUCCESS' || currentBuild.result == 'UNSTABLE')
    {
        body += "\n\nBuild page: http://builds.red-soft.biz/release_hub/${release_hub_project}/${version}"
    }
    
    mail(to: utils.defaultEmailAddresses(),
         subject: subject,
         body: body);
}

def build(String jdk, archive_prefix, version_tag)
{
    node('jdk' + jdk + '&&linux')
    {
        stage 'Build JDK' + jdk
        
        deleteDir()
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
        withEnv(["JAVA_HOME=${java_home}", "archive_prefix=${archive_prefix}", "version_tag=${version_tag}", "jdk=${jdk}"]) {
            sh "cd ${archive_prefix} && ./build.sh -Dversion.tag.maven=-${version_tag} jars"
            sh "mkdir -p dist-${jdk}/{bin,esp,sources,javadoc,test}"
            sh "mv ${archive_prefix}/output/lib/jaybird-*javadoc* dist-${jdk}/javadoc"
            sh "mv ${archive_prefix}/output/lib/jaybird-*sources* dist-${jdk}/sources"
            sh "mv ${archive_prefix}/output/lib/jaybird-*test* dist-${jdk}/test"
            sh "mv ${archive_prefix}/output/lib/jaybird-*esp* dist-${jdk}/esp"
            sh "mv ${archive_prefix}/output/lib/* dist-${jdk}/bin"
        }
        
        stash includes: "dist-${jdk}/bin/**", name: "bin-${jdk}"
        stash includes: "dist-${jdk}/esp/**", name: "esp-${jdk}"
        stash includes: "dist-${jdk}/javadoc/**", name: "javadoc-${jdk}"
        stash includes: "dist-${jdk}/sources/**", name: "sources-${jdk}"
        stash includes: "dist-${jdk}/test/**", name: "test-${jdk}"
    }    
}