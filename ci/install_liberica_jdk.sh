#!/bin/bash
set -e
set -x

gpg --keyserver keys2.kfwebs.net --recv-keys 32e9750179fcea62
gpg --export -a 32e9750179fcea62 | sudo tee /etc/pki/rpm-gpg/RPM-GPG-KEY-bellsoft > /dev/null

echo | sudo tee /etc/yum.repos.d/bellsoft.repo > /dev/null << EOF
[BellSoft]
name=BellSoft Repository
baseurl=https://yum.bell-sw.com
enabled=1
gpgcheck=1
gpgkey=https://download.bell-sw.com/pki/GPG-KEY-bellsoft
priority=1
EOF

yum install -y bellsoft-java${JDK_VER}