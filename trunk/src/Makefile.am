# Makefile.am for jtrain
# Guido Scholz
# last update: 06.10.2004

# echter Quellcode
srcjavafiles = \
	CBlContainer.java CBlock.java CBlockEditDialog.java \
	CBlockEvent.java CBlockEventHandler.java CBlockListener.java \
	CChain.java CChainEditor.java CChainEditorDialog.java \
	CChainExec.java CChainFrame.java CChainFrameDialog.java \
	CChainManipulator.java CCommand.java CCommandPipe.java \
	CControlCenter.java CFbElement.java CFeedbackportEvent.java \
	CFeedbackportHandler.java CFeedbackportListener.java CGb.java \
	CGbBlChangeDialog.java CGbBlockEditor.java CGbDialog.java \
	CGbEdit.java CGbEditor.java CGbElement.java \
	CGbExec.java CGbSignal.java CGbStreetDialog.java \
	CGbStreetEditor.java CGbSwitchEl.java CHelp.java \
	CInfo.java CInfoportHandler.java CLang.java \
	CLoco.java CLocoAccClock.java CLocoCommandDialog.java \
	CLocoControl.java CLocoControlDialog.java CLocoDialog.java \
	CLocoEdit.java CLocoEvent.java CLocoEventHandler.java \
	CLocoListener.java CMainFrame.java CMyKeyAdapter.java \
	CNode.java CPowerCommand.java CPropertyEditor.java \
	CS88Test.java CStep.java CStreet.java \
	CStreetDlg.java CTurnout.java CTurnoutCommandDialog.java \
	CTurnoutEditor.java CTurnoutEvent.java CTurnoutHandler.java \
	CTurnoutJob.java CTurnoutListener.java CTurnoutTest.java \
	CTurnoutTestDialog.java ExampleFileFilter.java JTrain.java

# generated soucecode (see configure.ac)
generatedjavafiles = config.java

# mit 'noinst' wird hier erreicht, dass die class-Dateien nicht
# installiert werden. Die java-Dateien werden über 'EXTRA_DIST' der
# Distribution zugeteilt
noinst_JAVA = $(srcjavafiles) $(generatedjavafiles)

CLASSES = $(noinst_JAVA:.java=.class)

jtrainjardir = @JARDIR@
jtrainjar_DATA = JTrain.jar

# generated (see configure.ac) wrapper script for jar file
nodist_bin_SCRIPTS = jtrain

CLEANFILES = $(jtrainjar_DATA) jtrain config.java

EXTRA_DIST = Manifest GPL.txt config.java.in jtrain.in jt11_32.gif \
	$(srcjavafiles)

JAVAROOT = $(srcdir)

AM_JAVACFLAGS = -deprecation

# force build of class files
$(CLASSES): classnoinst.stamp

# $(CLASSES) geht nicht wegen mehrerer Klassen pro java-Datei:
JTrain.jar: $(CLASSES)
	$(JAR) -cf $@ -m Manifest *.class jt11_32.gif

