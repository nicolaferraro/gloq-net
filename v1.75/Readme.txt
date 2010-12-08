ABiNet 1.75

ABiNet is a tool that allows performing global alignement and querying of protein-to-protein (PPI) interaction networks.

Main authors: Nicola Ferraro, Luigi Palopoli, Simona Panni, Simona E. Rombo.

Main website: http://siloe.deis.unical.it/ABiNet

Requirements:
* Java Standard Edition (SE) JRE version 1.6 (1.6.0 or higher).
  You can download it from Oracle (http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Installation:
* Unzip the archive in a folder with write permissions.

Usage:
To launch the application, double click on the "ABiNet Launcher" icon if you are using windows, or execute the following command from the directory where the files have been unzipped to: java -Xmx1500m -jar ABiNet.jar.
The command line istruction should work with all operating systems.

Global Alignment
To perform global alignment of two networks follow the istructions below:
1) Go to the "Protein Networks" tab and click in the "Add.." button to load the network you want to use as master. The network must be provided as a tab-separated file where each line contains two protein identifiers.
2) Load the slave network by using the same procedure as described above.
3) Go to the "Preferences" panel. There are many settings you can change. For now, it's important that you set the similarity threshold that you want to use for the dictionary. Modify the preferences and press on the "Apply" button.
4) Go to the "Dictionary" tab. Here you're going to load the dictionary files. The files must be tab-separated files with each row containing two protein identifiers (each one belonging to a different input network) and a similarity value. You can add more than one dictionary file and they will be merged.
5) go to the "Global Alignment" panel and press the "Start.." button. Wait for the results (the procedure can be slow depending on the hardware of your computer) and then you will be allowed to save them in a file or to plot them. The procedure can be interrupted any time and you will be able to see the partial results.

Network Querying
To perform network querying, follow the procedure below. Refer to the Global Alignment section above if you need more details on the single operations.
1) Load a network from the "Protein Networks" tab. This is the network that you are going to query.
2) Modify the preference from the "Preferences" tab, especially the similarity threshold that you want to use.
3) Add one or more dictionary in the "Dictionary" tab.
4) Go to the "Querying" tab. Here you are allowed to draw a query in the upper side of the panel. Add some proteins and connect them, then change the identifiers of the proteins by right clicking on the circles (they should match with entries of the dictionary you have loaded) and clicking on "Properties...".
5) Press the "Query..." button and wait for the results. Each result will be presented in a different panel, you can select a result and press the "View" button to plot it, or press the "Save..." button to save all the results in a file.