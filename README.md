# libmatthew
Some libraries based on code from Matthew Johnson: http://www.matthew.ath.cx/projects/java/

Modifications:
- including precompiled native C libraries (libunix-java) for architectures: arm, amd64 and i386
- UnixSocket/UnixServerSocket now implements closable (allows try-with-resources usage)
- Cleaned up code style
- Split test classes to test classpath
- Introduced JUnit
- Removed the whole CGI package, it should never ever been used these days

