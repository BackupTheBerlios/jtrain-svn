dnl configure.ac for jtrain

AC_PREREQ(2.57)
AC_INIT(jtrain, 0.1_gs01, [Guido Scholz <guido.scholz@bayernline.de>])
#AC_CONFIG_SRCDIR([src/java/JTrain.java])
AM_INIT_AUTOMAKE([dist-bzip2])

dnl AC_PROG_JAVAC
dnl AC_PROG_JAR
AC_PATH_PROG(JAVAC, javac, no)

if test "$JAVAC" = "no" ; then
  AC_MSG_ERROR(
    [cannot find java compiler 'javac', please check your installation.])
fi

AC_PATH_PROG(JAR, jar, no)

if test "$JAR" = "no" ; then
  AC_MSG_ERROR(
    [cannot find java packager 'jar', please check your installation.])
fi

### The following seems to be necessary because the replacement of
### the default value for prefix only happens in AC_OUTPUT (it would
### already be too late).
test "x$prefix" = xNONE && prefix=$ac_default_prefix

AC_ARG_WITH(icon-dir,
  [AC_HELP_STRING([--with-icon-dir=DIR],
    [directory for icons])],
  [ if test "x$withval" != "x" ; then
     ICONDIR="$withval"
    fi ],
  [ICONDIR="${prefix}/share/pixmaps"])
AC_SUBST(ICONDIR)

AC_SUBST(JARDIR, "${prefix}/share/java")

AC_ARG_WITH(doc-dir,
  [AC_HELP_STRING([--with-doc-dir=DIR],
    [directory for documentation (e.g. html files)])],
  [ if test "x$withval" != "x" ; then
     DOC_DIR="$withval"
    fi ],
  [DOC_DIR="${prefix}/share/doc"])
AC_SUBST(DOC_DIR)

HTMLDIR="$DOC_DIR/$PACKAGE/html"
AC_SUBST(HTMLDIR)


AC_CONFIG_FILES([Makefile
data/Makefile
doc/Makefile
icons/Makefile
src/Makefile])
AC_CONFIG_FILES([src/java/de/jtrain/config/Config.java])
AC_CONFIG_FILES([src/jtrain], [chmod +x src/jtrain])
AC_OUTPUT
