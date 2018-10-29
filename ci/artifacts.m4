group ru.red-soft.jdbc

artifact jaybird-src VERSION
file dist-src/jaybird-VERSION.tar.gz tar.gz
file dist-src/jaybird-VERSION.zip zip
end

artifact jaybird-jdk18 VERSION
file dist/jdk18/bin/jaybird-VERSION.jar jar
file dist/jdk18/test/jaybird-test-VERSION.jar jar test
file dist/jdk18/sources/jaybird-VERSION-sources.jar jar sources
file dist/jdk18/javadoc/jaybird-VERSION-javadoc.jar jar javadoc
end

artifact jaybird-full-jdk18 VERSION
file dist/jdk18/bin/jaybird-full-VERSION.jar jar
file dist/jdk18/test/jaybird-test-VERSION.jar jar test
file dist/jdk18/sources/jaybird-full-VERSION-sources.jar jar sources
file dist/jdk18/javadoc/jaybird-VERSION-javadoc.jar jar javadoc
end

artifact jaybird-cryptoapi-jdk18 VERSION
file dist/jdk18/bin/jaybird-cryptoapi-VERSION.jar jar
file dist/jdk18/sources/jaybird-cryptoapi-VERSION-sources.jar jar sources
end

artifact jaybird-cryptoapi-security-jdk18 VERSION
file dist/jdk18/bin/jaybird-cryptoapi-security-VERSION.jar jar
file dist/jdk18/sources/jaybird-cryptoapi-security-VERSION-sources.jar jar sources
end

testpack jdk18
dir results/jdk18
end
